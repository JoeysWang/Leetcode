# ANR 检测与 Perfetto 分析：从“超时”还原真正阻塞链

## 0. ANR 不是一个主线程定时器

不同系统模块各自为正在等待的操作计时：

```text
InputDispatcher 等窗口响应输入
ActiveServices 等 Service 生命周期执行完成
BroadcastQueue 等 Receiver / goAsync finish
ContentProviderHelper 等 Provider 发布/响应
JobScheduler 等 JobService 回调
前台服务控制器等 startForeground()
  → 超时后把 reason、目标进程和上下文汇入 AMS
  → ProcessErrorStateRecord / AnrHelper 诊断与记录
  → 抓栈、写 ANR trace、stats/dropbox、决定对话框或 kill
```

因此“ANR 就是主线程阻塞 5 秒”是错误答案。输入无响应的 AOSP 默认常见是 5 秒，但 Service、Broadcast、Job、Provider 等有各自预算，且前后台、CPU 是否饥饿、目标 SDK、设备/OEM 都可能影响时间。

---

## 第一层：入口 API

### 1.1 常见检测入口

| 类型 | 谁在等待 | AOSP/Pixel 常见默认口径 |
|---|---|---|
| Input dispatch | `InputDispatcher` 等目标窗口 ack/完成 | 通常约 5 秒 |
| No focused window | 已聚焦应用迟迟没有可接收按键的窗口 | 通常约 5 秒 |
| Service execute | `ActiveServices` 等 create/start/bind 完成 | 前台约 20 秒、后台约 200 秒，版本可变 |
| Broadcast | 广播队列等 receiver 返回或 `PendingResult.finish()` | 前后台预算不同，并可结合 CPU 时间调整 |
| FGS promotion | 启动 FGS 后等待 `startForeground()` | 常见约 5 秒，版本策略可变 |
| JobService | JobScheduler 等 `onStartJob/onStopJob` 快速返回 | 预算和显式 ANR 行为随版本演进 |
| Provider | AMS 等发布或响应 provider | 场景相关，不应死背统一数字 |

数字用于建立量级，不应作为跨版本契约。面试回答先说检测者、等待对象和完成信号，再给“典型默认值”。

### 1.2 应用侧可用诊断入口

```java
ActivityManager.getHistoricalProcessExitReasons(...)
  → ApplicationExitInfo.REASON_ANR
  → getTraceInputStream() // 设备/版本/权限允许时读取关联 trace
```

开发期工具：

```bash
adb shell ls /data/anr
adb bugreport bugreport.zip
adb shell dumpsys activity processes
adb shell dumpsys input
adb shell dumpsys window
```

`/data/anr/anr_*` 通常需要 root/userdebug 或通过 bugreport 间接取得。线上优先看 Play Console Android vitals 的 ANR cluster 与 `ApplicationExitInfo`。

### 1.3 Perfetto 采集入口

简单开发期采集可用：

```bash
adb shell perfetto -o /data/misc/perfetto-traces/anr.perfetto-trace \
  -t 20s sched freq idle am wm gfx view binder_driver
adb pull /data/misc/perfetto-traces/anr.perfetto-trace
```

复杂场景应使用 textproto 配置，至少关注：

- `sched_switch` / thread state / CPU frequency；
- Binder transaction；
- `am`、`wm`、`input`、`view`、`gfx` atrace categories；
- ftrace `binder/*`、`sched/*`，必要时 memory/LMKD；
- FrameTimeline/SurfaceFlinger（若与首帧或 GPU 相关）。

生产采集需控制 trace buffer、时长和隐私；Android 新版本可通过系统 tracing/`ProfilingManager` 等受控入口捕获。

---

## 第二层：关键类

