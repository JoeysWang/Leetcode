# WindowManagerGlobal、ViewRootImpl、WMS：一棵 View 树怎样成为系统窗口

## 0. 三个对象先定位

| 对象 | 所在进程 | 管理范围 |
|---|---|---|
| `WindowManagerGlobal`（WMG） | App | 当前进程中所有已添加的顶层 View/ViewRootImpl |
| `ViewRootImpl`（VRI） | App | 一棵顶层 View 树与一个窗口的连接、traversal、输入、Insets |
| `WindowManagerService`（WMS） | `system_server` | 全系统窗口、焦点、层级、布局、Insets、转场和 Surface 元数据 |

`WindowManagerImpl` 是绑定到某个 Context/Display/默认 token 的轻量门面；真正的 App 进程级注册表是 WMG。WMS 不执行 `View.onDraw()`，ViewRootImpl 也不决定全系统 Z-order。

```text
Activity.setContentView()
  → PhoneWindow/DecorView（此时只是准备 View 树）

ActivityThread.handleResumeActivity()
  → WindowManagerImpl.addView(decor, attrs)
  → WindowManagerGlobal.addView()
  → new ViewRootImpl()
  → ViewRootImpl.setView()
  → IWindowSession.addToDisplayAsUser()
  ⇢ Binder ⇢ Session → WMS.addWindow()
  → WindowState 加入容器树，创建/关联 SurfaceControl
  → VRI requestLayout() → scheduleTraversals()
```

---

## 第一层：入口 API

### 1.1 添加窗口

典型入口：

- Activity：`WindowManager.addView()` 由框架在 resume 阶段调用；
- Dialog：`Dialog.show()` → `WindowManager.addView(decor, attrs)`；
- 悬浮窗：应用显式 `WindowManager.addView(view, TYPE_APPLICATION_OVERLAY...)`；
- Toast/系统 UI：根据版本由通知服务、Toast presenter 或系统进程加入特定窗口。

统一应用侧链路：

```text
WindowManagerImpl.addView()
  → WindowManagerGlobal.addView(view, params, display, parentWindow, userId)
  → root = new ViewRootImpl(context, display)
  → mViews.add(view); mRoots.add(root); mParams.add(params)
  → root.setView(view, params, panelParentView, userId)
```

WMG 先把三者加入列表，再调用 `setView()`；若 setView 失败会回滚。这也解释了异常堆栈常从 WMG/VRI 抛回 `Dialog.show()` 或 `addView()`。

### 1.2 更新窗口

```text
WindowManager.updateViewLayout(view, params)
  → WindowManagerGlobal.updateViewLayout()
  → ViewRootImpl.setLayoutParams()
  → requestLayout()/scheduleTraversals()
  → relayoutWindow()
  ⇢ WMS.relayoutWindow()
```

View 自身 `requestLayout()` 不等同于更新 WMS 的 LayoutParams；当窗口级尺寸、类型、flags、softInputMode 等改变时才需要相应窗口 relayout/属性同步。

### 1.3 移除窗口

```text
WindowManager.removeView()/removeViewImmediate()
  → WindowManagerGlobal.removeView()
  → ViewRootImpl.die(immediate)
  → doDie()
      ├─ dispatchDetachedFromWindow()
      ├─ IWindowSession.remove(mWindow)
      └─ WindowManagerGlobal.doRemoveView(root)
```

`removeView()` 通常把死亡处理排到正确线程/消息时点；`removeViewImmediate()` 更激进，但仍要求遵守线程与状态约束，不应作为泄漏补丁滥用。

### 1.4 traversal 入口

```text
View.requestLayout()/invalidate()
  → ViewRootImpl.requestLayout()/invalidateChildInParent()
  → scheduleTraversals()
  → Choreographer.postCallback(CALLBACK_TRAVERSAL, mTraversalRunnable)
  → doTraversal()
  → performTraversals()
      ├─ performMeasure()
      ├─ performLayout()
      └─ performDraw()
```

`scheduleTraversals()` 同时设置同步屏障，让异步的 VSYNC/输入相关消息在下一帧前有机会优先执行；重复请求会被 `mTraversalScheduled` 合并。

---

## 第二层：关键类

### 2.1 App 侧

