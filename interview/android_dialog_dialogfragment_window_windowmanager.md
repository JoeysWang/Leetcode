# Android Dialog、DialogFragment、Window 与 WindowManager 关系梳理

> 本文中的 “fragmentdialog” 按 AndroidX `DialogFragment` 理解。讨论对象以平台 `Dialog`、AndroidX `DialogFragment` 和常规应用窗口为主。`AppCompatDialog`、`AlertDialog`、`MaterialAlertDialog` 会增加兼容层、主题覆盖和背景 inset，但底层仍然进入同一套 `Window` / `WindowManager` 链路。

## 1. 先建立一个准确的心智模型

一句话概括：

- `DialogFragment` 管理 Fragment 状态、FragmentManager 事务和 Dialog 的创建/销毁。
- `Dialog` 表达“弹窗”的行为语义，例如 show、hide、dismiss、cancel 以及相应回调。
- `Window` 是一个顶层窗口的客户端抽象，通常实现为 `PhoneWindow`，持有 `DecorView` 和 `WindowManager.LayoutParams`。
- `WindowManager` 是应用进程中的客户端入口；真正统一管理窗口的是 system_server 中的 `WindowManagerService`，简称 WMS。
- `ViewRootImpl` 把一棵 View 树接到窗口系统，负责 traversal、输入分发入口、Insets 协调和与 WMS 的 IPC。
- `SurfaceFlinger` 最终合成各个窗口对应的 Surface；WMS 不负责绘制应用 View。

关系图如下：

```text
Host Activity / FragmentManager
│
├── Activity
│   └── PhoneWindow
│       └── DecorView
│           └── ViewRootImpl
│               └── Activity Surface
│
└── DialogFragment
    └── Dialog / AppCompatDialog / AlertDialog
        └── PhoneWindow
            └── DecorView
                └── ViewRootImpl
                    └── Dialog Surface

WindowManagerImpl
    └── WindowManagerGlobal
        └── IWindowSession / Binder
            └── WindowManagerService
                ├── Window token、窗口类型和权限校验
                ├── 层级、位置、尺寸、Insets、焦点、IME、输入区域
                ├── 窗口可见性和窗口动画
                └── SurfaceControl
                    └── SurfaceFlinger 合成显示
```

最关键的一点是：

**Dialog 的内容通常不在 Activity 的 `DecorView` 里面。Activity Window 和 Dialog Window 是两棵独立的 View 树、两个 `ViewRootImpl`，通常也对应两个独立 Surface。**

它们之所以表现得像父子，是因为 Dialog Window 使用了宿主 Activity 相关的窗口 token，并受到 WMS 的同一应用窗口组、层级、焦点和输入策略约束，而不是因为 Dialog View 是 Activity View 的子节点。

---

## 2. 每个对象具体负责什么

| 对象 | 核心职责 | 不应把它理解成什么 |
|---|---|---|
| `DialogFragment` | 用 FragmentManager 管理 Dialog；参与状态保存、重建、返回栈和宿主生命周期 | 它不是 `Dialog` 的子类，也不直接代表一个 Window |
| `Dialog` | 提供 show、hide、dismiss、cancel、内容设置和监听器；内部持有一个 Window | 它不只是覆盖在 Activity 上的一层 View |
| `Window` | 保存顶层窗口属性、Feature、DecorView；把应用请求转换成窗口参数 | 它不是 WMS 中的服务端窗口对象，也不直接绘制内容 |
| `DecorView` | Window 的根 View，承载标题栏、内容区域、系统栏相关布局等 | 它不等于业务布局本身；业务布局通常在其 content 容器内 |
| `WindowManager` | 客户端 add、update、remove Window View 的接口 | 它不是 system_server 中真正的全局窗口管理器 |
| `ViewRootImpl` | 连接 View 树和窗口系统；执行 measure/layout/draw traversal；处理输入、Insets 和 IPC | 它不是普通 View，也不会出现在布局层级 XML 中 |
| `WindowManagerService` | 服务端仲裁窗口 token、类型、权限、层级、焦点、可见性、Insets、动画和 Surface | 它不执行应用 View 的 onDraw |
| `SurfaceFlinger` | 把各窗口 Surface 的 buffer 做最终合成 | 它不了解 DialogFragment 或业务 View 生命周期 |

### 2.1 DialogFragment 与 Dialog 的关系

`DialogFragment` 通常在 `onCreateDialog()` 中创建或返回一个 `Dialog`：

```kotlin
override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return Dialog(requireContext(), theme)
}
```

两者是“管理者和被管理对象”的关系，而不是继承关系。