| 类 | 职责 |
|---|---|
| native `InputDispatcher` | 维护输入 wait queue 与连接响应计时，通知策略层不可响应 |
| `InputManagerService` / WMS policy | 把 input ANR 与窗口/Activity 身份关联 |
| `ActiveServices` | Service 执行与 FGS 相关超时 |
| `BroadcastQueue` / modern broadcast queue | Receiver 调度、finish 与 timeout |
| `ContentProviderHelper` | Provider 获取、发布、连接等待 |
| `JobServiceContext` / JobSchedulerService | Job callback/运行状态与超时 |
| `ProcessErrorStateRecord` | 进程 crash/ANR 状态和 `appNotResponding()` 主流程 |
| `AnrHelper` / `AnrRecord` | 排队处理 ANR、降低 system_server 主锁/重复 ANR 压力 |
| `StackTracesDumpHelper` | 选择进程并抓 Java/native 栈，写 trace 文件 |
| `AppErrors` | 错误 UI、kill/等待/报告等策略协作 |

### 2.1 Input ANR 链路

```text
InputDispatcher
  → dispatch event to Connection
  → 放入 waitQueue，记录 timeout
  → 客户端未 finish / 无 focused window
  → onAnrLocked()/policy notify
  → IMS/WMS 找到 WindowState、ActivityRecord、pid
  → AMS appNotResponding(... reason ...)
```

“应用没有响应输入”可能因为它从未拿到 CPU、卡在上一条 Binder、没有可聚焦窗口或 GPU 同步阻塞，并不限定 `dispatchTouchEvent()` 自身慢。

### 2.2 Service / Broadcast 完成信号

Service 的组件回调由 App 主线程执行，ActivityThread 完成后通过 Binder 通知 AMS；广播同步 `onReceive()` 返回会 finish，有序/`goAsync()` 由 `PendingResult.finish()` 结束。计时覆盖冷启动时，慢 Provider/Application 初始化也会消耗同一预算。

### 2.3 ANR 抓栈不是触发瞬间

触发、排队、发信号、线程 dump 和写文件存在延迟。App 可能已恢复，最终主线程栈落在 `nativePollOnce`；也可能 dump 本身在系统高负载下失败。必须把 trace 栈当“采样证据”，与 reason、时间戳和 Perfetto 前史结合。

---

## 第三层：核心数据结构

### 3.1 ANR reason 与 process context

有效报告至少要回答：

- 发生时间、pid/uid/package/process；
- ANR type/reason（Input、Service、Broadcast 等）；
- parent/target Activity 或 Window；
- CPU load、memory pressure、system_server 状态；
- 主进程及相关进程线程栈；
- Binder/锁的对端。

只拿一段 main stack，不足以证明根因。

### 3.2 ANR trace 线程状态

Java trace 常见状态：

- `RUNNABLE`：可能真在跑，也可能 JNI/native 内可运行；
- `BLOCKED`：等待 Java monitor，查看 owner tid；
- `WAITING/TIMED_WAITING`：`wait/join/park/sleep`，继续找唤醒者；
- native 栈 `futex`、Binder ioctl、I/O 可揭示 Java 栈看不到的等待。

注意 Java `RUNNABLE` 与 Perfetto scheduler 的 Running/Runnable/Sleeping 不是同一层定义。

### 3.3 Perfetto thread state

定位主线程时间区间后区分：

```text
Running：正在 CPU 上执行 → 看 slice/调用逻辑和 CPU 时间
Runnable：想运行但没拿到 CPU → 看系统负载、优先级、抢占、频率
Sleeping/Blocked：等待 futex/Binder/epoll/I/O → 沿 waker 或 Binder flow 找对端
Uninterruptible sleep：常见内核 I/O 等待 → 看 block I/O/驱动
```

一段墙钟 5 秒可能只有几十毫秒 CPU，说明不是“算法跑了 5 秒”，而是调度或等待问题。

### 3.4 Binder transaction flow

Perfetto 中从 App main 的 `binder transaction` 沿 flow 到 system_server/其他进程 Binder thread，再看 reply：

