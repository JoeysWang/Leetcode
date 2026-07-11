# OOM Adj 与后台进程管理：谁决定进程的重要性，谁真正执行回收

## 0. 三段式结论

```text
AMS 根据组件、可见性和依赖图计算 procState / capability / oom_adj
  → 把 oom_score_adj 写给内核，并把进程状态同步给 lmkd/调度策略
  → lmkd 监控 PSI/内存压力，选择可牺牲进程并 kill
```

“进入后台就被杀”不准确：后台只是影响重要性的信号之一；cached 进程可能长期留在内存，也可能被冻结或在压力下很快被杀。`onTrimMemory()` 不是死亡预告，`onDestroy()` 更不是进程退出保证。

---

## 第一层：入口 API

### 1.1 应用行为如何触发重算

没有供普通 App 直接设置 OOM Adj 的公开 API。下列状态变化会间接触发 AMS 更新：

- Activity resume/pause/stop、窗口可见性变化；
- Service start/bind/unbind、前台服务状态；
- Provider 被客户端持有或释放；
- Broadcast/backup/instrumentation 等临时执行；
- 进程间 Service connection 依赖；
- Job、音频、location 等能力与系统策略变化；
- 进程创建、死亡、UID idle/active。

### 1.2 应用可观察入口

```java
ActivityManager.getMyMemoryState(runningAppProcessInfo);
ActivityManager.getRunningAppProcesses(); // 可见性受限，结果不保证完整
onTrimMemory(level);
ApplicationExitInfo                   // Android 11+ 查询历史退出原因
```

这些是观察或回调，不是可靠保活手段。进程优先级是系统全局资源决策，App 不能通过空 Service、1 像素 Activity 等技巧合法固定高优先级。

### 1.3 系统更新入口

版本间入口会重构，稳定概念是：

```text
AMS.updateOomAdjLocked(reason)
  → OomAdjuster.updateOomAdjLocked(...)
  → 计算进程依赖图/状态
  → applyOomAdjLSP()/ProcessList.setOomAdj(...)
  → 写 /proc/<pid>/oom_score_adj / 通知 lmkd
```

新版本可能使用更现代的批量/图算法实现，面试不要过度绑定某个私有方法名。

### 1.4 lmkd 入口

用户态 `lmkd` 使用 PSI 等内核信号监控 memory pressure、thrashing、swap 情况；达到策略阈值时，在符合 `oom_score_adj` 范围的进程中挑选牺牲者。AMS 给出“可杀优先级”，lmkd 在真实压力下做 kill 决策，二者不是同一个模块。

---

## 第二层：关键类

| 类 | 职责 |
|---|---|
| `ActivityManagerService` | 进程和组件全局状态、触发 OOM Adj 更新 |
| `OomAdjuster` | 计算进程重要性、依赖传播、应用结果 |
| `ProcessList` | 进程启动/列表、adj 常量、与 lmkd/内核接口 |
| `ProcessRecord` | 一个应用进程的权威记录 |
| `ProcessStateRecord` | procState、adj、capability、调度组等计算/缓存状态 |
| `ProcessServiceRecord` | 进程运行/绑定 Service 及 connection 关系 |
| `UidRecord` | UID 聚合状态，多个进程可共享一个 UID |
| `CachedAppOptimizer` | cached 进程 freezer、compaction 等优化 |
| `lmkd` | native daemon，压力监控和最终 kill |

### 2.1 重要性大致层级

从难杀到易杀，可用心智顺序：

```text
system / persistent
  → top app（当前 resumed/交互）
  → foreground（正在接收广播、执行服务回调等）
  → visible（可见但非 top）
  → perceptible / foreground service
  → service / home / previous（受版本策略影响）
  → cached activity/client/empty
```

这不是一张固定一一映射表。`procState`、`oom_adj`、调度组和 capability 是不同维度，版本和设备策略会调整阈值。

### 2.2 依赖提升

进程 A 绑定进程 B 的 Service，如果 A 很重要，B 往往需要沿 connection 被提升，避免杀掉 B 让 A 立即失败。提升程度由 bind flags（如 important、above client、waive priority）、组件状态和周期依赖规则决定。

Provider 客户端依赖、instrumentation、receiving broadcast 等也会改变宿主进程的重要性。OOM Adj 计算因此是依赖图问题，不只是查 Activity 生命周期。

### 2.3 Cached Apps Freezer

支持的设备上，cached 进程可迁入 freezer cgroup：保留内存但暂停所有线程，减少后台 CPU。Android 14+ 的典型实现会在进 cached 后延迟冻结，并在生命周期事件到来时解冻。

冻结不是 kill：静态对象和堆仍在；但线程、Timer、GC 都不运行。解冻后 fixed-rate 定时任务可能扎堆执行，网络连接也可能已被系统处理。后台设计不能依赖 cached 进程持续运行。

---

## 第三层：核心数据结构