`DialogFragment` 还可以不以弹窗方式显示，而是作为普通 Fragment 放进容器。是否显示为 Dialog，取决于 `showsDialog`、是否添加到容器以及 Fragment 内部状态。也因此：

- 以 Dialog 方式显示时，动画主要由 Dialog Window 控制。
- 作为普通 Fragment 嵌入容器时，Fragment transaction animation / transition 才是主要动画机制。

### 2.2 Dialog 与 Window 的关系

一个常规 `Dialog` 内部会创建并持有一个 `Window`，平台实现通常是 `PhoneWindow`。调用：

```kotlin
dialog.setContentView(layout)
```

本质上会继续委托给其 Window，把内容放入 Window 的 DecorView 内容区域。

调用：

```kotlin
dialog.show()
```

最终会把 Dialog Window 的 DecorView 交给 WindowManager 注册。只有注册之后，它才进入 WMS 管理的窗口体系。

---

## 3. Dialog 显示时到底发生了什么

以 `DialogFragment.show()` 为入口，典型链路如下：

```text
DialogFragment.show(fragmentManager, tag)
    │
    ├── 提交 FragmentTransaction
    │
    ├── Fragment onCreate / DialogFragment 内部准备 Dialog
    │       └── onCreateDialog(savedInstanceState)
    │
    ├── DialogFragment.onStart()
    │       └── Dialog.show()
    │           ├── 第一次 show 时触发 Dialog.onCreate()
    │           ├── Dialog.onStart()
    │           ├── 获取或创建 DecorView
    │           ├── WindowManager.addView(decorView, layoutParams)
    │           └── 发送 OnShowListener
    │
    ├── ViewRootImpl 安排第一次 traversal
    │       ├── measure
    │       ├── layout
    │       └── draw
    │
    └── WMS 使窗口可见并执行 enter animation
            └── SurfaceFlinger 合成第一帧及后续动画帧
```

几个必须区分的时刻：

1. `Dialog.onStart()` 执行时，DecorView 通常还没有完成第一次 measure/layout。
2. `OnShowListener` 被调用时，`addView()` 已经发起，但不代表第一帧已经显示，也不代表 enter animation 已结束。
3. `DialogFragment.showNow()` 只意味着 Fragment 事务同步执行，不意味着窗口第一帧或动画同步完成。
4. Window enter animation 通常在 WMS 接受窗口并使其可见后开始，时机晚于单纯的 Java/Kotlin `show()` 方法调用。

---

## 4. 生命周期梳理

## 4.1 Dialog 自身的状态变化

典型 AOSP 行为可以整理为：

| 操作 | Window/View 状态 | 关键回调 |
|---|---|---|
| 构造 `Dialog` | 仅有对象和 Window，尚未添加到 WMS | 不触发可见生命周期 |
| 第一次 `show()` | DecorView 被加入 WindowManager | `onCreate()` 一次、`onStart()`、`OnShowListener` |
| `hide()` | Window 仍注册，DecorView 变为不可见 | 通常不触发 `onStop()`、`OnDismissListener` |
| hide 后再次 `show()` | 复用已注册 Window，恢复可见 | 典型实现不会重新走完整的首次 show 链路 |
| `dismiss()` | DecorView 从 WindowManager 移除 | `onStop()`、`OnDismissListener` |
| `cancel()` | 先表达“用户取消”，再执行 dismiss | `OnCancelListener`，随后 `onStop()` 和 `OnDismissListener` |
| dismiss 后再次 `show()` | Window 再次注册 | `onStart()` 和 `OnShowListener` 再次发生，`onCreate()` 通常不重复 |

### hide、dismiss、cancel 的本质区别

```text
hide
  只隐藏，不解除 Window 注册，不表示一次会话结束。

dismiss
  关闭并移除 Window，表示本次显示结束。

cancel
  表示由返回键、点击外部等“取消语义”触发，随后仍会进入 dismiss 流程。
```

因此：

- 业务上需要区分“用户取消”和“正常关闭”时，分别处理 `onCancel` 和 `onDismiss`。
- `onDismiss` 不等价于“退出动画最后一帧播放完成”。
- 不要用 `hide()` 代替生命周期收尾；hide 后 Window 仍然存在。

## 4.2 DialogFragment 生命周期

AndroidX `DialogFragment` 的关键映射如下：

