# InputDispatcher 到 View：一次触摸如何命中具体控件

## 0. 完整链路

```text
触摸屏/键盘驱动
  → EventHub 读取 /dev/input
  → InputReader 解析、校准、生成 NotifyMotion/NotifyKey
  → InputDispatcher 选择目标 Window
  → InputChannel（socketpair）
  ⇢ App 进程 WindowInputEventReceiver
  → ViewRootImpl.enqueueInputEvent()
  → InputStage 链（IME、兼容、合成等）
  → DecorView.dispatchTouchEvent()/dispatchKeyEvent()
  → Activity/Window/ViewGroup/View 分发
  → finishInputEvent() 回执
```

必须分清两个命中：InputDispatcher 基于窗口信息选择“哪扇 Window”；事件进入 App 后，`ViewGroup` 才在该 View 树内选择“哪个 View”。InputDispatcher 不知道 Button、RecyclerView 等业务控件。

---

## 第一层：入口 API

### 1.1 硬件事件入口

Linux input 驱动通过 `/dev/input/event*` 上报 `input_event`。native `EventHub` 用 epoll 监听设备节点，识别设备增删和原始事件；`InputReader` 根据设备配置与 mapper 把原始扫描码/坐标转成 Android 输入语义。

```text
EventHub.getEvents()
  → InputReader.loopOnce()
  → InputMapper.process(rawEvent)
  → InputReaderContext/Listener.notifyMotion|notifyKey()
  → InputDispatcher.notifyMotion|notifyKey()
```

### 1.2 窗口目标选择

InputDispatcher 主要依据 WMS 发布的 `InputWindowInfo`：

- display、可见性和窗口层级；
- touchable region、transform、scale；
- focusable、paused、trusted overlay 等 flags；
- owner pid/uid、application token；
- input config、dispatch timeout；
- 对应 InputChannel token。

触摸通常按坐标命中最上层可触窗口；按键通常发给 display 上聚焦窗口。焦点、触摸目标、IME target 是相关但不同的概念。

### 1.3 App 进程入口

`ViewRootImpl.setView()` 建立输入接收端后：

```text
WindowInputEventReceiver.onInputEvent(event)
  → ViewRootImpl.enqueueInputEvent(event, receiver, flags, true)
  → doProcessInputEvents()
  → mFirstInputStage.deliver(q)
```

如果事件来自 native channel，处理完成必须通过 `finishInputEvent(event, handled)` 回执，InputDispatcher 才能更新等待队列、触摸状态与 ANR 计时。

### 1.4 View 树入口

触摸：

```text
ViewPostImeInputStage
  → ViewRootImpl.processPointerEvent()
  → mView.dispatchPointerEvent()
  → DecorView.dispatchTouchEvent()
  → Window.Callback（通常 Activity）.dispatchTouchEvent()
  → PhoneWindow.superDispatchTouchEvent()
  → DecorView.superDispatchTouchEvent()
  → ViewGroup.dispatchTouchEvent()
  → child.dispatchTouchEvent()
  → View.onTouchListener / onTouchEvent()
```

按键：

```text
ViewRootImpl.processKeyEvent()
  → pre-IME / IME
  → DecorView/Activity.dispatchKeyEvent()
  → focused View / KeyEvent.DispatcherState
  → fallback / shortcut / unhandled key
```

---

## 第二层：关键类

### 2.1 native 输入系统

| 类 | 职责 |
|---|---|
| `InputManagerService` | system_server Java 服务，协调 WMS、输入设备、注入与 native input manager |
| `NativeInputManager` | Java IMS 与 native inputflinger 桥梁 |
| `EventHub` | 监听 `/dev/input`，产出 RawEvent |
| `InputReader` | 设备 mapper、坐标/按键转换、设备状态 |
| `InputDispatcher` | 选窗口、排队、分发、回执、焦点和 input ANR |
| `InputChannel` | 一对端点的 Java/native 句柄，底层传输输入消息 |

### 2.2 WMS 与窗口信息

WMS 掌握窗口树和 SurfaceControl，因此负责把最新 input window handles 提交给 InputDispatcher。窗口移动、layer、touchable region、焦点或 transform 变化后，都可能更新输入命中状态。

关键类包括 `WindowState`、`DisplayContent`、`InputMonitor`、`InputWindowHandle/InputWindowInfo`。不同 AOSP 版本更新路径会改，但职责边界稳定：WMS 产出窗口视图，InputDispatcher 消费并做分发。

### 2.3 ViewRootImpl InputStage

经典 stage 链可概括为：

```text
NativePreImeInputStage
  → ViewPreImeInputStage
  → ImeInputStage
  → EarlyPostImeInputStage
  → NativePostImeInputStage
  → ViewPostImeInputStage
  → SyntheticInputStage
```

每个 stage 返回 `FORWARD`、`FINISH_HANDLED`、`FINISH_NOT_HANDLED` 或 `DEFER`。IME 可异步处理并稍后继续；synthetic stage 可把轨迹球、手柄等转成兼容事件。

### 2.4 ViewGroup 触摸分发

一次手势序列从 `ACTION_DOWN` 开始：

