# Android Framework 高级面试专题

> 面向“无需直接修改 Framework，但必须讲清系统逻辑”的 Android 高级开发岗位。

## 阅读约定

- 主线以 Android 14/15 AOSP 的稳定架构为基线；不同版本类名或职责明显变化时单独标注。
- 每篇都使用同一套四层模型：**入口 API → 关键类 → 核心数据结构 → 异常场景**。
- “应用侧”指 App 进程；“系统侧”通常指 `system_server`；图形合成还涉及 `SurfaceFlinger`，输入还涉及 `InputReader/InputDispatcher`。
- 源码路径均相对 AOSP 根目录。厂商 ROM 会修改策略，但主干职责和跨进程边界通常不变。

## 总体心智模型

```text
App 公开 API
  ↓ Java/Kotlin 调用
App Framework 客户端（Manager / Singleton / ViewRootImpl）
  ↓ Binder IPC
system_server 服务（AMS / ATMS / WMS / PMS / AppOps）
  ↓ native daemon / kernel / SurfaceFlinger
Linux 进程、调度、内存、输入与图形系统
```

面试回答一条 Framework 链路时，先说清四个问题：

1. 谁是调用发起方，公开入口是什么？
2. 第一次 Binder 跨进程发生在哪里？回调又从哪里返回 App？
3. 系统保存状态的核心对象是什么，谁持有谁？
4. 失败时表现为异常、超时、丢帧、ANR，还是进程被杀？

## 专题目录

| # | 专题 | 一句话抓手 |
|---:|---|---|
| 1 | [ActivityThread 与组件启动](01_ActivityThread与组件启动.md) | `ActivityThread` 是应用主线程运行时，不是 Thread 子类 |
| 2 | [AMS / ATMS 的应用侧调用链](02_AMS_ATMS应用侧调用链.md) | AMS 管进程与组件，ATMS 管 Activity/Task/Display |
| 3 | [WindowManagerGlobal、ViewRootImpl、WMS](03_WindowManagerGlobal_ViewRootImpl_WMS.md) | WMG 管本进程窗口，VRI 管一棵 View 树，WMS 管全系统窗口 |
| 4 | [Binder Java 层](04_Binder_Java层.md) | Proxy 发起、Stub 分发、线程池执行、Parcel 承载 |
| 5 | [Handler、MessageQueue、Choreographer](05_Handler_MessageQueue_Choreographer.md) | 消息调度与帧调度共用主 Looper，但不是一回事 |
| 6 | [View 渲染和 Surface 基础](06_View渲染与Surface基础.md) | View 产出绘制命令/像素，Surface 承载 Buffer，SF 负责合成 |
| 7 | [InputDispatcher 到 View](07_InputDispatcher到View.md) | 系统选窗口，应用在 View 树内命中与分发 |
| 8 | [OOM Adj 与后台进程管理](08_OOMAdj与后台进程管理.md) | AMS 算重要性，内核暴露 score，lmkd 在压力下执行回收 |
| 9 | [ANR 检测和 Perfetto 分析](09_ANR检测与Perfetto分析.md) | 不同事件由不同 watchdog 计时，最终汇入 ANR 诊断 |
| 10 | [PMS、权限、AppOps 基础](10_PMS_权限_AppOps基础.md) | PMS 回答“装了什么/声明什么”，权限与 AppOps 决定“能否做” |

## 建议学习顺序

```text
组件启动（1、2）
    ↓
窗口、消息、渲染（3、5、6）
    ↓
输入闭环（7）
    ↓
IPC 底座（4，可提前穿插）
    ↓
进程治理与故障分析（8、9）
    ↓
包和安全模型（10）
```

## 官方参考

- [AOSP Framework 源码](https://android.googlesource.com/platform/frameworks/base/)
- [Android 平台架构](https://source.android.com/docs/core/architecture)
- [SurfaceFlinger 与 WindowManager](https://source.android.com/docs/core/graphics/surfaceflinger-windowmanager)
- [lmkd](https://source.android.com/docs/core/perf/lmkd)
- [Perfetto 官方文档](https://perfetto.dev/docs/)

