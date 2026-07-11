# AMS / ATMS 的应用侧调用链：一次 startActivity 如何穿过系统边界

## 0. 先分清 AMS 与 ATMS

Android 10 起，Activity/Task 相关职责从 AMS 中更明确地拆到 ATMS：

| 服务 | 主责 |
|---|---|
| `ActivityManagerService`（AMS） | 进程、Service、Broadcast、Provider、OOM Adj、ANR 等 |
| `ActivityTaskManagerService`（ATMS） | Activity 启动与生命周期、Task、Display/窗口容器、可见性与切换 |

二者同在 `system_server`，共享锁和内部接口进行协作。`ActivityManager` 和 `ActivityTaskManager` 则是应用进程的客户端门面，不是服务本体。

一次典型 Activity 启动：

```text
Activity.startActivity()
  → Instrumentation.execStartActivity()
  → ActivityTaskManager.getService()（IActivityTaskManager Proxy）
  ⇢ Binder ⇢ ATMS.startActivity(...)
  → ActivityStartController.obtainStarter()
  → ActivityStarter.execute()
  → 解析目标、权限/后台启动检查、Task 选择
  → ActivityRecord / Task 状态迁移
  → 若无进程：AMS + ProcessList + Zygote 创建进程
  → 若进程已就绪：ClientLifecycleManager.scheduleTransaction()
  ⇢ Binder ⇢ IApplicationThread.scheduleTransaction()
  → App 主线程执行 launch/resume
```

---

## 第一层：入口 API

### 1.1 Activity 入口

```text
ContextImpl.startActivity()
Activity.startActivity()/startActivityForResult()
  → Instrumentation.execStartActivity()
  → ActivityTaskManager.getService().startActivity(...)
```

`Instrumentation` 不只是测试类，它是应用侧组件创建和生命周期调用的重要钩子，也在这里执行 `Intent.migrateExtraStreamToClipData()`、`prepareToLeaveProcess()` 等离开进程前处理。

公开 API 的参数最终会扩展为：caller binder、callingPackage/featureId、Intent、resolvedType、resultTo、requestCode、flags、options、userId 等。system_server 不信任客户端解析结果，会重新基于调用 UID 做权限和可见性校验。

### 1.2 Service 入口

```text
ContextImpl.startServiceCommon()
  → ActivityManager.getService().startService(...)
  ⇢ AMS.startService()
  → ActiveServices.startServiceLocked()

ContextImpl.bindServiceCommon()
  → AMS.bindServiceInstance()/bindService()
  → ActiveServices.bindServiceLocked()
```

这里走 `IActivityManager`，不是 ATMS。Service 的启动、绑定、执行超时和前台服务策略都由 AMS/`ActiveServices` 主导。

### 1.3 Broadcast 入口

```text
ContextImpl.sendBroadcast()
  → ActivityManager.getService().broadcastIntentWithFeature(...)
  → AMS
  → BroadcastController / BroadcastQueue
```

现代 Android 对广播队列和缓存策略持续重构，但核心仍是：解析接收者、按权限/AppOps/用户状态过滤、选择并行或有序调度，再回调 `IApplicationThread`。

### 1.4 Provider 入口

```text
ContentResolver.acquireProvider()
  → ActivityThread.acquireProvider()
  → ActivityManager.getService().getContentProvider(...)
  → AMS.ContentProviderHelper
```

系统返回 `ContentProviderHolder`，其中包含 provider 的 Binder 接口和元数据。若目标进程未启动，调用者可能等待 provider 发布；这是跨进程 Provider 首次访问延迟高的重要原因。

### 1.5 系统服务代理如何取得

```java
ActivityManager.getService()       // IActivityManager
ActivityTaskManager.getService()   // IActivityTaskManager
```

底层通常通过 `Singleton<T>` 延迟从 `ServiceManager` 取得 Binder，再 `Stub.asInterface()` 转成接口。若 Binder 在本进程则可返回本地实现；应用访问 AMS/ATMS 时得到的是 Proxy。

---

## 第二层：关键类

### 2.1 应用侧代理层