| Fragment 回调/操作 | 对 Dialog 的典型处理 |
|---|---|
| `onCreate()` | 恢复是否以 Dialog 显示、style、theme、cancelable 等状态 |
| `onCreateDialog()` | 创建 Dialog 对象；此时通常尚未真正显示 |
| `onCreateView()` | 可选；返回的 Fragment View 可以作为 Dialog 内容 |
| `onStart()` | 调用 `Dialog.show()`，让 Window 真正进入窗口系统 |
| `onStop()` | 通常调用 `Dialog.hide()` |
| `onDestroyView()` | dismiss Dialog，并清除 Dialog 引用和监听器 |
| `onCancel()` | 接收 Dialog 的取消事件 |
| `onDismiss()` | 接收关闭事件，并确保 Fragment 从 FragmentManager 中移除 |
| `dismiss()` | 立即发起 Dialog 关闭，同时提交移除 Fragment 的事务 |
| `dismissNow()` | 同步完成 Fragment 移除事务；仍不代表窗口退出动画同步结束 |

可以用下面的近似状态图理解：

```text
Fragment CREATED
    └── Dialog 对象被准备

Fragment STARTED
    └── Dialog.show()
        └── Window attach / visible

Fragment STOPPED
    └── Dialog.hide()

Fragment View DESTROYED
    └── Dialog.dismiss()
        └── Window detach
```

### 宿主 Activity 会不会因为 Dialog 显示而暂停

普通 Dialog 或 DialogFragment 显示时，宿主 Activity 通常仍保持 RESUMED。因为这不是启动另一个 Activity，只是在同一应用窗口 token 体系下增加了一个 Window。

如果使用的是“Dialog 主题的 Activity”，那是另一个 Activity，底层 Activity 会按 Activity 切换规则进入 pause 等生命周期。这是两种完全不同的实现。

### DialogFragment 的优势

相较于在 Activity 中手动 new 一个 Dialog，`DialogFragment` 更适合以下场景：

- 需要跟随 FragmentManager 保存和恢复状态。
- 需要在配置变化、宿主重建时正确恢复。
- 需要避免 Activity 已销毁但 Dialog Window 仍被持有导致的 Window 泄漏。
- 需要统一处理 back stack 和 dismiss 事务。

但仍需注意：FragmentManager 已保存状态后直接 `show()` 或 `dismiss()`，可能产生 `IllegalStateException`。`dismissAllowingStateLoss()` 只是显式接受状态丢失，不应作为默认方案。

## 4.3 Window 和 View 的“生命周期”

`Window` 本身没有类似 Fragment 那样的标准 Lifecycle 状态机。更接近窗口生命周期的信号包括：

- `Window.Callback.onAttachedToWindow()`
- `Window.Callback.onDetachedFromWindow()`
- `onWindowFocusChanged()`
- DecorView 的 `onAttachedToWindow()` / `onDetachedFromWindow()`
- `ViewTreeObserver.OnPreDrawListener`
- `View.OnLayoutChangeListener`
- Insets、配置和可见性回调

平台 `android.app.Dialog` 本身也不是通用的 `LifecycleOwner`。AndroidX `ComponentDialog`、AppCompat 等兼容实现可能额外实现 Lifecycle、SavedState、BackPressed 等 Owner，但这不改变底层 Window 的 attach/detach 模型。

---

## 5. 动画系统与准确时机

Dialog 相关动画至少有四套机制，不能混用概念。

## 5.1 Window animation

通过：

```xml
<item name="android:windowAnimationStyle">@style/AppDialogWindowAnimation</item>
```

指定：

```xml
<style name="AppDialogWindowAnimation">
    <item name="android:windowEnterAnimation">@anim/dialog_enter</item>
    <item name="android:windowExitAnimation">@anim/dialog_exit</item>
    <item name="android:windowShowAnimation">@anim/dialog_show</item>
    <item name="android:windowHideAnimation">@anim/dialog_hide</item>
</style>
```

特点：

- 作用对象是整个 Window Surface，而不是某个业务 View。
- 由窗口系统根据 add、remove、show、hide 等可见性变化选择动画。
- `windowEnterAnimation` / `windowExitAnimation` 是 Dialog 最常用的两项。
- `windowShowAnimation` / `windowHideAnimation` 对应已存在窗口的显示、隐藏路径，但不同平台版本和具体转场路径可能有实现差异，不能把它当成精确业务回调。
- 这里通常引用 `res/anim` 下的传统 View Animation XML，不是 `res/animator` 的属性动画资源。

## 5.2 View property animation

例如对弹窗内容面板执行：

```kotlin
panel.animate()
    .alpha(1f)
    .translationY(0f)
    .start()
```

特点：

- 只作用于 Dialog Surface 内的某个 View。
- 应用可以精确拿到开始、取消和结束回调。
- 适合需要“动画结束后再提交业务状态”或“动画结束后再 dismiss”的场景。
- 如果在 View 尚未 measure 时依赖 `width`、`height`，应在 `doOnPreDraw`、`post` 或 layout 回调中启动。