### 3.1 `oom_score_adj`

Linux 暴露 `/proc/<pid>/oom_score_adj`，范围通常为 `-1000..1000`：值越大越容易成为 OOM/低内存牺牲者。Android 的 `ProcessList` 定义诸如 persistent、foreground、visible、perceptible、service、cached 等 adj 档位。

不要把旧 `oom_adj` 的 `-17..15` 范围与现代 `oom_score_adj` 混用；很多口头资料仍沿用“OOM Adj”作为总称。

### 3.2 `procState`

`ActivityManager.PROCESS_STATE_*` 描述进程当前组件/用户感知状态，粒度通常比 adj 更细。它用于后台执行限制、网络策略、冻结、统计等，不只是 lmkd 排序。

### 3.3 capability

进程即使处于某个 procState，也不必然拥有所有敏感能力。capability 进一步表达能否使用特定 while-in-use 权限或前台能力，避免单靠“进程看起来前台”放大权限。

### 3.4 adj source / target

ProcessStateRecord 会记录当前 adj/状态由哪个组件或依赖提升而来。排查“为什么这个进程杀不掉/优先级异常”时，`dumpsys activity processes` 中的 adjType、source、target 比只看最终数字更有价值。

### 3.5 LRU 进程列表

AMS 维护进程 LRU 次序，并结合 Activity/Service 等分区与最近使用情况组织 cached 牺牲顺序。lmkd 也会综合内存占用、adj 和策略选 victim；“LRU 最老必定先杀”是过度简化。

---

## 第四层：异常场景

### 4.1 前台服务也被杀

FGS 提高用户感知与进程重要性，但不是不可杀。极端内存、用户强停、系统策略、崩溃、FGS 违规或 OEM 管控都可能终止。持久任务必须可恢复、幂等并保存进度。

### 4.2 绑定导致意外保活

长生命周期前台客户端绑定远程 Service，连接 flags 传播优先级，可能让大进程长期保持高 adj、造成内存压力。及时 unbind，避免用强绑定把“缓存”伪装成必要依赖。

### 4.3 cached frozen Binder 问题

解绑后仍保存远程 Binder 并同步调用一个已冻结进程，现代系统可能为防调用者无限阻塞而终止 frozen server；大量 oneway 发往 frozen 进程还可能填满异步缓冲。解绑后应立即丢弃旧 Binder 引用。

### 4.4 `onTrimMemory()` 误用

不能指望系统在 kill 前一定回调某个 level；冻结期间进程也不能运行回调。正确策略是缓存可随时重建、关键数据及时持久化、根据明确内存信号主动缩减，而不是等“最后通知”。

### 4.5 进程被杀后的状态恢复

用户从最近任务回到应用时，Task/ActivityRecord 可能仍由系统记录，但 Linux 进程和所有单例已消失。Activity 通过 saved state/持久层重建。若依赖自定义 Application 单例中未持久化的数据，就会出现只在“后台久置后”复现的空状态崩溃。

### 4.6 厂商后台策略

OEM 电量/自启动策略可能在 AOSP LMKD 之外主动限制或杀进程。排查需要区分：`ApplicationExitInfo` reason、lmkd 日志、AMS force-stop、crash、用户操作与 OEM daemon。不能把所有后台死亡都归因于 OOM。

---

## 常用诊断

```bash
adb shell dumpsys activity processes
adb shell dumpsys activity oom
adb shell cat /proc/<pid>/oom_score_adj
adb shell dumpsys meminfo <package>
adb shell logcat -b all | grep -iE 'lmkd|lowmemorykiller|am_kill'
```

不同版本 `dumpsys` 子命令和权限可能变化；重点看 procState、adj、adjType/source、是否 cached/frozen、最后 kill reason。

## 面试追问

### AMS 会直接在低内存时杀 App 吗？

AMS 维护进程状态并计算/下发重要性；现代主路径是用户态 lmkd 根据 PSI/内存压力和 adj 选 victim。AMS 也会因 cached 上限、force-stop、异常等主动终止进程，不能把所有 kill 都说成 lmkd。

### cached 与 frozen 有什么区别？

cached 是进程重要性/可回收状态；frozen 是一种执行控制优化。cached 进程可能未冻结，frozen 进程仍占内存且之后可解冻。

## 源码阅读锚点

- `frameworks/base/services/core/java/com/android/server/am/OomAdjuster.java`
- `frameworks/base/services/core/java/com/android/server/am/ProcessRecord.java`
- `frameworks/base/services/core/java/com/android/server/am/ProcessStateRecord.java`
- `frameworks/base/services/core/java/com/android/server/am/CachedAppOptimizer.java`
- `system/memory/lmkd/`
- [AOSP lmkd 文档](https://source.android.com/docs/core/perf/lmkd)
- [AOSP Cached Apps Freezer](https://source.android.com/docs/core/perf/cached-apps-freezer)