| 类 | 职责 |
|---|---|
| `ContextImpl` | Context 公共 API 的真实实现和参数整理 |
| `Activity` | 附加 token/result/requestCode 等 Activity 上下文 |
| `Instrumentation` | Activity 启动出站钩子、组件创建与生命周期入站钩子 |
| `ActivityManager` | AMS 客户端门面，提供进程/任务/服务等公共 API |
| `ActivityTaskManager` | ATMS 客户端门面 |
| `IActivityManager.Proxy` | AMS 的 AIDL/Binder 代理 |
| `IActivityTaskManager.Proxy` | ATMS 的 AIDL/Binder 代理 |
| `ActivityThread.ApplicationThread` | system_server 回调应用的 `IApplicationThread.Stub` |

### 2.2 Activity 服务端启动流水线

| 类 | 职责 |
|---|---|
| `ActivityTaskManagerService` | Binder 入口、身份校验、全局 Activity/Task 协调 |
| `ActivityStartController` | 管理 `ActivityStarter`，批量/待处理启动 |
| `ActivityStarter` | 一次 start 请求的核心状态机：解析、检查、Task 与复用决策 |
| `RootWindowContainer` | 所有 Display/Task 等窗口容器的根 |
| `Task` | 任务栈/活动容器；组织 `ActivityRecord` |
| `ActivityRecord` | 服务端 Activity 状态 |
| `ActivityTaskSupervisor` | 调度真实启动、pause/resume、进程关联 |
| `ClientLifecycleManager` | 向应用安排 `ClientTransaction` |

### 2.3 目标进程不存在时

```text
ATMS 发现 ActivityRecord 无可用 app thread
  → AMS.startProcessAsync()/ProcessList.startProcessLocked()
  → Process.start()
  → ZygoteProcess.start()
  ⇢ zygote socket ⇢ fork 子进程
  → ActivityThread.main() / attachApplication()
  → AMS.attachApplicationLocked()
  → ATMS realStartActivityLocked()
  → scheduleTransaction(Launch + Resume)
```

这里存在一次关键“折返”：初次 start 请求进入 system_server，但目标进程尚不存在；进程 attach 回 AMS 后，系统才继续完成之前挂起的 `ActivityRecord` 启动。

### 2.4 Service 调度回应用

```text
ActiveServices
  → app.thread.scheduleCreateService()/scheduleServiceArgs()/scheduleBindService()
  ⇢ Binder ⇢ ApplicationThread
  → ActivityThread.H
  → handleCreateService()/handleServiceArgs()/handleBindService()
```

Service 对象依然在 App 主线程创建和回调。`onBind()` 返回的 `IBinder` 经 AMS 转交给客户端；后续服务方法调用是否在主线程，取决于该 Binder Stub 的实现与调用方式。

---

## 第三层：核心数据结构

### 3.1 `Intent`、`ResolveInfo` 与 `ActivityInfo`

- `Intent`：调用者表达“做什么”，可能显式也可能隐式。
- `ResolveInfo`：PMS 对匹配结果的包装。
- `ActivityInfo/ServiceInfo/ProviderInfo`：清单解析后的组件静态信息。

Intent 不是组件运行状态；`ActivityRecord` 才是一次 Activity 实例在服务端的状态载体。

### 3.2 Binder 身份与 token

关键 token：

- `IApplicationThread`：系统调用整个应用进程的客户端 Binder；
- Activity token：标识一个服务端 `ActivityRecord`，客户端用于关联 `ActivityClientRecord`；
- `resultTo`：把启动与结果接收方关联；
- Service connection binder：标识一次绑定关系；
- window token：允许窗口加入正确的窗口组，通常与 Activity 记录关联但概念不应完全等同。

### 3.3 Task/Activity 容器树

概念化结构：

```text
RootWindowContainer
  └─ DisplayContent
      └─ TaskDisplayArea
          └─ Task
              ├─ ActivityRecord A
              └─ ActivityRecord B (top)
```

实际层级还包括 `TaskFragment`、Display Area 策略和组织器。启动模式、flags、document mode、launchMode 的最终效果，本质是在这棵树上选择“复用哪个记录、清除哪些记录、放进哪个 Task”。

### 3.4 `ProcessRecord` 与 `WindowProcessController`