## 5.3 Fragment transaction animation / transition

```kotlin
transaction.setCustomAnimations(...)
```

它主要控制 Fragment View 在同一个容器中的加入和移除。

当 `DialogFragment` 以独立 Dialog Window 显示时，Fragment 的 custom animation 通常不会替代 Dialog Window 的 enter/exit animation。此时应使用 Window animation 或对 Dialog 内容 View 做动画。

## 5.4 Activity transition

如果使用“Dialog 样式 Activity”，它仍然是 Activity，所以会走 Activity open/close transition。`overridePendingTransition`、Activity transition、task transition 等均属于另一套系统，不是普通 Dialog Window animation。

---

## 6. 动画应该在哪个时机配置和启动

| 需求 | 推荐时机 | 原因 |
|---|---|---|
| 指定 Dialog theme | `DialogFragment.onCreate()` 中 `setStyle()`，或构造 Dialog 时传 theme | 必须早于 Dialog/Decor 创建 |
| `requestWindowFeature()` | `setContentView()` 和 Decor 安装之前 | Window Feature 一旦安装 Decor 后再改通常无效或抛异常 |
| 设置 `windowAnimationStyle` | 最好在 `show()` 前 | 第一次 add Window 时 WMS 就需要读取动画属性 |
| 设置背景、dim、flags、gravity | 最好在 `show()` 前；尺寸可在 `onStart()` 中补充 | 避免首帧闪烁，并确保首次布局使用目标属性 |
| 设置最终 width/height | `DialogFragment.onStart()` 调用 `super.onStart()` 之后最稳妥 | 此时 Dialog 已 show，修改会通过 updateViewLayout 生效 |
| 启动依赖 View 尺寸的内容动画 | `decorView.doOnPreDraw {}` 或目标 View 的 `doOnLayout {}` | 此时 measure/layout 已完成 |
| 自动弹出 IME | Window 已显示并获得焦点后 | 仅 requestFocus 并不代表窗口已可接受 IME |
| 精确控制退出结束 | 先做内容 View 动画，在动画结束回调中调用 `dismiss()` | Window exit animation 没有稳定的应用层完成回调 |

### 6.1 OnShow 不是 enter animation 完成

`OnShowListener` 的含义更接近“Dialog 已经执行 show 并请求加入 WindowManager”。它不保证：

- 第一帧已经绘制；
- Window 已获得焦点；
- enter animation 已结束；
- View 已经有最终宽高。

需要依赖尺寸时，使用 `doOnPreDraw`：

```kotlin
override fun onStart() {
    super.onStart()

    val dialog = requireDialog()
    val panel = dialog.findViewById<View>(R.id.dialogPanel)

    panel.doOnPreDraw {
        panel.alpha = 0f
        panel.translationY = panel.height * 0.1f
        panel.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(180L)
            .start()
    }
}
```

### 6.2 OnDismiss 也不是 exit animation 完成

`dismiss()` 会发起客户端 View 移除和服务端 Window 移除。WMS 可能继续持有最后一帧 Surface 完成退出动画，但 Java/Kotlin 层的 Dialog 已进入停止和 dismiss 回调。

因此不要把下面的代码理解为“视觉动画已经结束”：

```kotlin
override fun onDismiss(dialog: DialogInterface) {
    super.onDismiss(dialog)
    // 这里表示 Dialog 语义上已关闭，不保证退出动画最后一帧已完成。
}
```

如果业务必须等动画结束，采用内容 View 动画并延迟 dismiss：

```kotlin
private var closing = false

private fun dismissWithAnimation() {
    if (closing) return
    closing = true

    val panel = requireDialog().findViewById<View>(R.id.dialogPanel)
    panel.animate()
        .alpha(0f)
        .translationY(panel.height * 0.1f)
        .setDuration(160L)
        .withEndAction {
            if (parentFragmentManager.isStateSaved) {
                dismissAllowingStateLoss()
            } else {
                dismiss()
            }
        }
        .start()
}
```

是否允许 state loss 应由业务明确决定，而不是机械使用。

---

## 7. Style 的三层含义

“style”在 Dialog 体系中经常指三种不同东西：

1. `DialogFragment.setStyle(style, theme)` 中的 style 常量；
2. Dialog 使用的 Theme 资源；
3. `android:windowAnimationStyle` 指向的动画 style。

必须分开理解。

## 7.1 DialogFragment.setStyle 的四个常量

```kotlin
setStyle(STYLE_NO_TITLE, R.style.AppDialogTheme)
```

