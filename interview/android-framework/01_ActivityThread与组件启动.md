# ActivityThread 与组件启动：应用进程如何接住系统调度

## 0. 先给结论

`ActivityThread` 不是 Java `Thread`，而是一个应用进程的主线程运行时控制器。它在进程创建后完成主 Looper、`Application`、四大组件实例与资源环境的初始化，并通过 `ApplicationThread` 这个 Binder Stub 接收 `system_server` 的调度。

以 Activity 冷启动为主线：

```text
Launcher / Context.startActivity
  → ATMS 解析并创建 ActivityRecord
  → AMS 判断目标进程不存在，通知 Zygote fork
  → 新进程 ActivityThread.main()
  → ActivityThread.attach() 把 ApplicationThread 注册给 AMS
  → system_server 通过 IApplicationThread.scheduleTransaction()
  → ClientTransaction 投递到主线程
  → LaunchActivityItem → handleLaunchActivity()
  → performLaunchActivity() 创建 Activity
  → ResumeActivityItem → handleResumeActivity()
  → WindowManager.addView()，随后首帧 traversal
```

Activity 的“启动完成”至少有三种口径：实例和生命周期创建完成、窗口加入 WMS、首帧真正显示。面试时必须说明当前说的是哪一种。

---

## 第一层：入口 API

### 1.1 应用启动组件的入口

| 组件 | 常见入口 | 关键语义 |
|---|---|---|
| Activity | `Context.startActivity()`、`Activity.startActivityForResult()` | 需要 ATMS 做解析、任务栈和可见性决策 |
| Service | `startService()`、`startForegroundService()`、`bindService()` | AMS 管理创建、绑定、前台服务时限 |
| BroadcastReceiver | `sendBroadcast()`；清单静态广播；已注册动态广播 | AMS 广播队列选择接收者并调度 |
| ContentProvider | `ContentResolver.query()/insert()` 等 | 首次访问先向 AMS 获取 provider，必要时拉起进程 |

### 1.2 进程入口：`ActivityThread.main()`

典型动作顺序：

```text
ActivityThread.main()
  ├─ 初始化主线程环境
  ├─ Looper.prepareMainLooper()
  ├─ 创建 ActivityThread（字段初始化时已创建主 Handler H）
  ├─ attach(false, startSeq)
  │    └─ ActivityManager.getService().attachApplication(mAppThread, startSeq)
  └─ Looper.loop()
```

关键点：

- Zygote fork 后，子进程最终进入 `RuntimeInit`/反射入口并执行 `ActivityThread.main()`。
- `attach()` 把 `mAppThread`（`ApplicationThread`）交给 AMS，系统从此才拥有调度该进程组件的 Binder 句柄。
- `Looper.loop()` 之后，主线程依靠消息循环处理组件生命周期、输入、绘制和普通 Handler 消息。

### 1.3 系统回调应用的入口

现代版本主要通过：

```text
IApplicationThread.scheduleTransaction(ClientTransaction)
  → ActivityThread.ApplicationThread
  → ClientTransactionHandler.scheduleTransaction()
  → ActivityThread.H
  → TransactionExecutor.execute()
```

早期源码里常见 `scheduleLaunchActivity()`；现代 AOSP 把 Activity 生命周期操作封装成 `ClientTransactionItem`。回答时可以说历史方法名，但主线应以事务模型为准。

---

## 第二层：关键类

### 2.1 应用进程侧

| 类 | 职责 |
|---|---|
| `ActivityThread` | 组件实例化、生命周期执行、配置/资源更新、进程级运行时状态 |
| `ActivityThread.ApplicationThread` | `IApplicationThread.Stub`；接收 system_server Binder 调度，避免系统直接操作 Activity 对象 |
| `ActivityThread.H` | 主线程 Handler，把 Binder 线程收到的事务切回主线程 |
| `Instrumentation` | 创建 Activity/Application，包裹并调用生命周期，可用于测试/监控 |
| `LoadedApk` | 一个已加载 APK 在进程内的描述：类加载器、资源、Application、Receiver/Service 分发表 |
| `ContextImpl` | `Context` 的核心实现，连接资源、系统服务、文件目录和包身份 |
| `TransactionExecutor` | 按事务 item 和目标生命周期状态执行客户端事务 |
| `LaunchActivityItem` | 携带 Activity 启动所需客户端参数，并调用 `handleLaunchActivity()` |
| `ActivityClientRecord` | App 进程内一条 Activity 记录，连接 token、Activity 实例、配置和生命周期状态 |