| 类 | 职责 |
|---|---|
| `PhoneWindow` | Window 的主实现，创建 DecorView、管理 feature 和窗口属性 |
| `DecorView` | 一棵窗口 View 树的顶层 View |
| `WindowManagerImpl` | 带 Context/Display/默认 token 的客户端包装 |
| `WindowManagerGlobal` | 进程级单例，保存顶层 View、VRI、LayoutParams |
| `ViewRootImpl` | ViewParent 根实现；窗口 Binder 客户端；traversal 与输入连接中心 |
| `ViewRootImpl.W` | `IWindow.Stub`，WMS 向 App 通知 resize、focus、insets 等 |
| `IWindowSession` | App 到 WMS 的会话接口；一个进程通常共享 Session |
| `Surface` / `BLASTBufferQueue` | App 侧生产图形 buffer 的连接和现代 BufferQueue 提交路径 |

### 2.2 system_server 侧

| 类 | 职责 |
|---|---|
| `WindowManagerService` | 窗口全局策略和 Binder 服务 |
| `Session` | `IWindowSession.Stub`；代表一个客户端进程/UID 的窗口会话 |
| `WindowState` | 服务端一扇窗口的权威状态，关联 `IWindow`、token、attrs、Surface |
| `WindowToken` / `ActivityRecord` | 组织一组窗口并提供窗口合法身份；ActivityRecord 也是窗口容器 |
| `DisplayContent` | 一个 Display 上的窗口层级、焦点、Insets、布局状态 |
| `RootWindowContainer` | 所有 DisplayContent 的根 |
| `WindowSurfaceController` / `SurfaceControl` | 控制 SurfaceFlinger layer 的属性和事务 |
| `InsetsStateController` | 系统栏、IME 等 Insets source/target 管理 |

### 2.3 `setView()` 到 `addWindow()`

```text
ViewRootImpl.setView()
  ├─ requestLayout()
  ├─ 创建 InputChannel（按需）
  ├─ mWindowSession.addToDisplayAsUser(mWindow, attrs, ...)
  │    ⇢ Session.addToDisplayAsUser()
  │    → WMS.addWindow()
  │       ├─ 校验窗口类型、token、权限、display
  │       ├─ 创建 WindowState
  │       ├─ 加入 WindowToken/DisplayContent 容器
  │       ├─ 建立 input window 状态
  │       └─ 返回 insets/尺寸/控制信息
  ├─ 创建 WindowInputEventReceiver
  └─ assignParent()/dispatchAttachedToWindow()
```

窗口成功 add 不代表 Surface 已有有效 buffer，也不代表第一帧已合成。

### 2.4 双向 Binder

```text
App → WMS：IWindowSession
  addToDisplay、relayout、remove、finishDrawing、performHapticFeedback...

WMS → App：IWindow
  resized、insetsControlChanged、windowFocusChanged、dispatchAppVisibility...
```

回调到 `ViewRootImpl.W` 后通常只发送消息/封装参数，再切回创建 VRI 的线程处理，避免 Binder 线程直接触碰 View 树。

---

## 第三层：核心数据结构

### 3.1 WMG 三张并行表

```text
mViews[i]  = 顶层 View（通常 DecorView）
mRoots[i]  = 对应 ViewRootImpl
mParams[i] = 对应 WindowManager.LayoutParams
```

另有 `mDyingViews` 跟踪已请求移除但尚未完成 detach 的 View。它们是进程内窗口登记，不是系统窗口真相；系统真相在 WMS 的容器树。

### 3.2 `WindowManager.LayoutParams`

它既是 ViewGroup.LayoutParams，也是跨进程窗口协议的重要输入：

- `type`：应用、子窗口、IME、wallpaper、overlay 等；
- `flags/privateFlags`：焦点、触摸、截屏、布局等行为；
- `token`：窗口归属和合法性；
- `softInputMode`、gravity、x/y、width/height；
- format、alpha、动画、cutout/insets 相关字段。

客户端传值不代表 WMS 必然接受；WMS 会按 UID、权限、窗口类型和策略修正或拒绝。

### 3.3 窗口容器树

```text
RootWindowContainer
  └─ DisplayContent
      ├─ DisplayArea（分层策略）
      │   ├─ ActivityRecord/Task 下的 WindowState
      │   ├─ IME 容器
      │   └─ 系统装饰窗口
      └─ focus / insets / transition state
```

Z-order 不是简单按 addView 顺序；窗口类型、token、父子窗口、DisplayArea policy、Activity/Task 层级与转场共同决定。

### 3.4 Insets 数据

现代 Insets 模型关注：

- `InsetsState`：当前有哪些 source、frame、visible 状态；
- `InsetsSource`：状态栏、导航栏、IME、cutout 等来源；
- `InsetsSourceControl`：客户端可控制 source 时获得的 leash/位置；
- `WindowInsets`：View 层消费的不可用区域快照。