| 常量 | 含义 |
|---|---|
| `STYLE_NORMAL` | 默认样式 |
| `STYLE_NO_TITLE` | 请求无标题 Window Feature |
| `STYLE_NO_FRAME` | 语义上表示无标准框架；实际外观仍取决于 theme 和 windowBackground |
| `STYLE_NO_INPUT` | 不接收焦点和触摸输入，同时走无标题设置，内部会使用 `FLAG_NOT_FOCUSABLE`、`FLAG_NOT_TOUCHABLE` 等 |

建议在 `onCreate()` 中、Dialog 创建前调用：

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NO_TITLE, R.style.AppDialogTheme)
}
```

## 7.2 Dialog Theme 的解析顺序

常见优先级可以理解为：

```text
运行时显式 Window 属性和 API 调用
    > 构造 Dialog / DialogFragment setStyle 指定的 theme
        > Context 中解析出的 dialogTheme
            > AppCompat / Material theme overlay
                > 父主题与平台默认值
```

MaterialAlertDialog 还可能给背景套一层 inset drawable，所以最终看到的可视边距不一定只来自 Window width。

## 7.3 常用 Window Theme 属性

| 属性 | 含义与注意点 |
|---|---|
| `android:windowIsFloating` | 声明为浮动窗口，影响 PhoneWindow 的默认策略、尺寸和装饰；不等同于简单的 `WRAP_CONTENT` |
| `android:windowNoTitle` | 取消标题 Feature；必须足够早地应用 |
| `android:windowBackground` | DecorView 背景；平台默认 Dialog 背景经常自带 inset、圆角和 padding |
| `android:windowFrame` | 窗口外围 frame drawable，现代应用较少直接使用 |
| `android:windowElevation` | DecorView/Window 的视觉 elevation；最终阴影还取决于背景 outline 和系统实现 |
| `android:windowClipToOutline` | 是否按 outline 裁剪；背景需要能提供有效 outline |
| `android:backgroundDimEnabled` | 是否启用后方变暗，通常映射到 `FLAG_DIM_BEHIND` |
| `android:backgroundDimAmount` | 默认 dim 强度，取值通常在 0 到 1 之间；运行时最终落到 `LayoutParams.dimAmount` |
| `android:windowAnimationStyle` | 指向一组 Window enter/exit/show/hide 动画的 style 资源 |
| `android:windowMinWidthMajor` | 当窗口宽度对应屏幕 major 轴时的最小宽度；通常是横屏下的 Dialog 宽度约束 |
| `android:windowMinWidthMinor` | 当窗口宽度对应屏幕 minor 轴时的最小宽度；通常是竖屏下的 Dialog 宽度约束 |
| `android:windowFixedWidthMajor` / `Minor` | 对应 major/minor 轴下的固定宽度 |
| `android:windowFixedHeightMajor` / `Minor` | 对应 major/minor 轴下的固定高度 |
| `android:windowIsTranslucent` | 使用透明/半透明窗口策略；对 Activity 影响更明显。仅为 Dialog 做透明圆角时，通常直接控制 windowBackground 更清晰 |
| `android:windowCloseOnTouchOutside` | 点击窗口外是否关闭；业务代码使用 `setCanceledOnTouchOutside()` 通常更直观 |
| `android:windowSoftInputMode` | 控制 IME 显示和 resize/pan 等行为；现代 edge-to-edge 场景还要结合 WindowInsets |
| `android:statusBarColor` / `navigationBarColor` | 对全屏或覆盖系统栏的 Dialog 才明显；普通居中浮窗通常看不到效果 |
| `android:windowFullscreen` | 传统全屏配置；现代实现更建议使用 InsetsController 和 edge-to-edge API |
| `android:windowContentTransitions` | Window 内的内容 Transition 开关，不等同于 `windowAnimationStyle`，普通 Dialog 通常不依赖它 |

### major / minor 到底是什么意思

major/minor 指的是屏幕当前较长轴和较短轴，不是固定等于横屏/竖屏：

```text
竖屏：
  屏幕高度是 major
  屏幕宽度是 minor
  Dialog 宽度通常使用 windowMinWidthMinor

横屏：
  屏幕宽度是 major
  屏幕高度是 minor
  Dialog 宽度通常使用 windowMinWidthMajor