```text
AMS: ProcessRecord
  ├─ pid/uid/processName
  ├─ IApplicationThread
  ├─ services/providers/receivers
  └─ procState/adj

ATMS: WindowProcessController
  ├─ activities
  ├─ visible/hasTopUi
  └─ configuration/display state
```

拆分是职责隔离，不是两个进程。二者通过 controller/listener 和受控锁协作。

### 3.5 `ServiceRecord`、`BroadcastRecord`、`ContentProviderRecord`

它们分别是 system_server 对运行中服务、一次广播和已发布 Provider 的权威描述。应用侧真实 Java 实例则由 `ActivityThread` 的映射保存。跨进程系统设计常见模式就是“两端各有状态记录，中间用 Binder token 对齐”。

---

## 第四层：异常场景

### 4.1 Activity 启动失败

| 表现 | 常见系统决策 |
|---|---|
| `ActivityNotFoundException` | 无匹配 Activity，或包可见性导致查询/解析不可见 |
| `SecurityException` | 未导出、缺权限、跨用户、受保护 action、URI grant 不合法 |
| 后台启动无界面 | Background Activity Launch 限制拦截或延后 |
| 启动到旧实例 | `launchMode`、flags、Task 复用规则生效 |
| `TransactionTooLargeException` | Intent/options/result/state 超过 Binder 能承载的合理范围 |

### 4.2 调用方身份错用

Binder 服务端常用 `Binder.getCallingUid()/getCallingPid()` 做权限判断，清除身份后以 system_server 身份访问其他服务，再 `restoreCallingIdentity()`。漏恢复、过早清除或把客户端传来的包名当可信身份都会形成安全问题。

### 4.3 Binder 死亡

应用进程被杀后，system_server 持有的 `IApplicationThread` death recipient 收到通知，AMS 清理 `ProcessRecord`、组件和连接。反方向，system_server 重启通常意味着整机/Runtime 重启；普通 App 不应假设系统服务 Binder 永不死亡，框架代理会按各自策略重新获取或抛 `DeadObjectException`。

### 4.4 Service 后台限制

- 后台直接 `startService()` 可能抛 `IllegalStateException` 或被限制；
- `startForegroundService()` 后必须在规定窗口内调用 `startForeground()`；
- 前台服务类型、权限和后台启动豁免随 Android 版本收紧；
- `onStartCommand()` 返回值影响进程被杀后的重建语义，不保证永不被杀。

### 4.5 Provider 发布超时或死锁

调用者等待目标 provider 发布；目标进程初始化又同步访问调用者或争用相同锁，可能形成跨进程环路。Provider `onCreate()` 必须轻量，并避免在主线程执行不可控同步 IPC。

### 4.6 广播超时

有序广播接收者不 finish，或 `onReceive()` 主线程阻塞，会卡住队列并触发 ANR。`goAsync()` 只是把 finish 时点延后，仍需在系统预算内调用 `PendingResult.finish()`。

---

## 面试回答模板

> `startActivity()` 先在应用侧经过 Activity/ContextImpl 和 Instrumentation，随后通过 `IActivityTaskManager` Binder 进入 ATMS。ATMS 用 ActivityStarter 完成解析、权限与后台启动检查，并在 RootWindowContainer 中决定 Task 和 ActivityRecord。若目标进程不存在，AMS 通过 Zygote 拉起进程；进程的 ActivityThread 用 ApplicationThread attach 回 AMS。最后 system_server 通过 IApplicationThread 下发 ClientTransaction，应用 Binder 线程接收后切到主线程，由 TransactionExecutor 执行 Launch/Resume。系统侧是 ActivityRecord，客户端是 ActivityClientRecord，二者靠 token 对齐。

## 源码阅读锚点

- `frameworks/base/core/java/android/app/ContextImpl.java`
- `frameworks/base/core/java/android/app/Instrumentation.java`
- `frameworks/base/core/java/android/app/ActivityManager.java`
- `frameworks/base/core/java/android/app/ActivityTaskManager.java`
- `frameworks/base/core/java/android/app/IActivityManager.aidl`
- `frameworks/base/core/java/android/app/IActivityTaskManager.aidl`
- `frameworks/base/services/core/java/com/android/server/wm/ActivityStarter.java`
- `frameworks/base/services/core/java/com/android/server/am/ActiveServices.java`