`fitsSystemWindows` 只是旧式入口；高级面试应能说明 edge-to-edge 下 Insets 是独立状态流，不只是一次 padding。

### 3.5 Surface 与 SurfaceControl

- `Surface` 偏向 buffer 生产端，应用锁 Canvas 或由 RenderThread/GPU 向它提交内容；
- `SurfaceControl` 是 layer 的控制句柄，设置位置、裁剪、alpha、层级和 reparent；
- WMS 主要持有/操作 `SurfaceControl` 元数据，应用获得可生产 buffer 的 Surface；
- SurfaceFlinger 消费 buffer 并按 layer metadata 合成。

---

## 第四层：异常场景

### 4.1 `WindowManager.BadTokenException`

常见原因：

- 使用已 finish/destroy Activity 的 token 显示 Dialog；
- 用 Application Context 添加应用窗口却没有合法 token；
- 窗口 type 与 token 类型不匹配；
- token 尚未注册或已经从 WMS 移除。

本质是 WMS 拒绝客户端窗口身份，不是“Dialog 样式错误”。

### 4.2 `WindowLeaked`

Activity 销毁后，WMG 仍登记其 DecorView/VRI，常因 Dialog/Popup/自建窗口未 dismiss/remove。框架在 Activity 清理时检查并打印最初 `addView()` 堆栈。正确修复是让窗口生命周期从属于宿主，而非吞异常。

### 4.3 `CalledFromWrongThreadException`

VRI 记录创建它的线程，`checkThread()` 发现其他线程发起影响 View 树的操作就抛异常。准确说法不是“所有 View 方法都绝对只能主线程调用”，而是 attach 后绝大多数会触及 VRI 的 UI 操作必须在其所属线程；应用窗口通常属于主线程。

### 4.4 `View not attached to window manager`

重复 remove、异步回调晚到、宿主已销毁或拿错 View 实例。先用明确的生命周期/幂等状态解决；`isAttachedToWindow()` 可防竞态，但不能替代正确所有权。

### 4.5 add 成功但黑屏/无首帧

逐层区分：

1. WMS 是否有 WindowState、窗口是否可见；
2. Surface 是否创建、尺寸是否为零；
3. VRI 是否完成 traversal；
4. RenderThread/GPU 是否提交 buffer；
5. SurfaceFlinger layer 是否有 buffer、被遮挡或 alpha 为零；
6. 转场 starting window 是否迟迟未被真实窗口替换。

### 4.6 relayout / resize 抖动

布局代码在 Insets 或尺寸变化时反复修改窗口属性，可能形成：WMS resize → App requestLayout → 更新 params → WMS 再 resize 的反馈环。用 Perfetto/Winscope 联合看 traversal、WMS state、SurfaceFlinger transaction，而不是只数 `onLayout()`。

### 4.7 InputChannel 异常

窗口移除后仍投递输入、客户端卡死或 channel broken，会出现 input channel disposed/broken。InputDispatcher 依赖 WMS 发布的 input window handle 决定目标；可见 View 不一定拥有输入焦点。

---

## 面试追问

### 一个 Activity 有几个 ViewRootImpl？

Activity 主 Window 通常一个；但同进程 Dialog、PopupWindow、悬浮窗各自可有独立顶层 ViewRootImpl。不能说“一个 Activity 进程只有一个 VRI”。

### `setContentView()` 时窗口加到 WMS 了吗？

通常没有。它主要构造 PhoneWindow 的 DecorView 内容。Activity resume 的 `handleResumeActivity()` 才通过 WindowManager 添加 DecorView。

### WMS 负责绘制吗？

不负责应用 View 的 measure/layout/draw。WMS 管窗口策略、状态和 Surface layer 元数据；应用 UI 管线产出 buffer，SurfaceFlinger 合成。

## 源码阅读锚点

- `frameworks/base/core/java/android/view/WindowManagerGlobal.java`
- `frameworks/base/core/java/android/view/WindowManagerImpl.java`
- `frameworks/base/core/java/android/view/ViewRootImpl.java`
- `frameworks/base/core/java/android/view/IWindowSession.aidl`
- `frameworks/base/services/core/java/com/android/server/wm/WindowManagerService.java`
- `frameworks/base/services/core/java/com/android/server/wm/WindowState.java`
- `frameworks/base/services/core/java/com/android/server/wm/DisplayContent.java`