- 对端 Running 很久：服务逻辑慢；
- 对端 Runnable 很久：CPU 饥饿；
- 对端又同步回调 App：潜在环路/死锁；
- 找不到 reply 且进程死亡：检查 DeadObject/LMKD；
- Binder 线程全部等待同一锁：线程池耗尽。

### 3.5 `ApplicationExitInfo`

它保存历史退出 reason、importance、pss/rss、description、timestamp、process state summary 等。App 可在运行时写 `setProcessStateSummary()` 作为下次重启后的业务线索，但数据大小受限，不能代替日志系统。

---

## 第四层：异常场景与分析方法

### 4.1 主线程长任务

ANR trace 显示业务计算/I/O，Perfetto 中主线程持续 Running 且 slice 覆盖超时窗口。修复是移出关键线程、拆分/取消、缓存或优化算法；仅增大线程池不会解决主线程上的工作。

### 4.2 锁竞争

main `BLOCKED`/futex，找到 lock owner；owner 可能在 I/O、低优先级 Runnable 或同步 Binder。修复要缩小锁范围、禁止持锁 I/O/IPC、调整数据所有权。只把 `synchronized` 换成 `ReentrantLock` 通常不解决结构问题。

### 4.3 慢 Binder

main 等 `BinderProxy.transactNative`，根因在远端。Perfetto 沿 flow 看 system_server/服务进程。若远端正常但很晚才被调度，属于系统负载/优先级；若持系统锁等待 App 回调，属于跨进程死锁。

### 4.4 No focused window / 首帧慢

Activity 已成为 focused app，但窗口未 add、不可 focus 或首帧/转场迟迟未完成。联合看 `dumpsys window`、WMS/Winscope、ActivityThread 启动和 ViewRoot traversal。不要只优化 `dispatchKeyEvent()`。

### 4.5 主线程 trace 是 `nativePollOnce`

三种常见解释：抓栈太晚、ANR 归因线程不是 main、系统整体调度异常。回到 ANR reason 和 Perfetto 触发前 5~20 秒；不能直接结论“系统误报”。

### 4.6 CPU 饥饿

主线程长时间 Runnable，不断被别的线程/进程抢占。检查应用是否开过多线程、实时/高优先级线程，设备是否 thermal throttling，system_server/媒体/GPU 是否高负载。优化线程数量和优先级，不要把所有工作都放 `Dispatchers.Default`/无限 executor。

### 4.7 线上偶发且无法复现

- 按 ANR reason、top frames、设备/版本聚类；
- 用 Play vitals/Crash 平台看发生率而非单例；
- 使用 `ApplicationExitInfo` 和轻量业务 trace marker；
- 在可控采样率下触发 Perfetto/ProfilingManager；
- 保留 Binder 对端、锁 owner、前后台/内存/thermal 信息。

---

## 一套可复述的 Perfetto 排查流程

1. 以 ANR timestamp/reason 定位超时终点，向前覆盖完整预算。
2. 找目标进程主线程，先按 Running/Runnable/Blocked 分类。
3. Running 看具体 slice 与 CPU；Runnable 看调度和系统负载；Blocked 沿 waker/锁/Binder flow。
4. 若是 input，检查 focused window、InputDispatcher wait 与首帧；若是组件，定位对应 Service/Receiver/Provider/Job 回调。
5. 同时看 system_server、Binder 对端、RenderThread/GPU、LMKD/内存，排除系统性问题。
6. 用代码、日志或二次 trace 验证假设，再决定修复；不要仅凭最后一张栈下结论。

## 源码与官方参考

- `frameworks/base/services/core/java/com/android/server/am/ProcessErrorStateRecord.java`
- `frameworks/base/services/core/java/com/android/server/am/AnrHelper.java`
- `frameworks/base/services/core/java/com/android/server/am/ActiveServices.java`
- `frameworks/native/services/inputflinger/dispatcher/`
- [官方 ANR 诊断](https://developer.android.com/topic/performance/anrs/diagnose-and-fix-anrs)
- [Perfetto 文档](https://perfetto.dev/docs/)