```

常见配置：

```xml
<item name="android:windowMinWidthMinor">90%</item>
<item name="android:windowMinWidthMajor">60%</item>
```

## 7.4 Window animation style 示例

```xml
<!-- res/values/styles.xml -->
<style name="AppDialogTheme" parent="Theme.MaterialComponents.DayNight.Dialog.Alert">
    <item name="android:windowNoTitle">true</item>
    <item name="android:windowBackground">@drawable/bg_dialog</item>
    <item name="android:backgroundDimEnabled">true</item>
    <item name="android:backgroundDimAmount">0.45</item>
    <item name="android:windowMinWidthMinor">90%</item>
    <item name="android:windowMinWidthMajor">60%</item>
    <item name="android:windowAnimationStyle">@style/AppDialogWindowAnimation</item>
</style>

<style name="AppDialogWindowAnimation">
    <item name="android:windowEnterAnimation">@anim/dialog_enter</item>
    <item name="android:windowExitAnimation">@anim/dialog_exit</item>
</style>
```

```xml
<!-- res/anim/dialog_enter.xml -->
<set xmlns:android="http://schemas.android.com/apk/res/android"
    android:duration="180"
    android:interpolator="@android:interpolator/fast_out_slow_in">

    <alpha
        android:fromAlpha="0"
        android:toAlpha="1" />

    <scale
        android:fromXScale="0.96"
        android:fromYScale="0.96"
        android:pivotX="50%"
        android:pivotY="50%"
        android:toXScale="1"
        android:toYScale="1" />
</set>
```

```xml
<!-- res/anim/dialog_exit.xml -->
<set xmlns:android="http://schemas.android.com/apk/res/android"
    android:duration="140"
    android:interpolator="@android:interpolator/fast_out_linear_in">

    <alpha
        android:fromAlpha="1"
        android:toAlpha="0" />

    <scale
        android:fromXScale="1"
        android:fromYScale="1"
        android:pivotX="50%"
        android:pivotY="50%"
        android:toXScale="0.98"
        android:toYScale="0.98" />
</set>
```

---

## 8. WindowManager.LayoutParams 中常用字段

Theme 最终会有一部分结果落入 `WindowManager.LayoutParams`。运行时也可以直接修改。

| 字段 | 控制内容 |
|---|---|
| `width` / `height` | Window 请求的宽高；`MATCH_PARENT` 指当前可用窗口边界，不一定是物理屏幕完整像素 |
| `gravity` | Window 在可用区域内的对齐位置，例如 CENTER、BOTTOM |
| `x` / `y` | 在 gravity 基础上的偏移 |
| `type` | 窗口类型，决定层级、权限和 token 规则；普通 Dialog 通常属于应用窗口体系 |
| `token` | WMS 用于归属、权限和生命周期校验的 Binder token |
| `flags` | 焦点、触摸、dim、安全、屏幕常亮等行为位 |
| `dimAmount` | 后方变暗强度，需配合 `FLAG_DIM_BEHIND` |
| `softInputMode` | IME 显示状态及 adjustResize、adjustPan 等策略 |
| `windowAnimations` | Window animation style 的资源 id |
| `format` | Surface 像素格式；通常由 Window 和背景推断，不建议随意修改 |
| `alpha` | 整个 Window Surface 的透明度 |
| `layoutInDisplayCutoutMode` | 刘海/挖孔区域布局策略 |

## 8.1 常用 flags

| Flag | 作用与副作用 |
|---|---|
| `FLAG_DIM_BEHIND` | 在窗口后创建 dim 效果，强度由 `dimAmount` 决定 |
| `FLAG_NOT_FOCUSABLE` | Window 不获得按键焦点，通常也无法正常与 IME 交互，并影响触摸模态策略 |
| `FLAG_NOT_TOUCHABLE` | Window 完全不接收触摸 |
| `FLAG_NOT_TOUCH_MODAL` | 窗口外部触摸可以继续传给后方窗口；这不等同于“点击外部自动 dismiss” |
| `FLAG_WATCH_OUTSIDE_TOUCH` | 请求收到一次 `ACTION_OUTSIDE`，通常需要配合非触摸模态策略 |
| `FLAG_ALT_FOCUSABLE_IM` | 改变 Window 与输入法层级/交互关系，使用前必须明确是否需要输入焦点 |
| `FLAG_SECURE` | 禁止普通截图、录屏和非安全显示输出 |
| `FLAG_KEEP_SCREEN_ON` | Window 可见期间保持屏幕唤醒 |

不要把 flag 当成孤立开关。焦点、触摸模态、IME 和层级经常相互影响，应按组合行为验证。

---

## 9. WindowManager 是怎么控制 Dialog 的

## 9.1 add：把 View 树注册成 Window

`Dialog.show()` 最终调用应用侧 WindowManager：

```text
WindowManagerImpl.addView()
    └── WindowManagerGlobal.addView()
        ├── 创建 ViewRootImpl
        └── ViewRootImpl.setView()
            └── 通过 IWindowSession 请求 WMS.addWindow()