### 2.2 system_server 侧

| 类 | 职责 |
|---|---|
| `ActivityTaskManagerService` | Activity、Task、Display Area、窗口容器和可见性 |
| `ActivityManagerService` | 进程、Service、Broadcast、Provider、OOM Adj 与 ANR 总控 |
| `ActivityTaskSupervisor` | Activity 启动与生命周期调度、进程就绪后的 realStart |
| `ActivityStartController/ActivityStarter` | 解析一次启动请求、复用/创建 Task、启动模式和权限检查 |
| `ActivityRecord` | system_server 中的 Activity 权威状态，不能与 `ActivityClientRecord` 混淆 |
| `WindowProcessController` | ATMS 看到的应用进程窗口/Activity 视图，与 AMS 的进程记录协作 |
| `ProcessRecord` | AMS 中进程权威记录，保存 pid、uid、组件、adj、`IApplicationThread` 等 |

### 2.3 Activity 实例化与生命周期链路

```text
TransactionExecutor
  → LaunchActivityItem.execute()
  → ActivityThread.handleLaunchActivity(r)
  → performLaunchActivity(r)
      ├─ LoadedApk.getClassLoader()
      ├─ mInstrumentation.newActivity()
      ├─ createBaseContextForActivity()
      ├─ activity.attach(...)
      │    ├─ 关联 ActivityThread、token、Application
      │    └─ 创建 PhoneWindow
      └─ mInstrumentation.callActivityOnCreate()
           └─ Activity.performCreate() → onCreate()
```

随后 `StartActivityItem`、`ResumeActivityItem`（具体事务组合随版本和场景变化）把实例推进到目标状态。`onResume()` 完成也不代表用户已经看到首帧；`handleResumeActivity()` 之后还要把 DecorView 加入窗口并完成一次 traversal。

### 2.4 其他组件如何落到 ActivityThread

```text
Service：CreateServiceData → handleCreateService()
  → LoadedApk classLoader → AppComponentFactory/Instrumentation 创建
  → ContextImpl → Service.attach() → onCreate()

Receiver：ReceiverData → handleReceiver()
  → 创建 Receiver → onReceive()
  → finishReceiver() 告知 AMS（有序广播尤其重要）

Provider：bindApplication() 阶段或首次按需安装
  → installContentProviders()/installProvider()
  → attachInfo() → onCreate()
```

Provider 常在 `Application.onCreate()` 之前安装，这是初始化框架依赖 Provider 时容易出现“启动过早”的根源。

---

## 第三层：核心数据结构

### 3.1 两端 Activity 记录

```text
system_server                         app process
ActivityRecord                        ActivityClientRecord
  token: Binder  ────────────────────  token: IBinder
  task / display                       activity: Activity
  state / visibility                   lifecycleState
  app: WindowProcessController         packageInfo: LoadedApk
```

Binder token 是两端关联同一个逻辑 Activity 的稳定身份。不能跨进程传 Activity Java 对象。

### 3.2 `mActivities`

`ActivityThread` 维护 token 到 `ActivityClientRecord` 的映射。它用于生命周期事务定位目标、配置变更、停止/销毁和状态保存。

### 3.3 `ClientTransaction`

可将它理解为“对某个应用客户端和 Activity token 的一批命令”：

```text
ClientTransaction
  ├─ target: IApplicationThread
  ├─ activityToken
  ├─ callbacks/items: LaunchActivityItem、NewIntentItem...
  └─ lifecycle request: ResumeActivityItem、PauseActivityItem...
```

新版本正在继续演化 transaction item 的组织方式，但核心目标不变：批量、可排序地把系统状态迁移请求送到客户端主线程。

### 3.4 `AppBindData`

`bindApplication()` 使用的进程初始化数据，常含：

- `ApplicationInfo`、进程名、providers；
- `LoadedApk`、`Configuration`、兼容信息；
- `ProfilerInfo`、调试/备份/自动化参数；
- 绑定限制和 SDK sandbox 等版本相关参数。