1. ViewGroup 重置上次触摸目标；
2. `onInterceptTouchEvent()` 决定父容器是否拦截；
3. 未拦截则逆绘制顺序命中 child，建立 TouchTarget；
4. 后续 MOVE/UP 复用该目标，不是每次重新完整 hit-test；
5. 父容器中途拦截时，子 View 收到 `ACTION_CANCEL`，后续交父处理。

`OnTouchListener.onTouch()` 返回 true 会消费并阻止 `onTouchEvent()`；但整条手势归属还受 DOWN 是否被消费、父拦截和取消影响。

---

## 第三层：核心数据结构

### 3.1 `RawEvent`、`NotifyMotionArgs`、`MotionEntry`

```text
RawEvent：设备节点原始 type/code/value/time
NotifyMotionArgs：InputReader 输出的规范化动作、坐标、pointer、policy flags
MotionEntry：InputDispatcher 队列中的一次 Motion 事件
DispatchEntry：针对某个目标连接，应用 transform 后的分发记录
```

同一个逻辑事件可为多个目标生成不同 DispatchEntry，例如监视器、wallpaper、分屏或拆分触摸场景。

### 3.2 inbound / outbound / wait queue

- inbound queue：等待 InputDispatcher 选择目标的输入；
- connection outbound queue：等待写入某客户端 channel；
- wait queue：已发给客户端、等待 finish 回执的事件。

Input ANR 关注的并不只是“主线程某方法 5 秒”，而是目标窗口/应用未在分发预算内完成或系统找不到合适焦点窗口。

### 3.3 `MotionEvent`

核心字段：

- downTime/eventTime、actionMasked/actionIndex；
- pointer count、pointer id、tool type；
- 每个 pointer 的 coordinates/properties；
- source、deviceId、displayId、flags、classification；
- history 批次。

pointer index 会变，pointer id 才在一条手势内稳定。多指代码必须用 `getPointerId()` 与 `findPointerIndex()`，不能长期保存 index。

### 3.4 TouchTarget

`ViewGroup.TouchTarget` 是复用链表节点，保存 child 与该 child 接收的 pointerIdBits。它表达“当前手势哪些指针属于哪个子 View”，不是系统 InputDispatcher 的窗口 target。

### 3.5 坐标变换

InputDispatcher 按窗口 Surface transform 把显示坐标转到窗口；ViewGroup 再考虑 scroll、matrix、child left/top 将事件变换到子 View 局部坐标。窗口缩放、旋转、动画或 magnification 下不能假设 raw 坐标等于 View 局部坐标。

---

## 第四层：异常场景

### 4.1 Input dispatch ANR

常见原因：

- App 主线程执行长任务、锁等待或慢 Binder；
- 前一输入已发出但客户端没 finish；
- 目标进程没有 CPU 调度机会；
- GPU/渲染链阻塞间接拖住同步点；
- focused application 已设置但没有可聚焦窗口/首帧迟迟未建立。

堆栈抓取有延迟，ANR trace 中主线程已回到 `nativePollOnce` 不代表当时没卡住，应结合 ANR reason 和 Perfetto 时间线。

### 4.2 `ACTION_CANCEL`

系统焦点/窗口变化、父容器拦截、手势被其他消费者 pilfer、设备重置或策略取消，都可能产生 CANCEL。自定义 View 必须在 CANCEL 清除 pressed、velocity tracker、pointer 和动画状态，不能只处理 UP。

### 4.3 事件注入失败

`InputManager.injectInputEvent()` 受 `INJECT_EVENTS` 等系统权限和目标 UID 规则限制。普通应用不能任意给其他应用注入可信输入；无障碍、测试 instrumentation 走受控能力。

### 4.4 遮挡与 tapjacking

事件可带 partially/fully obscured 标志；敏感 View 可过滤被遮挡触摸。系统还会限制不可信 overlay 的穿透和最大不透明度。`FLAG_NOT_TOUCHABLE` 不等于事件必然安全落到下层应用。

### 4.5 手势冲突

RecyclerView/自定义父容器中常见：父过早 intercept、子未消费 DOWN、`requestDisallowInterceptTouchEvent()` 使用时机错误、多指 pointer index 错乱。按 DOWN→MOVE→CANCEL/UP 的完整序列记录日志，不要只看某一个 MOVE。

### 4.6 channel broken

窗口已移除或进程死亡，InputDispatcher 清理 connection；App 仍持有 receiver/旧窗口状态时可能出现 channel broken/disposed。通常是生命周期结果，不应盲目重建 channel，应该检查窗口泄漏与异步回调。

---

## 面试追问

### InputDispatcher 如何找到 Button？

找不到。它只根据 WMS 的 input window 信息选择目标窗口；Button 命中由 App 进程的 DecorView/ViewGroup 分发完成。

### 为什么子 View 收到 DOWN 后还会收到 CANCEL？

父容器可在后续 MOVE 决定拦截，或窗口/系统手势夺走序列。CANCEL 表示本次手势终止且不得执行 click 等完成语义。

## 源码阅读锚点

- `frameworks/native/services/inputflinger/reader/`
- `frameworks/native/services/inputflinger/dispatcher/`
- `frameworks/base/services/core/java/com/android/server/input/InputManagerService.java`
- `frameworks/base/services/core/java/com/android/server/wm/InputMonitor.java`
- `frameworks/base/core/java/android/view/ViewRootImpl.java`
- `frameworks/base/core/java/android/view/ViewGroup.java`