```

WMS 会处理：

1. 校验 token 是否有效；
2. 校验窗口 type 和权限；
3. 确定它属于哪个 Display、Task、Activity token 或父窗口；
4. 计算层级和相对 Z-order；
5. 建立输入通道和触摸区域；
6. 计算 frame、Insets、cutout 和可用区域；
7. 创建或准备 SurfaceControl；
8. 决定焦点、IME target 和可见性；
9. 选择并启动窗口动画。

应用请求的 width、height、x、y、type、flags 并不是绝对命令。WMS 会根据系统安全策略、窗口类型、多窗口模式、Display 边界和 Insets 进行限制或修正。

## 9.2 update：修改窗口属性

调用：

```kotlin
window.attributes = window.attributes.apply {
    dimAmount = 0.5f
    gravity = Gravity.BOTTOM
}
```

或：

```kotlin
window.setLayout(
    WindowManager.LayoutParams.MATCH_PARENT,
    WindowManager.LayoutParams.WRAP_CONTENT
)
```

最终会触发 `WindowManager.updateViewLayout()`，再由 `ViewRootImpl` 与 WMS 进行 relayout。WMS 重新计算 Window frame、Insets、焦点、输入区域和 Surface 状态。

## 9.3 remove：关闭窗口

`Dialog.dismiss()` 最终移除 DecorView。客户端 View 树会 detach，WMS 将窗口标记为退出并处理 Surface 销毁。为了播放 exit animation，服务端可能暂时保留窗口最后一帧的 Surface，待动画结束后再真正移除。

这解释了为什么：

- 应用层 `onDismiss()` 可以已经执行；
- 屏幕上仍可能短暂看到退出动画；
- 业务回调和视觉动画完成不是同一个时钟。

## 9.4 焦点、输入与 IME

Dialog 显示后，WMS 通常把窗口焦点切换到 Dialog Window：

```text
Activity Window：仍可见，但失去窗口焦点
Dialog Window：获得窗口焦点，接收按键、触摸和 IME
Activity 生命周期：通常仍是 RESUMED
```

输入分发由 WindowManager/Input 系统共同维护的输入窗口信息决定，不是简单地按 View 层级从 Dialog 传到 Activity。

点击 Dialog 外部时是否关闭，涉及多层条件：

- Window 是否触摸模态；
- 是否允许 outside touch；
- Dialog 是否 cancelable；
- `setCanceledOnTouchOutside()` 配置；
- 主题或 Window policy；
- 事件是否被背景/DecorView 消费。

## 9.5 token 为什么重要

WMS 使用 token 判断一个应用窗口归属于哪个 Activity/任务，以及该窗口是否有资格存在。

普通应用 Dialog 应使用 Activity 或以 Activity 为 base 的 themed context。使用 `applicationContext` 创建普通应用 Dialog，或在 Activity 已 finishing/destroyed 后 show，常见结果是：

```text
WindowManager.BadTokenException
```

系统级悬浮窗是另一套方案，需要 `TYPE_APPLICATION_OVERLAY`、对应权限和独立的生命周期管理，不能把普通 Dialog 简单改 type 后当作悬浮窗使用。

---

## 10. 一个相对完整的 DialogFragment 示例

```kotlin
class ActionDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppDialogTheme)
        isCancelable = true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), theme).apply {
            setContentView(R.layout.dialog_action)
            setCanceledOnTouchOutside(true)

            window?.let { window ->
                window.setWindowAnimations(R.style.AppDialogWindowAnimation)
                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                window.attributes = window.attributes.apply {
                    gravity = Gravity.BOTTOM
                    dimAmount = 0.45f
                    softInputMode =
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val window = requireDialog().window ?: return
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val panel = requireDialog().findViewById<View>(R.id.dialogPanel)
        panel.doOnPreDraw {
            // 此处 panel 已有可用尺寸，适合启动依赖高度的内容动画。
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        // 处理返回键、点击外部等取消语义。
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        // 表示 Dialog 语义上已经关闭，不表示 Window exit animation 已播放完。
    }
}
```

需要的典型 import：

```kotlin
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.DialogFragment
```

---

## 11. 尺寸、透明背景和 inset 的常见陷阱

### 11.1 Window 已经 MATCH_PARENT，为什么视觉上仍有边距

可能原因：

1. `windowBackground` 本身是 InsetDrawable；
2. MaterialAlertDialog 对背景额外套了 inset；
3. DecorView 或内容根布局有 padding；
4. `windowMinWidthMajor/Minor` 和 fixed width 限制了测量；
5. 当前处于多窗口模式，MATCH_PARENT 只匹配当前窗口可用边界；
6. system bars、display cutout 或 WindowInsets 改变了可用区域。

### 11.2 设置透明背景后圆角、阴影或 dim 消失

这些是不同层级：

- 圆角通常来自业务背景 drawable；
- 阴影依赖 elevation 和 outline；
- dim 来自 `FLAG_DIM_BEHIND` 与 `dimAmount`；
- Window 背景透明只影响窗口内容背景，不会自动关闭 dim；
- 透明背景也可能移除平台原有 inset，需要业务自己补 padding 和 shape。

常见全宽底部 Dialog 做法：

```kotlin
window.setBackgroundDrawableResource(android.R.color.transparent)
window.setLayout(MATCH_PARENT, WRAP_CONTENT)
window.setGravity(Gravity.BOTTOM)
```

然后把圆角、背景和内边距放在内容根布局上。

---

## 12. 最常见的错误理解

| 错误理解 | 正确认知 |
|---|---|
| Dialog 是 Activity View 树上的一层 View | Dialog 通常有独立 Window、DecorView、ViewRootImpl 和 Surface |
| DialogFragment 是一种 Dialog | 它是管理 Dialog 的 Fragment |
| `onShow` 表示进入动画结束 | 只表示 show 流程已经发起，第一帧和动画可能尚未结束 |
| `onDismiss` 表示退出动画结束 | 表示逻辑关闭，服务端 Surface 动画可能仍在进行 |
| `setCustomAnimations()` 能控制独立 Dialog Window | 它主要控制 Fragment 容器内 View；独立 Dialog 使用 Window animation 或内容动画 |
| `hide()` 和 `dismiss()` 一样 | hide 保留 Window；dismiss 移除 Window 并触发停止/关闭语义 |
| Dialog 显示会让宿主 Activity pause | 普通 Dialog 通常只改变窗口焦点，Activity 仍为 RESUMED |
| WindowManager 负责绘制 View | ViewRoot/RenderThread 产生 buffer，SurfaceFlinger 合成；WMS 负责窗口策略和管理 |
| 使用 applicationContext 创建 Dialog 更安全 | 普通应用 Dialog 需要有效 Activity/window token，applicationContext 常导致 BadToken |
| Window width 是唯一决定视觉宽度的因素 | 背景 inset、Decor padding、Material overlay 和 major/minor 尺寸都会影响结果 |

---

## 13. 最终总结

可以把整套系统记成下面五句话：

1. `DialogFragment` 管状态和事务，不直接代表 Window。
2. `Dialog` 管 show、hide、dismiss、cancel 等弹窗语义，并持有一个 `Window`。
3. `Window` 保存 DecorView 和 LayoutParams，是应用侧顶层窗口抽象。
4. `WindowManager` 把 DecorView 注册给 WMS；WMS 决定 token、层级、尺寸、Insets、焦点、输入、IME、可见性和窗口动画。
5. Window 回调表达的是逻辑状态，Surface 动画表达的是视觉状态；`OnShow`、`OnDismiss` 都不等价于动画完成。

最实用的时机规则是：

```text
Dialog theme / setStyle       -> Dialog 创建前
Window feature                -> setContentView 前
Window animation / flags      -> show 前
最终 Window width / height    -> DialogFragment.onStart() 的 super 之后
依赖尺寸的内容动画            -> doOnPreDraw / doOnLayout
需要精确退出完成              -> 内容动画结束后再 dismiss
```

---

## 14. 官方资料与源码入口

- [Dialog API](https://developer.android.com/reference/android/app/Dialog)
- [AndroidX DialogFragment API](https://developer.android.com/reference/androidx/fragment/app/DialogFragment)
- [Window API](https://developer.android.com/reference/android/view/Window)
- [WindowManager API](https://developer.android.com/reference/android/view/WindowManager)
- [WindowManager.LayoutParams API](https://developer.android.com/reference/android/view/WindowManager.LayoutParams)
- [AOSP Dialog.java](https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/app/Dialog.java)
- [AndroidX DialogFragment.java](https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:fragment/fragment/src/main/java/androidx/fragment/app/DialogFragment.java)
- [AOSP WindowManagerService.java](https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/services/core/java/com/android/server/wm/WindowManagerService.java)

> WindowManager 和 DialogFragment 的内部调用顺序会随 Android 平台、AndroidX Fragment、AppCompat 和 Material 版本调整。业务代码应依赖公开生命周期和 API 契约；源码顺序用于理解机制，不应当作跨版本绝对保证。