它最终驱动 `handleBindApplication()`：设置进程环境、创建 `Instrumentation`、安装 providers、创建并回调 `Application.onCreate()`。

### 3.5 `LoadedApk` 与类加载

一个进程可以加载主 APK、共享库或其他包上下文，因此不能简单认为进程只有一个 `LoadedApk`。组件创建依赖其 `ClassLoader`。动态特性、插件化、热修复或错误的 split 安装，常在这里表现为类找不到或资源不一致。

---

## 第四层：异常场景

### 4.1 冷启动卡住或启动 ANR

常见阻塞点：

- `ContentProvider.onCreate()`、`Application.onCreate()` 做磁盘/网络/锁等待；
- `Activity.onCreate()/onStart()/onResume()` 重活；
- 主线程等待 Binder，而 Binder 服务端又反向等待主线程，形成死锁；
- 类加载、dex2oat、资源加载或首次 page fault 过重。

排查不能只看 Activity 回调，要从进程 bind、provider 初始化一直看到首帧。

### 4.2 `TransactionTooLargeException`

`Intent` extras、`savedInstanceState`、Fragment 状态或 Activity result 过大，共享 Binder transaction buffer 被挤爆。它可能在 `startActivity()`、stop/save state 或返回 result 时出现，堆栈位置未必是数据产生处。

原则：Binder 传标识和小型结构，图片/大列表落文件、数据库或共享内存。

### 4.3 `ClassNotFoundException` / `Unable to instantiate activity`

常见原因：

- Manifest 类名错误或类被混淆/裁剪；
- split APK 未安装或 ClassLoader 路径不完整；
- 自定义 `AppComponentFactory`/插件 ClassLoader 使用错误；
- 构造器或静态初始化抛异常。

外围常被包装成 `RuntimeException: Unable to instantiate/start activity`，必须继续看最底层 cause。

### 4.4 生命周期状态错位

系统侧 `ActivityRecord` 与客户端执行存在 IPC 和消息队列延迟。快速旋转、返回、重启或多窗口切换时，旧事务可能在新状态请求后到达。事务执行器通过生命周期状态和 pre/post state 做校正；应用仍不应假设每次回调之间都有可见帧。

### 4.5 进程启动后立即死亡

- `Application`/Provider 初始化异常：进程 crash，AMS 可能按组件需求重启；
- native crash 或 watchdog；
- 内存压力下刚进入 cached 的进程被 lmkd 杀；
- 前台服务未在时限内调用 `startForeground()`，触发异常或 ANR（依版本策略）。

### 4.6 配置变化与进程重建

配置变化可能重建 Activity，但通常不重启进程；内存回收则可能连进程一起消失。恢复时不能依赖静态变量或单例仍存在，应使用 state、持久化数据和 ViewModel/SavedState 的正确边界。

---

## 面试追问

### ActivityThread 为什么叫 Thread 却不继承 Thread？

它代表“应用主线程的运行时管理者”。真正线程由进程启动链创建，`ActivityThread.main()` 在该线程上准备 Looper；类本身无需继承 `Thread`。

### Binder 回调为什么还要切到 H？

`ApplicationThread` 的 Binder 方法通常运行在 Binder 线程池，而组件生命周期、View 操作必须串行落在主线程。`H` 和客户端事务机制负责线程切换与顺序执行。

### Application 什么时候创建？

通常在 `handleBindApplication()` 中通过 `LoadedApk.makeApplicationInner()` 等路径创建，安装进程 providers 后再调用 `Application.onCreate()`。具体内部方法名会随版本调整。

### onResume 后为什么还可能白屏？

`onResume()` 只是生命周期推进。DecorView 加入 WMS、Surface 可用、measure/layout/draw、Buffer 提交和 SurfaceFlinger 合成都尚可能未完成。

## 源码阅读锚点

- `frameworks/base/core/java/android/app/ActivityThread.java`
- `frameworks/base/core/java/android/app/servertransaction/`
- `frameworks/base/core/java/android/app/LoadedApk.java`
- `frameworks/base/services/core/java/com/android/server/am/ActivityManagerService.java`
- `frameworks/base/services/core/java/com/android/server/wm/ActivityTaskManagerService.java`
- `frameworks/base/services/core/java/com/android/server/wm/ActivityRecord.java`
