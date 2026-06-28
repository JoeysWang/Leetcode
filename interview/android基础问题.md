# Android 高频面试题整理

> 说明：原文按公司面经罗列，存在大量重复题。本文按知识点去重后整理为问答，答案保持面试可讲清的粒度，适合复习和临场展开。

## 一、Android 基础与 UI

### 1. dp、px、sp 的关系是什么？如何把 dp 转成 px？

`dp` 是密度无关像素，`px` 是真实像素，换算公式：

```text
px = dp * density = dp * (dpi / 160)
```

常见密度：mdpi 为 1.0，hdpi 为 1.5，xhdpi 为 2.0，xxhdpi 为 3.0，xxxhdpi 为 4.0。`sp` 通常用于文字，会同时受屏幕密度和用户字体缩放影响。

### 2. Android 屏幕适配通常怎么做？

常用方案包括：使用 `dp/sp`、`ConstraintLayout`、`match_constraint`、百分比或 guideline、不同 `values-sw<N>dp` 资源、不同密度图片资源、矢量图、合理的最小宽度适配。大型项目还会统一设计稿基准，通过 `DisplayMetrics` 或资源限定符做尺寸体系，但要避免过度魔改系统 density，防止第三方组件和系统控件异常。

### 3. Activity 生命周期是什么？

典型流程是 `onCreate -> onStart -> onResume -> onPause -> onStop -> onDestroy`。从后台回前台一般是 `onRestart -> onStart -> onResume`。`onCreate` 做初始化，`onStart` 表示可见，`onResume` 表示可交互，`onPause` 释放前台独占资源，`onStop` 释放不可见资源，`onDestroy` 做最终清理。

### 4. A Activity 打开 B Activity 时生命周期如何变化？

一般顺序是：A `onPause`，B `onCreate -> onStart -> onResume`，然后 A `onStop`。如果 B 是透明主题或对话框样式，A 可能只到 `onPause`，不会进入 `onStop`。返回时通常是 B `onPause`，A `onRestart -> onStart -> onResume`，B `onStop -> onDestroy`。

### 5. Activity 启动模式有哪些？

`standard` 每次新建实例；`singleTop` 如果目标 Activity 已在栈顶则复用并回调 `onNewIntent`；`singleTask` 在任务栈中复用已有实例，并清理其上的 Activity；`singleInstance` 独占一个任务栈。现在还可结合 `launchMode`、`taskAffinity`、Intent flags 和 Jetpack Navigation 设计导航行为。

### 6. Launcher 启动 App 的流程是什么？

点击图标后 Launcher 通过 Binder 调用系统服务发起启动请求，系统侧经过 ActivityTaskManager/ActivityManager 做任务栈、进程和权限处理。如果目标进程不存在，Zygote fork 出应用进程，应用进程启动 `ActivityThread.main`，创建主线程 Looper，绑定 Application，随后创建目标 Activity 并执行生命周期。

### 7. ActivityThread 的 main 方法做了什么？

它创建主线程 Looper，初始化 `ActivityThread`，与系统服务建立 Binder 通信，调用 `Looper.loop()` 开始处理主线程消息。应用内 Activity、Service、BroadcastReceiver 的生命周期分发最终都落到主线程消息队列执行。

### 8. Activity 和 AppCompatActivity 有什么区别？

`Activity` 是 framework 基础类，`AppCompatActivity` 来自 AndroidX，提供向下兼容的 AppCompat 主题、Toolbar、夜间模式、FragmentActivity 能力等。`AppCompatActivity` 会打包进 APK，framework 的 `Activity` 由系统提供。

### 9. Activity 如何启动 Service？两者如何交互？

启动方式有 `startService/startForegroundService` 和 `bindService`。前者偏长期任务，后者偏组件间调用。交互方式包括 Binder、本地 Binder、AIDL、Messenger、广播、共享 ViewModel 不适合跨进程。Android 8.0 后后台启动服务受限，耗时前台任务要使用前台服务并展示通知。

### 10. Service 和 Thread 有什么区别？

Service 是 Android 组件，默认运行在主线程，不等于后台线程；Thread 是执行单元。耗时任务应放在线程池、协程、WorkManager 或其他异步机制中，Service 只负责承载生命周期和系统可见性。

### 11. Fragment hide/show 生命周期如何变化？

`hide/show` 不会销毁 Fragment 视图，通常不会重新走 `onCreateView/onDestroyView`，主要改变可见状态。老 API 可观察 `onHiddenChanged`，结合 `setUserVisibleHint` 已过时，现代做法更推荐用 `Lifecycle`、`setMaxLifecycle` 或 ViewPager2 的 FragmentStateAdapter 管理可见生命周期。

### 12. Fragment replace 生命周期如何变化？

`replace` 本质上通常是 remove 旧 Fragment 再 add 新 Fragment。旧 Fragment 会走 `onPause -> onStop -> onDestroyView`，如果不加入返回栈还会继续 `onDestroy -> onDetach`。新 Fragment 会创建视图并进入可见生命周期。

Fragment 重叠通常不是 `replace` 本身导致的，而是 Fragment 事务和状态恢复处理不当。常见原因有：Activity 因旋转、内存回收等重建时，FragmentManager 已经自动恢复旧 Fragment，但代码又在 `onCreate` 中无条件 `add` 了一次；使用 `add + hide/show` 切换时只 show 新 Fragment，没有 hide 其他 Fragment；连续快速点击导致多次提交事务；`commit()` 是异步执行，状态保存后再提交或时序混乱也可能出现异常显示。规避方式是首次添加前判断 `savedInstanceState == null` 或通过 tag 查找已存在 Fragment，切换时保证 hide/show 成对执行，必要时防重复点击，并避免在 `onSaveInstanceState` 后提交普通事务。

### 13. ViewPager 切换 Fragment 为什么可能耗时？

主要耗时来自 Fragment 创建、布局 inflate、图片加载、首帧数据渲染、嵌套 RecyclerView 预加载、复杂 measure/layout/draw。优化方向是懒加载数据、缓存页面、减少首屏布局层级、异步预取、控制 `offscreenPageLimit`，避免在 `onResume` 或可见回调里做重活。

### 14. ViewPager2 的原理是什么？

ViewPager2 基于 RecyclerView 实现，复用 RecyclerView 的布局、回收、滚动和动画机制。它支持竖向滑动、RTL、DiffUtil 友好，Fragment 场景下通过 `FragmentStateAdapter` 管理 Fragment 生命周期和状态保存。

### 15. 自定义 View 的基本流程是什么？

重写构造方法读取属性，按需重写 `onMeasure` 决定尺寸，`onLayout` 摆放子 View，`onDraw` 绘制内容，处理触摸事件和状态保存。自定义 ViewGroup 必须处理子 View 的 measure 和 layout，自定义单 View 通常重点在 measure、draw 和 touch。

### 16. 自定义 View 的多个构造参数分别是什么意思？style、theme、defStyle 如何正确获取？

自定义 View 常见构造方法有四个：`View(Context context)` 用于代码直接创建；`View(Context context, AttributeSet attrs)` 用于 XML inflate，`attrs` 是 XML 上写给当前 View 的属性集合；`View(Context context, AttributeSet attrs, int defStyleAttr)` 多了一个默认样式属性；`View(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)` 从 API 21 开始多了一个默认样式资源兜底。

`context` 不是随便的上下文，它携带当前页面的 theme。XML inflate 时，LayoutInflater 会把 Activity theme、Application theme、局部 `android:theme` 等合并到这个 Context 里。`attrs` 来自当前 View 的 XML 节点，包含节点上直接写的属性，也包含节点上的 `style="@style/xxx"` 这个样式引用；所以 XML 里显式写的 `style` 是通过 `attrs` 这条路参与解析的，和 `defStyleAttr` 不是一回事。`defStyleAttr` 是一个 theme attribute，例如 `R.attr.myViewStyle`，它不是 style 本身，而是一个“默认样式入口”：当 XML 节点没有用直接属性或 `style` 明确指定某个值时，控件才拿着这个 key 去当前 theme 里查 `<item name="myViewStyle">...</item>` 指向哪个默认 style。`defStyleRes` 是真正的默认 style 资源兜底，当 `defStyleAttr` 为 0 或 theme 里没配置对应 style 时使用。

XML 里的 `style` 是当前这个 View 实例的显式样式，例如 `<MyView style="@style/RedMyView" />`，只影响这个节点。`theme` 是一组全局或局部的属性环境，例如 Activity 使用的 `Theme.App`，也可以通过 `android:theme` 给某个 View 子树套一层局部主题。`defStyle` 在自定义 View 语境里通常指构造方法里的 `defStyleAttr/defStyleRes`，用于给控件提供默认外观。

自定义 View 正确读取属性时，应使用 `obtainStyledAttributes(attrs, styleable, defStyleAttr, defStyleRes)`，不要只从 `attrs` 里手动取值。它会按优先级合并属性：XML 上直接写的属性优先级最高，其次是 XML 的 `style="@style/xxx"`，这两层都来自 `attrs`；然后才是 `defStyleAttr` 在当前 theme 中指向的默认 style；最后是 `defStyleRes` 兜底。读取后一定要 `recycle()`。

举例：希望所有 `MyView` 默认标题是蓝色，但某个页面换主题后默认变绿色，某个具体控件还能单独改红色。`defStyleAttr` 的作用就是让 `MyView` 不需要在每个 XML 节点都写 `style`，而是从当前 theme 自动拿默认样式。

```xml
<!-- attrs.xml -->
<attr name="myViewStyle" format="reference" />

<declare-styleable name="MyView">
    <attr name="titleColor" format="color" />
</declare-styleable>
```

```xml
<!-- styles.xml -->
<style name="Theme.App" parent="Theme.MaterialComponents.DayNight">
    <!-- myViewStyle 是 theme 里的一个入口，指向默认 MyView 样式 -->
    <item name="myViewStyle">@style/Widget.App.MyView</item>
</style>

<style name="Theme.App.Green" parent="Theme.App">
    <!-- 换一个 theme，就能批量改变 MyView 默认样式 -->
    <item name="myViewStyle">@style/Widget.App.MyView.Green</item>
</style>

<style name="Widget.App.MyView">
    <item name="titleColor">#1E88E5</item>
</style>

<style name="Widget.App.MyView.Green">
    <item name="titleColor">#43A047</item>
</style>

<style name="Widget.App.MyView.Red">
    <item name="titleColor">#E53935</item>
</style>
```

```kotlin
class MyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.myViewStyle
) : View(context, attrs, defStyleAttr) {

    private var titleColor: Int

    init {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.MyView,
            defStyleAttr,
            R.style.Widget_App_MyView
        )
        try {
            titleColor = a.getColor(
                R.styleable.MyView_titleColor,
                Color.BLACK
            )
        } finally {
            a.recycle()
        }
    }
}
```

如果布局里这样写：

```xml
<!-- 不写 style，也不写 app:titleColor：走当前 theme 的 myViewStyle -->
<com.example.MyView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />

<!-- 写了 style：通过 attrs 参与解析，覆盖 theme 里 myViewStyle 指向的默认样式 -->
<com.example.MyView
    style="@style/Widget.App.MyView.Red"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />

<!-- 直接写属性：优先级最高，覆盖 style 和 defStyleAttr -->
<com.example.MyView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:titleColor="#000000" />
```

所以 `defStyleAttr` 的价值是“让控件支持主题级默认样式”。XML 里显式写的 `style="@style/..."` 走的是 `attrs`，会覆盖 `defStyleAttr`；`defStyleAttr` 只负责 `<MyView />` 没有显式指定样式或某个属性时，从当前 theme 自动补默认值。没有它，`<MyView />` 只能读到 XML 直接属性、XML 的 `style` 和代码里的 `defStyleRes` 兜底，无法跟随不同 theme 自动切换默认外观。

### 17. onMeasure、onLayout、onDraw 的关系是什么？

`onMeasure` 决定自己和子 View 的测量宽高；`onLayout` 决定子 View 的具体位置；`onDraw` 绘制内容。流程通常由 ViewRootImpl 发起，经过 measure、layout、draw 三大阶段。

`onMeasure(int widthMeasureSpec, int heightMeasureSpec)` 的两个参数分别表示父容器传给当前 View 的宽度测量要求和高度测量要求。这里的 “当前 View” 是被测量的那个子 View，MeasureSpec 是它的父容器根据“父容器自身可用空间”和“当前 View 在父容器里的 LayoutParams”共同计算出来的。每个 MeasureSpec 都由 size 和 mode 组成，可通过 `MeasureSpec.getSize()` 和 `MeasureSpec.getMode()` 取出。

mode 有三种：`EXACTLY` 表示父容器要求当前 View 使用一个确定尺寸，例如当前 View 的 `layout_width/layout_height` 写了具体 dp，或者当前 View 写 `match_parent` 且父容器本身尺寸已确定；`AT_MOST` 表示当前 View 最大不能超过父容器给出的 size，典型场景是当前 View 写 `wrap_content`，父容器告诉它“你可以按内容决定大小，但不能超过这个上限”；`UNSPECIFIED` 表示父容器不限制当前 View 的大小，常见于 ScrollView、RecyclerView 等特殊测量场景。

如果当前 View 写 `match_parent`，但父容器自己是 `wrap_content`，父容器的最终尺寸还没确定，不能直接给子 View 一个精确的 `EXACTLY`。这种情况下父容器通常会根据祖先容器给自己的上限，给子 View 生成 `AT_MOST`，含义更接近“你想匹配父容器，但父容器现在也要靠内容决定大小，所以你先在这个最大范围内测量”。部分 ViewGroup 会在自己测量完成后，再把 `match_parent` 的子 View 按父容器最终尺寸重新测一遍。因此 `match_parent` 遇到父容器 `wrap_content` 容易出现测量两次、尺寸不符合预期或父子尺寸互相依赖的问题，布局设计上应尽量避免这种含糊组合，优先让其中一方有明确约束。

自定义 View 中通常根据内容期望尺寸、padding 和 MeasureSpec 共同决定最终尺寸，然后调用 `setMeasuredDimension(width, height)`。自定义 ViewGroup 中要先遍历子 View，结合子 View 的 LayoutParams、margin、父容器 padding 调用 `measureChild` 或 `measureChildWithMargins` 测量子 View，再根据子 View 测量结果计算自己的尺寸，最后同样调用 `setMeasuredDimension`。

### 18. 什么时候可以获取 View 宽高？

`onCreate/onStart/onResume` 不保证能拿到最终宽高。可靠方式包括 `View.post {}`、`ViewTreeObserver.OnGlobalLayoutListener`、`doOnLayout`、自定义 View 的 `onSizeChanged`。`view.post` 通常能拿到宽高，是因为消息会排到当前布局遍历之后执行，但也要注意 View 未 attach 或布局未发生的特殊场景。

### 19. attachToWindow 什么时候调用？

View 被添加到窗口并与 Window 关联时会回调 `onAttachedToWindow`，移除时回调 `onDetachedFromWindow`。常用于注册监听、启动动画、绑定资源和释放资源。

### 20. 事件分发机制如何工作？

事件从 Activity 到 Window，再到 DecorView 和 ViewGroup。核心方法是 `dispatchTouchEvent`、`onInterceptTouchEvent`、`onTouchEvent`。ViewGroup 先分发，可能拦截；子 View 消费后后续同一手势序列通常继续交给它。`ACTION_CANCEL` 表示当前手势被父容器或系统打断，子 View 应取消按压、拖拽等状态。

### 21. dispatchTouchEvent、onInterceptTouchEvent、onTouchEvent 的关系是什么？

`dispatchTouchEvent` 负责分发入口；ViewGroup 的 `onInterceptTouchEvent` 决定是否拦截给自己处理；`onTouchEvent` 负责真正消费事件。View 没有 `onInterceptTouchEvent`。如果 `onTouchEvent` 对 `ACTION_DOWN` 返回 false，后续事件通常不会再发给它。

### 22. OnTouchListener、onTouchEvent、OnClickListener 的关系是什么？

事件先到 `dispatchTouchEvent`，如果设置了 `OnTouchListener` 且返回 true，则事件被消费，`onTouchEvent` 和点击逻辑不再执行。`OnClickListener` 是在 `onTouchEvent` 处理完整点击手势后触发的高级回调。

### 23. ViewGroup 在 ACTION_MOVE 时拦截事件会怎样？

如果父 ViewGroup 在 MOVE 阶段从不拦截变为拦截，子 View 会收到 `ACTION_CANCEL`，后续事件交给父 ViewGroup 的 `onTouchEvent`。这也是处理滑动冲突的核心机制。

### 24. 如何处理 ViewPager 嵌套 ViewPager 或滑动冲突？

常用外部拦截法或内部拦截法。外部拦截法由父容器根据方向和边界决定是否拦截；内部拦截法由子 View 调用 `requestDisallowInterceptTouchEvent(true)`，需要父容器配合。嵌套滚动场景优先考虑 NestedScrolling 体系。

### 25. 如何实现不能滑动的 ViewPager？

可以自定义 ViewPager/ViewPager2，拦截触摸和 fake drag，或在 ViewPager2 中通过 `setUserInputEnabled(false)` 禁用用户滑动。仍可通过代码切换页面。

### 26. 如何自定义 LinearLayout 测量子 View？

遍历子 View，结合父容器 MeasureSpec、padding、子 View margin 和 LayoutParams 调用 `measureChildWithMargins`。垂直方向累加高度、取最大宽度，水平方向累加宽度、取最大高度，最后通过 `resolveSizeAndState` 设置自身尺寸。

### 27. 如何自定义 FlexLayout？

核心是测量时按行放置子 View：当前行剩余宽度不足则换行，记录每行宽高；布局时按行遍历并摆放。要处理 margin、padding、`MeasureSpec`、换行策略、对齐方式、RTL 和高度限制。

### 28. 如何实现九宫格布局？

可自定义 ViewGroup：先计算列数、间距和单元格尺寸，`onMeasure` 中测量每个子 View，`onLayout` 按 `row = index / column`、`column = index % columnCount` 放置。简单场景也可用 GridLayout、RecyclerView GridLayoutManager 或 ConstraintLayout Flow。

### 29. ConstraintLayout 如何实现三等分？

可以使用三条 view 设置宽度为 `0dp`，通过水平 chain 并设置相同 weight；也可以使用 Guideline 分割 33% 和 66%。在 ConstraintLayout 中 `0dp` 表示 match constraints，不是普通的 0 宽。

### 30. Drawable 和 View 有什么区别？

View 是可交互、可布局的 UI 节点，参与 measure/layout/draw 和事件分发。Drawable 是可绘制资源，不参与布局和事件分发，通常被 View 持有并绘制。常见 Drawable 有 BitmapDrawable、ShapeDrawable、GradientDrawable、VectorDrawable、StateListDrawable、LayerDrawable、NinePatchDrawable。

### 31. 两次 getDrawable 拿到的对象有什么区别？

资源系统可能共享 Drawable 的 ConstantState。直接修改一个 Drawable 的状态可能影响同资源的其他实例。需要独立修改时应调用 `mutate()`，再设置 tint、alpha 或 state。

### 32. wrap_content 的 ImageView 加载远程图片应如何裁剪？

不要等原图完整解码后再缩放。应尽量给 ImageView 明确尺寸或根据容器预估尺寸，请求图片库按目标宽高 downsample/override，再配合 `centerCrop`、`fitCenter` 等 scaleType。列表里尤其要避免加载超大 Bitmap。

### 33. 属性动画更新时一定会回调 onDraw 吗？

不一定。属性动画只是修改属性值，是否重绘取决于属性 setter 是否触发 `invalidate/requestLayout`。例如 `translationX` 属于 RenderNode 属性，可能不需要重新执行 `onDraw`；自定义属性若影响绘制，需要手动 `invalidate()`。

### 34. 补间动画和属性动画有什么区别？哪个效率更高？

补间动画只改变视觉表现，不改变真实属性和点击区域；属性动画真正修改对象属性，适用范围更广。效率不能简单比较，简单透明度/位移场景都很快；复杂属性动画如果频繁 requestLayout 或重绘大区域会更耗性能。

### 35. Android 动画里用到了哪些设计模式？

常见包括观察者模式监听动画回调，策略模式封装插值器和估值器，组合模式管理 AnimatorSet，模板方法体现在 View 绘制流程。面试中重点讲清插值器决定时间进度，估值器决定属性值计算。

### 36. 屏幕刷新机制、双缓冲、三缓冲、黄油计划是什么？

Android 通过 VSync 驱动 Choreographer 安排 input、animation、traversal。双缓冲是前后缓冲交替显示和绘制，三缓冲可在某些卡顿场景减少等待，但会增加延迟。黄油计划强调 16.6ms 一帧、VSync 协调、硬件加速和渲染管线优化。

### 37. LargeHeap 真的能分配到大内存吗？

`android:largeHeap="true"` 可以请求更大的堆上限，但具体大小由设备和系统决定，不能保证无限增大。它可能掩盖内存问题并影响系统整体调度，通常应优先做图片、缓存和泄漏优化。

### 38. 如何实现多主题或换肤？

优先用官方 theme 体系做多主题，不要在业务代码里到处硬编码颜色。做法是把颜色、字体、圆角、控件默认样式都抽成 theme attribute，例如 `?attr/colorPrimary`、`?attr/listItemTextColor`、`?attr/myButtonStyle`，布局和代码里引用 attr，而不是直接引用固定色值。

如果只是深色/浅色模式，推荐 AppCompat DayNight：主题继承 `Theme.MaterialComponents.DayNight` 或 `Theme.Material3.DayNight`，切换时调用 `AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES/NO/FOLLOW_SYSTEM)`，Activity 会 recreate，资源自动从 `values-night`、DayNight theme 中重新解析。Compose 则通常用 `isSystemInDarkTheme()` 和 `MaterialTheme(colorScheme = ...)`。

如果是 App 内多套主题，例如蓝色版、绿色版、节日版，常见做法是在 `styles.xml` 定义多套 Theme，在 Activity `super.onCreate` 前调用 `setTheme(R.style.Theme_App_Green)`，然后正常 inflate。运行中切换主题后保存用户选择，再 `activity.recreate()` 让当前页面重新走资源解析。局部弹窗或某个 View 子树可以用 `ContextThemeWrapper(context, R.style.Theme_App_Green)` 或 XML 的 `android:theme="@style/ThemeOverlay.xxx"`。

如果不想 recreate，只做少量颜色切换，可以维护一套 ThemeManager，通知页面局部刷新：重新读取 attr 或配置色值，然后更新 `TextView.setTextColor()`、`View.setBackgroundTintList()`、`ImageView.setImageTintList()`、`DrawableCompat.setTint()` 等。这个方案响应快，但要求所有自定义 View 暴露 `applyTheme()` 或监听主题变化，维护成本更高。

插件化换肤是另一类方案：下载皮肤包 APK/资源包，通过 `AssetManager.addAssetPath` 或兼容封装创建皮肤包 `Resources`，按相同资源名映射替换颜色、drawable、mipmap。它适合强运营换肤，但兼容性、资源 id、系统版本限制、缓存和回滚都更复杂，普通多主题不优先选。

落地时要处理三件事：第一，统一入口，所有颜色/样式尽量走 attr 和 style；第二，切换策略明确，全局换肤用 recreate，局部换肤就建立刷新协议；第三，自定义 View 读取属性时用 `obtainStyledAttributes(attrs, styleable, defStyleAttr, defStyleRes)`，并在主题变化后重新解析颜色或提供刷新方法。

### 39. setFactory 和 setFactory2 有什么区别？

二者都可拦截 LayoutInflater 创建 View。`Factory2` 多了 parent 参数，AppCompat 用它实现兼容控件替换、tint、主题处理。通常自定义换肤或监控 inflate 时更关注 Factory2。

### 40. 如何优化 XML inflate 时间？

减少布局层级，使用 ConstraintLayout 合理扁平化，少用复杂 include/merge 嵌套，避免首屏加载非必要布局，使用 ViewStub 懒加载，减少自定义 View 构造耗时。极致场景可预加载、异步 inflate，但要注意不能在子线程触碰不安全的 UI 逻辑。

## 二、Handler、消息机制与 IPC

### 41. Handler 机制是什么？

Handler 负责发送和处理消息，MessageQueue 保存消息，Looper 不断取消息分发，ThreadLocal 保证每个线程可持有自己的 Looper。主线程启动时系统已创建 Looper，子线程使用 Handler 前需要手动 `Looper.prepare()` 和 `Looper.loop()`。

### 42. 一个线程有几个 Looper？为什么？

一个线程最多一个 Looper。Looper 存在 ThreadLocal 中，`Looper.prepare()` 会检查当前线程是否已经创建过 Looper，重复创建会抛异常。这样可以保证消息队列和线程一一对应。

### 43. Handler 是怎么切换线程的？

Handler 绑定创建它时所在的 Looper。其他线程调用 Handler 发送消息时，消息进入目标 Looper 的 MessageQueue，最终由目标线程执行 `handleMessage` 或 Runnable，所以实现线程切换。

### 44. Handler 为什么可能内存泄漏？GC Root 是什么？

非静态内部 Handler 或延迟 Message 会持有外部 Activity 引用。MessageQueue 被主线程 Looper 持有，主线程是 GC Root，导致 Activity 无法释放。解决方式是静态内部类加弱引用，生命周期结束时 `removeCallbacksAndMessages(null)`，或使用 lifecycle-aware API。

### 45. Handler 同步屏障是什么？

同步屏障是一种特殊 Message，会阻塞普通同步消息，只允许异步消息通过。系统在 UI 刷新时用它优先处理 VSync 相关消息，保证输入、动画和绘制优先执行。应用层可通过异步 Handler 或 `Message.setAsynchronous(true)` 使用异步消息。

### 46. Handler 休眠和唤醒是怎样的？延时消息如何实现？

MessageQueue 底层通过 native poll/epoll 等机制等待。延时消息按执行时间排序，Looper 取队头消息，如果未到时间就按剩余时间休眠。插入更早消息或移除/唤醒时会唤醒队列重新计算等待时间。

### 47. IdleHandler 有什么应用场景？

IdleHandler 在 MessageQueue 空闲时回调，适合做低优先级初始化、首帧后预加载、日志上报等。不能执行耗时任务，否则会影响后续消息响应。

### 48. Binder 机制的原理是什么？

Binder 是 Android 主要 IPC 机制，由用户态 Binder 框架和内核 Binder 驱动配合完成。客户端通过代理对象发起调用，数据经 Binder 驱动传递到服务端线程池，服务端执行后返回结果。它支持身份传递、引用计数和一次拷贝，相比传统 Socket 更适合 Android 系统服务调用。

### 49. Binder 可以调用原进程方法吗？

如果服务和客户端在同一进程，Binder 可能直接返回本地对象，调用就是普通方法调用。如果跨进程，调用发生在服务端 Binder 线程池，客户端通过 Proxy 触发远端 Stub 的方法。

### 50. Binder 怎么验证调用方 pid/uid？

服务端可以在 Binder 调用中使用 `Binder.getCallingPid()` 和 `Binder.getCallingUid()` 获取调用方身份，再结合包管理器、签名权限或自定义权限做校验。

### 51. AIDL 中 in、out、inout、oneway 分别是什么意思？

`in` 表示客户端传入，服务端读取；`out` 表示服务端写回；`inout` 双向读写，开销更大。`oneway` 表示异步单向调用，客户端不等待结果，但服务端仍按 Binder 队列处理，不能滥用。

### 52. Android 跨进程通信方式有哪些？

常见有 Intent/Bundle、Binder/AIDL、Messenger、ContentProvider、Broadcast、Socket、文件、共享内存 MemoryFile/Ashmem。选择时看数据量、实时性、调用方向、是否需要权限控制和生命周期管理。

### 53. Bundle 是什么数据结构？靠什么传递数据？

Bundle 本质是 key-value 容器，内部常用 ArrayMap/Map 结构保存数据。跨进程或 Activity 传参时通过 Parcel 序列化，要求数据类型可被 Parcel 支持，例如基本类型、String、Parcelable、Serializable 等。

### 54. Serializable 和 Parcelable 有什么区别？

Serializable 是 Java 标准序列化，使用简单但反射和临时对象较多，性能一般。Parcelable 是 Android 专用序列化，手写读写字段，性能更好，适合进程间和组件间高频传输。

### 55. SharedPreferences 可以跨进程通信吗？

不推荐。SharedPreferences 主要面向单进程轻量配置，多进程下缓存一致性和并发写入容易出问题。跨进程应使用 ContentProvider、AIDL、DataStore 配合单进程代理，或数据库。

### 56. SharedPreferences 的 apply 和 commit 有什么区别？

`commit` 同步写入磁盘并返回是否成功；`apply` 先同步更新内存，再异步写磁盘，没有成功返回值。UI 线程高频写入应优先 `apply`，但关键配置需要确认结果时可用 `commit`。

### 57. Broadcast、LocalBroadcast、RxBus/EventBus 有什么区别？

系统广播可跨进程，受权限和后台限制影响；LocalBroadcast 只在进程内，现已不推荐新项目使用；RxBus/EventBus 是进程内事件分发，灵活但容易造成隐式依赖和生命周期泄漏。现代项目更推荐明确的数据流，如 Flow、LiveData、SharedFlow。

## 三、Java、Kotlin 与并发

### 58. Java 面向对象有哪些特性？

封装隐藏实现并暴露稳定接口；继承复用和扩展父类能力；多态让父类引用指向子类对象，运行时根据真实类型调用方法。面试中要强调组合通常比继承更灵活。

### 59. 静态方法、静态对象为什么不能被继承或多态重写？

静态成员属于类，不属于实例。子类可以声明同名静态方法形成隐藏，但不会发生运行时动态分派，所以不是重写。静态字段同理，访问解析取决于引用的编译期类型。

### 60. String、equals、== 有什么区别？

`==` 比较引用是否相同，基本类型比较值。`equals` 默认也是比较引用，但 String 重写后比较字符内容。字符串字面量会进入常量池，`new String()` 会创建新对象。

### 61. 反射是什么？如何用反射创建对象？

反射是在运行时获取类、方法、字段和构造器信息并调用。创建对象可通过 `Class.forName("类名").getDeclaredConstructor().newInstance()`。反射灵活但有性能、混淆和安全成本，Android 中常见于框架、注解、插件化和兼容逻辑。

### 62. 反射可以修改 final 字段吗？

某些情况下可以通过反射修改非静态 final 字段，但结果受 JVM/ART 优化、内联、访问限制影响，不可靠。Android 新版本还加强了隐藏 API 和反射限制，业务代码不应依赖这种行为。

### 63. 代理模式和装饰模式有什么区别？

代理模式强调控制访问，比如远程代理、权限代理、懒加载代理；装饰模式强调在不改原对象的情况下增强功能。二者结构相似，意图不同。动态代理常用于 Retrofit、AOP、RPC 等。

### 64. 动态代理有什么作用？

动态代理可在运行时生成代理对象，把方法调用统一拦截到 InvocationHandler，用于日志、监控、事务、网络接口适配、权限校验等。Java 动态代理要求基于接口，字节码代理可代理类。

### 65. 注解有哪些类型？怎么解析？

按保留期分为 SOURCE、CLASS、RUNTIME。SOURCE 注解常由 APT/KSP 在编译期处理生成代码；RUNTIME 注解运行时通过反射读取；CLASS 注解保留到字节码，可由字节码工具处理。Android 常用编译期注解减少运行时反射成本。

### 66. 编译期注解处理的是 Java 文件还是字节码？

APT 处理的是编译期抽象语法和符号模型，输入通常来自 Java/Kotlin 源码编译过程，不是直接改字节码。它常生成新的 Java/Kotlin 文件。字节码插桩才是处理 class 文件。

### 67. Retrofit 为什么使用运行时注解？

Retrofit 通过运行时反射读取接口方法、HTTP 注解和泛型信息，生成 ServiceMethod 并用动态代理发起请求。这样接口声明简单、灵活，启动后通常会缓存解析结果。若追求更低运行时成本，也可用编译期生成方案。

### 68. 泛型有什么优点？

泛型提供编译期类型检查，减少强转和 ClassCastException，提高 API 表达能力。集合、回调、网络响应、数据层封装都大量依赖泛型。

### 69. Java 泛型为什么会擦除？

Java 为兼容早期版本采用类型擦除，泛型信息主要用于编译期检查，运行时大多变成原始类型或上界类型。因此不能直接 `new T()`，也不能可靠判断 `List<String>` 和 `List<Integer>` 的运行时类型。

### 70. 什么是上界通配符和下界通配符？PECS 原则是什么？

`? extends T` 表示未知类型是 T 或 T 的子类，适合读取，读出来至少是 T，但不适合写入具体子类。`? super T` 表示未知类型是 T 或 T 的父类，适合写入 T 或 T 子类，读取只能当 Object。PECS：Producer Extends，Consumer Super。

### 71. Retrofit 中泛型是怎么解析的？

Retrofit 通过反射读取接口方法的 `GenericReturnType`、参数泛型和注解，再交给 Converter、CallAdapter 解析。由于方法签名里的泛型信息保存在 class 元数据中，运行时可以读取到如 `Call<ApiResult<User>>` 这类结构。

### 72. Kotlin 的空安全原理是什么？

Kotlin 在类型系统中区分可空和非空类型，编译器对可空调用做检查。与 Java 互操作时会插入运行时空检查，平台类型需要开发者谨慎处理。`?.`、`?:`、`!!` 分别表示安全调用、Elvis 默认值和强制非空。

### 73. Kotlin lazy 是什么？lazy ViewModel 要注意什么？

`lazy` 是延迟初始化，第一次访问时执行初始化逻辑，默认线程安全模式是 synchronized。ViewModel 常通过 `by viewModels()` 或 `by activityViewModels()` 懒加载，要注意作用域：Activity 作用域共享，Fragment 作用域独立。

### 74. Kotlin 和 Java 互相调用有什么问题？

常见问题包括空安全平台类型、默认参数生成重载、顶层函数类名、属性 getter/setter、协程 suspend 函数调用、SAM 转换、可见性和泛型通配符。对 Java 暴露 API 时可用 `@JvmOverloads`、`@JvmStatic`、`@JvmName`、`@JvmField` 优化体验。

### 75. 协程是什么？能完全取代 RxJava 吗？

协程是轻量级并发抽象，通过挂起函数写出顺序式异步代码。它适合结构化并发、生命周期绑定、异步任务和 Flow 数据流。RxJava 在复杂响应式组合、历史项目生态中仍有价值，不能简单说完全取代。

### 76. 协程如何取消？

协程取消是协作式的，调用 Job 的 `cancel()` 后，挂起函数会检查取消状态并抛出 `CancellationException`。CPU 密集循环中需要主动检查 `isActive` 或调用 `yield()`。资源释放可放在 `finally`，必要时用 `withContext(NonCancellable)` 做不可取消清理。

### 77. CoroutineScope、CoroutineContext 和 `+` 是什么？

Scope 定义协程生命周期边界，Context 保存 Job、Dispatcher、CoroutineName、ExceptionHandler 等元素。`+` 用于组合 Context，相同 key 的元素后者覆盖前者。Android 中常用 `lifecycleScope`、`viewModelScope`。

### 78. volatile 有什么作用？

`volatile` 保证可见性和一定的有序性，写 volatile 会把修改刷新到主内存，读 volatile 会读取最新值，同时禁止相关指令重排序。它不保证复合操作的原子性，如 `count++` 仍需锁或原子类。

### 79. synchronized 底层原理是什么？

同步代码块通过 `monitorenter/monitorexit` 操作对象监视器，方法同步通过方法访问标志实现。锁信息与对象头 Mark Word 相关。它保证互斥、可见性和有序性，异常退出时 JVM 会释放锁。

### 80. synchronized 锁升级过程是什么？

传统描述是无锁、偏向锁、轻量级锁、重量级锁。锁会根据竞争情况升级，重量级锁涉及操作系统互斥量，成本更高。注意较新 JDK 已逐步废弃偏向锁，但 Android/ART 具体实现也会随版本变化，面试中讲核心思想即可。

### 81. synchronized 和 ReentrantLock 有什么区别？

`synchronized` 是 JVM 关键字，自动释放锁，语法简单。`ReentrantLock` 是 JUC 类，支持可中断锁、超时锁、公平锁、多个 Condition，但必须手动 unlock，通常放在 finally。发生异常时 synchronized 自动释放，ReentrantLock 若未 finally unlock 会死锁。

### 82. synchronized 是公平锁吗？ReentrantLock 呢？

`synchronized` 通常是非公平的。`ReentrantLock` 默认非公平，可通过构造参数创建公平锁。公平锁减少饥饿，但吞吐量通常低于非公平锁。

### 83. AQS 是什么？

AQS 是 AbstractQueuedSynchronizer，JUC 中很多同步器的基础。它用一个 volatile state 表示同步状态，用 CLH 变体队列管理等待线程，提供独占和共享两类获取释放模板。ReentrantLock、Semaphore、CountDownLatch 都基于它实现。

### 84. JMM 的可见性、原子性、有序性是什么？

原子性指操作不可被中断；可见性指一个线程修改对其他线程可见；有序性指程序执行顺序符合 happens-before 规则。`synchronized` 三者都能保证，`volatile` 保证可见性和有序性但不保证复合操作原子性。

### 85. 双重检查单例为什么要加 volatile？

对象创建可能发生指令重排序：分配内存、初始化对象、引用赋值。如果引用赋值先于初始化，其他线程可能拿到半初始化对象。`volatile` 可以禁止这种重排序，并保证实例对其他线程可见。

### 86. 对象加载和类加载过程是什么？

类加载包括加载、验证、准备、解析、初始化。对象创建包括类加载检查、分配内存、初始化零值、设置对象头、执行构造方法。方法本身不会像字段一样“加载成对象属性”，类元数据会进入运行时方法区相关结构。

### 87. JVM/ART 内存结构有哪些区域？

常见划分包括程序计数器、虚拟机栈、本地方法栈、堆、方法区/元空间概念。Android ART 有自己的运行时实现，但面试中可按 Java 内存模型讲：对象主要在堆上，栈保存局部变量和调用帧，类元数据和常量等在方法区相关区域。

### 88. GC Roots 有哪些？

常见 GC Roots 包括线程栈中的局部变量、静态字段、JNI 引用、活动线程、类加载器、同步锁持有对象等。可达性分析从 GC Roots 出发，无法到达的对象才可能被回收。

### 89. 垃圾回收机制和老年代算法是什么？

GC 通过可达性分析判断对象存活。新生代常用复制算法，老年代常用标记清除、标记整理或混合算法。Android ART 的 GC 实现会随版本变化，重点应讲清可达性、分代思想、Stop-The-World 和减少对象分配。

### 90. HashMap 原理是什么？

HashMap 基于数组加链表/红黑树。根据 key 的 hash 定位桶，冲突时链表或树化存储。JDK 8 中链表过长且数组足够大时转红黑树，平均查找 O(1)，极端冲突可能退化，树化后 O(log n)。

### 91. LinkedHashMap 的数据结构是什么？

LinkedHashMap 继承 HashMap，并额外维护双向链表，保存插入顺序或访问顺序。访问顺序模式可实现 LRU 缓存，重写 `removeEldestEntry` 可控制淘汰。

### 92. AtomicInteger 在源码中有什么应用？

AtomicInteger 用 CAS 实现无锁原子更新，常用于引用计数、状态机、线程安全计数。Android/Java 源码中常见类似原子类用于并发状态控制，例如线程池状态、任务计数、一次性标记等。

### 93. 如何让两个线程交替打印？

可用 `synchronized + wait/notify`、`ReentrantLock + Condition`、Semaphore 或 BlockingQueue。核心是共享状态标记当前该谁打印，打印后切换状态并唤醒对方。

### 94. 如何中止线程？Thread.interrupt 一定有效吗？

推荐使用中断协作取消。`interrupt()` 只是设置中断标志或打断可中断阻塞方法，不会强杀线程。线程必须主动检查中断状态并安全退出。不可使用已废弃的 `stop()`。

### 95. 线程间同步有哪些方法？

常见方法有 synchronized、ReentrantLock、volatile、原子类、Semaphore、CountDownLatch、CyclicBarrier、BlockingQueue、Handler、协程 Channel/Mutex 等。选择取决于互斥、通知、计数、限流还是消息传递。

### 96. 线程池有哪些类型？原理是什么？

核心参数包括 corePoolSize、maximumPoolSize、keepAliveTime、workQueue、ThreadFactory、RejectedExecutionHandler。常见封装有 FixedThreadPool、CachedThreadPool、SingleThreadExecutor、ScheduledThreadPool。实际项目更推荐显式创建 ThreadPoolExecutor，避免无界队列或无界线程导致 OOM。

### 97. 线程池拒绝策略有哪些？

默认有 AbortPolicy 抛异常、CallerRunsPolicy 调用者线程执行、DiscardPolicy 丢弃当前任务、DiscardOldestPolicy 丢弃队头任务。业务中常自定义上报或降级。

### 98. 为什么阿里规范不建议直接使用 Executors 创建线程池？

因为一些快捷工厂隐藏了风险：FixedThreadPool 和 SingleThreadExecutor 使用无界队列，可能 OOM；CachedThreadPool 最大线程数很大，可能创建过多线程。显式 ThreadPoolExecutor 能更清楚控制资源边界。

## 四、Jetpack、架构与设计模式

### 99. MVC、MVP、MVVM 有什么区别？

MVC 中 View 和 Controller/Model 容易耦合；MVP 通过 Presenter 承接业务逻辑，View 接口化，但 Presenter 可能臃肿并持有 View 泄漏；MVVM 通过 ViewModel 暴露状态，View 观察状态更新 UI，更适合响应式数据流和生命周期管理。

### 100. MVP 如何处理 Presenter 内存泄漏？

Presenter 不应长期强持有 Activity/Fragment。可在生命周期结束时 detach View，使用弱引用或接口解绑，网络/订阅任务及时取消。更推荐让 Presenter 不直接依赖具体 View 实例，只输出状态。

### 101. ViewModel 为什么旋转屏幕后不丢数据？

Jetpack ViewModel 存在 ViewModelStore 中，配置变更时 Activity 重建但 ViewModelStore 会被保留，新 Activity 重新获取同一个 ViewModel。真正 finish 时 ViewModel 才会 `onCleared`。

### 102. Activity 和 Fragment 中初始化 ViewModel 有什么区别？

Activity 中 `by viewModels()` 作用域是 Activity。Fragment 中 `by viewModels()` 作用域是当前 Fragment，`by activityViewModels()` 作用域是宿主 Activity，可在多个 Fragment 间共享。

### 103. ViewModel 使用中有什么坑？

不要持有 Activity、View、Context 等短生命周期对象；不要把 ViewModel 当万能单例；异步任务要绑定 `viewModelScope`；一次性事件要避免配置变更后重复消费；大对象和 Bitmap 不宜长期放入 ViewModel。

### 104. Lifecycle 原理是什么？

LifecycleOwner 持有 LifecycleRegistry，组件生命周期变化时分发 Event，观察者根据当前 State 接收回调。它让组件能感知 Activity/Fragment 生命周期，减少手动注册和释放。

### 105. 如果在 onStart 里订阅 Lifecycle，会回调 onCreate 吗？

Lifecycle 会把观察者同步到当前状态。具体回调取决于观察 API 和当前状态，通常新增观察者会收到使其达到当前状态所需的事件，因此可能收到之前阶段对应的事件。面试中要说明这是状态机同步，不是简单只监听未来事件。

### 106. DataBinding 原理是什么？

DataBinding 在编译期根据 XML 生成 Binding 类，收集表达式和绑定关系，运行时通过 `setVariable`、Observable、LiveData 等触发 dirty flag，再批量更新 View。双向绑定通过属性适配器和 inverse binding listener 实现。

### 107. MVVM 怎么更新 UI？

ViewModel 暴露不可变 UI State，如 LiveData、StateFlow。View 观察状态变化并渲染。用户操作转成 Intent/Action 交给 ViewModel 处理，ViewModel 更新状态，形成单向数据流。双向绑定适合表单，但复杂页面要控制可读性。

### 108. Jetpack 常用组件有哪些？

常见有 Lifecycle、ViewModel、LiveData、Room、Navigation、WorkManager、DataStore、Paging、Hilt、CameraX、Compose 等。回答时最好结合项目说明解决了什么问题，而不是只背名字。

### 109. ARouter 原理是什么？

ARouter 使用注解标记路由页面，编译期 APT 生成路由表，运行时初始化加载分组映射。跳转时根据 path 找到目标 class，构造 Intent 并注入参数。拦截器通过责任链在跳转前执行登录、鉴权等逻辑。接口调用可通过 provider 路由注册服务实现解耦。

### 110. 如果不用 ARouter，如何解耦页面跳转？

可以按模块定义路由接口或导航协议，由 app 壳统一实现；也可以使用 Jetpack Navigation、多模块 API 模块、显式依赖倒置、服务发现表等。关键是避免业务模块互相直接依赖 Activity 类。

### 111. 组件化有什么好处？

组件化能降低模块耦合、支持并行开发、提升编译速度、允许模块独立运行和测试。难点是路由、依赖治理、资源命名、公共能力沉淀、版本兼容和边界控制。

### 112. 插件化原理是什么？

插件化通常通过动态加载 dex、资源和 so，并 hook Activity/Service 启动流程，让未安装 APK 的组件能运行。关键技术包括 ClassLoader、AssetManager 资源加载、Instrumentation/AMS hook、代理 Activity。缺点是兼容成本高、系统限制多、稳定性和审核风险高。

### 113. PathClassLoader 和 DexClassLoader 有什么区别？

PathClassLoader 通常加载已安装 APK 的 dex，Android 应用默认使用它。DexClassLoader 可加载外部 dex/apk/jar，常用于插件化和热修复。新系统对外部代码加载和存储路径有更多限制。

### 114. 插件化资源 id 冲突如何解决？

可通过独立 Resources/AssetManager 加载插件资源，或在构建期为插件分配独立 packageId，或做资源重写。核心是避免宿主和插件使用同一资源 id 空间导致查找错误。

### 115. 热修复原理是什么？

代码热修复常见思路是 dex 插桩或 dexElements 排序，让补丁类优先加载。资源热修复通过替换或追加资源包、hook AssetManager。Tinker 偏全量 dex/资源差分修复，Robust 偏方法级插桩跳转。热修复要考虑兼容性、签名、安全、冷启动生效和补丁回滚。

### 116. Tinker 和 Robust 的差异是什么？

Tinker 通过生成补丁包并在运行时合成/加载新的 dex、资源和 so，覆盖面广但通常需要重启生效。Robust 在编译期给方法插入跳转逻辑，补丁可更快生效，但侵入编译流程并增加方法调用开销。

### 117. Android 源码或常用框架中有哪些设计模式？

Builder 如 AlertDialog、OkHttp Request；观察者如 LiveData、AdapterDataObserver；适配器如 RecyclerView.Adapter；责任链如 OkHttp Interceptor、事件分发；单例如系统服务；代理如 Binder Proxy、Retrofit；工厂如 LayoutInflater。

## 五、RecyclerView、图片、三方框架

### 118. RecyclerView 缓存机制是什么？

RecyclerView 主要缓存包括 attached scrap、changed scrap、cached views、ViewCacheExtension、RecycledViewPool。屏幕内临时分离的 View 进 scrap，最近移出屏幕的进 cache，可直接复用且通常不重新 bind；更远的进 pool，复用时需要重新 bind。

### 119. 滑动 10 个再滑回去，会有几个执行 onBindView？

取决于 item 数量、屏幕可见数、cache size、pool、是否 stableId、是否数据变化。一般刚滑出不远并命中 cached views 的 item 不会重新 bind；从 RecycledViewPool 取出的 holder 会重新执行 `onBindViewHolder`。

### 120. RecyclerView 如何局部刷新？payload 是什么？

调用 `notifyItemChanged(position, payload)` 可传递局部变更信息。Adapter 的 `onBindViewHolder(holder, position, payloads)` 收到非空 payloads 时可只更新部分 UI，避免整项刷新和动画闪烁。

### 121. RecyclerView 预取是什么？

RecyclerView 的 GapWorker 会根据滚动方向预取即将出现的 item，提前执行创建和绑定，降低滑动时卡顿。嵌套 RecyclerView 中可通过 `setInitialPrefetchItemCount` 优化横向列表首屏体验。

### 122. RecyclerView 内部 ViewClick 冲突怎么处理？

点击事件由子 View 优先消费。若父容器需要处理 item 点击，可在 ViewHolder 根布局设置点击；子控件有独立点击时分别设置 listener。滑动冲突按事件分发和 NestedScrolling 处理，避免在 onBind 中重复创建大量 listener。

### 123. Glide 缓存机制是什么？

Glide 通常有活动资源、内存缓存、磁盘资源缓存、磁盘原始数据缓存多级缓存。加载时先查内存，再查磁盘，最后走网络。缓存 key 通常由 model、尺寸、transform、options、signature 等组成。

### 124. Glide 自定义 Model 处于什么阶段？

自定义 ModelLoader 负责把业务 model 转成可加载的数据源，属于加载链路的 model 到 data 阶段。常用于自定义 URL、鉴权、加密图片、本地协议等。

### 125. 图片加载和 Bitmap 优化有哪些经验？

按目标尺寸解码，避免原图进内存；使用 RGB_565 需权衡画质；列表中复用请求并取消无效请求；控制内存/磁盘缓存；大图用分片加载；避免 Bitmap 静态持有；监控 OOM、GC 和图片尺寸。

### 126. 一个圆角图片如何自定义实现？

简单做法用 Glide transform、ShapeableImageView 或 OutlineProvider。自定义 View 可在 `onDraw` 中使用 BitmapShader 配合圆角 RectF 绘制，避免每帧创建 Bitmap 和对象。

### 127. AsyncTask 为什么容易内存泄漏？

AsyncTask 常作为 Activity 内部类持有外部引用，任务执行时间超过 Activity 生命周期就会泄漏。它的线程池和行为也经历过版本变化，现代 Android 推荐协程、Executor、WorkManager 等替代。

### 128. OkHttp 拦截器有哪些？应用拦截器和网络拦截器区别是什么？

应用拦截器通过 `addInterceptor` 添加，只调用一次，可处理缓存、重试前逻辑、统一 header。网络拦截器通过 `addNetworkInterceptor` 添加，能看到真实网络请求和重定向后的请求，适合网络层调试和底层 header。OkHttp 内部还有重试、桥接、缓存、连接、请求服务等拦截器链。

### 129. OkHttp 连接池怎么实现？

OkHttp 使用 ConnectionPool 复用 RealConnection。连接按 Address、Route、协议等匹配，HTTP/2 可多路复用。空闲连接会保留一段时间并由清理任务回收，减少 TCP/TLS 握手成本。

### 130. OkHttp 如何处理 SSL？

HTTPS 请求通过 SSLSocketFactory、TrustManager、HostnameVerifier 建立 TLS 连接。OkHttp 支持证书校验、主机名校验、连接复用和证书锁定 CertificatePinner。不要在生产环境信任所有证书。

### 131. OkHttp 用到了哪些设计模式？

责任链是拦截器链，Builder 用于构建 client/request，连接池体现享元/池化思想，Call/Callback 体现命令和回调，Dispatcher 管理异步任务队列。

### 132. RxJava 怎么切换线程？

`subscribeOn` 指定上游订阅和执行线程，通常第一次生效；`observeOn` 切换下游观察线程，可多次调用。Android 中常见 `subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())`。

### 133. RxJava 自定义操作符怎么做？

可通过 `ObservableTransformer` 封装组合逻辑，或实现 `ObservableOperator`/自定义 Observable。实际项目中更常用 transformer 统一线程、错误处理、生命周期绑定。

### 134. 网络框架里如何避免内存泄漏？

请求层不要持有 Activity；回调绑定生命周期，页面销毁时取消 Call/Disposable/Job；结果通过 ViewModel 状态分发；使用弱引用不是根本方案。RxJava 可用 AutoDispose、takeUntil、CompositeDisposable，协程可用 lifecycleScope/viewModelScope。

### 135. 如何设计一个网络框架？

要考虑请求抽象、序列化、错误模型、拦截器、鉴权刷新、缓存、重试、取消、线程切换、日志、监控、Mock、文件上传下载、生命周期绑定和可测试性。不要把 UI 生命周期耦合进底层网络库。

## 六、WebView、网络与安全

### 136. HTTP 和 HTTPS 有什么区别？

HTTP 明文传输，HTTPS 在 HTTP 与 TCP 之间加入 TLS，提供加密、完整性校验和身份认证。HTTPS 需要证书校验和 TLS 握手，性能成本可通过连接复用、会话恢复、HTTP/2 等降低。

TLS 握手阶段主要使用非对称机制做身份认证和密钥协商，例如通过证书公钥验签、用 ECDHE 协商会话密钥；握手完成后，HTTP 请求和响应使用协商出的会话密钥做对称加密传输，常见如 AES-GCM、ChaCha20-Poly1305。TLS 1.2 完整握手通常是 2 个 RTT，TLS 1.3 完整握手通常是 1 个 RTT，会话恢复还可能支持 0-RTT，但有重放风险。

### 137. HTTP 1.0、1.1、2.0 有什么区别？

HTTP/1.0 默认短连接；HTTP/1.1 支持持久连接、Host、分块传输、更多缓存控制；HTTP/2 使用二进制分帧、多路复用、头部压缩和服务端推送能力，能减少队头阻塞，但 TCP 层队头阻塞仍存在。

### 138. GET、POST、PUT 有什么区别？

GET 通常用于查询，参数常在 URL，语义应安全且幂等。POST 常用于提交或创建，语义不要求幂等。PUT 通常用于整体更新资源，语义幂等。实际还要看服务端 API 约定。

### 139. Retrofit 怎么做 POST 请求？

在接口方法上使用 `@POST("path")`，请求体可用 `@Body`，表单可用 `@FormUrlEncoded + @Field`，文件上传可用 `@Multipart + @Part`。返回值通过 CallAdapter 转成 Call、RxJava、协程等类型。

### 140. WebView 和 Native 通信方式有哪些？

Native 调 JS 可用 `evaluateJavascript` 或 `loadUrl("javascript:...")`。JS 调 Native 可用 `addJavascriptInterface`、拦截 URL scheme、`WebMessagePort` 等。现代场景优先 `evaluateJavascript`，因为它异步、有返回值且不会刷新页面。

### 141. loadUrl 和 evaluateJavascript 有什么区别？

`loadUrl("javascript:...")` 兼容老版本，但无直接返回值，可能影响加载流程。`evaluateJavascript` Android 4.4 起可用，异步执行 JS 并通过回调返回结果，更适合 JSBridge。

### 142. @JavascriptInterface 有什么安全问题？

暴露给 JS 的方法可能被任意页面调用，老版本还存在反射调用风险。应只对可信页面开启接口，限制域名、协议和方法，避免暴露敏感能力，页面销毁时移除接口。

### 143. Native 如何对 H5 鉴权？

可维护白名单域名和路径，对 JSBridge 方法做权限分级；每次调用携带 token、签名、nonce、时间戳；Native 校验当前 URL、证书状态和登录态；敏感接口二次确认或服务端校验。

### 144. 为什么 JSBridge 通常不用同步方法？

WebView JS 与 Native 通信跨线程、跨语言环境，同步等待容易阻塞 UI 或 JS 线程，引起死锁和卡顿。异步调用加 callbackId 更安全，可超时、取消和统一错误处理。

### 145. WebView 秒开和性能优化怎么做？

可预创建 WebView、预热内核、复用容器、离线包、本地资源拦截、DNS/连接预取、骨架屏、减少首屏 JS、延迟非关键资源、开启缓存并监控白屏。要注意 WebView 复用时清理状态和防止内存泄漏。

### 146. WebView 加载本地图片如何考虑安全？

避免随意开启 `allowFileAccessFromFileURLs` 和 `allowUniversalAccessFromFileURLs`。本地资源应放在受控目录，通过 WebViewAssetLoader 或安全的 ContentProvider 暴露，校验路径，防止目录穿越和跨域读取。

### 147. Token 放本地如何保存？如何加密？

优先使用 Android Keystore 保存密钥，再用 AES 加密 token，或使用 EncryptedSharedPreferences/DataStore。不要硬编码密钥，不要把长期 token 明文落盘。高安全场景要结合服务端刷新 token、设备绑定和风控。

### 148. AES 和 RSA 原理有什么区别？

AES 是对称加密，加解密使用同一密钥，速度快，适合加密大量数据。RSA 是非对称加密，公钥加密私钥解密或私钥签名公钥验签，速度慢，适合密钥交换和签名。实际常用混合加密：RSA 保护 AES 密钥，AES 加密数据。

### 149. Android 和 iOS 调同一接口，一个通一个不通怎么排查？

先确认 URL、方法、header、body、签名、编码、证书、代理、DNS、环境和服务端日志。抓包对比请求差异，再检查平台特有问题，如 Android 网络安全配置、证书链、明文 HTTP 限制、Gzip、时间戳和时区。

### 150. Android 比 iOS 慢或反过来，如何排查？

拆分 DNS、TCP、TLS、请求发送、服务端处理、下载、解析、渲染各阶段耗时。用 OkHttp EventListener、Charles/Proxyman、服务端 trace、客户端埋点对齐。再看连接复用、缓存、线程调度、JSON 解析和主线程阻塞。

### 151. 抓包常用什么工具？

常见有 Charles、Proxyman、Fiddler、Wireshark、mitmproxy、Android Studio Network Inspector。HTTPS 抓包要安装信任证书，Android 7.0 后应用默认不信任用户证书，需要 network security config 配置调试信任。

## 七、性能、稳定性与工程化

### 152. ANR 是什么？如何定位？

ANR 是应用主线程长时间无响应。常见类型包括输入事件 5 秒、广播 10 秒、服务 20 秒等。定位看 traces、logcat、CPU、主线程堆栈、锁等待、Binder 调用、I/O 和系统负载。线上可结合 ANR 监控、主线程卡顿采样和关键链路埋点。

### 153. UI 卡顿如何优化？

先用 Perfetto/Systrace、FrameMetrics、Profile GPU Rendering、Layout Inspector 定位。常见优化：主线程不做 I/O 和重计算，减少布局层级，控制 overdraw，图片按需解码，RecyclerView 局部刷新，异步 diff，避免频繁 requestLayout/invalidate。

### 154. 启动优化怎么做？

区分冷启动、温启动、热启动，统计 Application、首 Activity、首帧和可交互时间。优化方向：延迟非关键初始化、按需加载、异步初始化、ContentProvider 懒加载、减少主线程 I/O、优化布局和图片、控制类加载和反射。

### 155. 如果首页就要用到初始化能力怎么办？

把初始化拆成最小可用核心和非关键扩展。核心同步完成，非核心异步或首帧后执行。使用依赖拓扑控制顺序，并设置超时、降级和监控，避免所有 SDK 都阻塞启动。

### 156. 性能优化做过哪些方面可以回答？

可按启动、卡顿、内存、包体、网络、电量、稳定性展开。最好给出背景、指标、工具、改动和结果，例如“冷启动 P95 从 1800ms 降到 1200ms，通过延迟 SDK、移除主线程 I/O、首屏布局扁平化实现”。

### 157. 内存泄漏常见场景和处理方式？

常见有静态持有 Activity、Handler 延迟消息、匿名内部类、未注销监听、Dialog/PopupWindow、WebView、Bitmap、单例持有 Context、协程/Rx 未取消。定位用 LeakCanary、MAT、Android Studio Profiler，修复关键是生命周期解绑。

### 158. 稳定性优化可以做什么？

包括崩溃治理、ANR 治理、OOM 监控、灰度发布、降级开关、异常兜底、线程和资源监控、启动保护、日志和链路追踪。稳定性要有指标，如 crash-free、ANR 率、启动失败率。

### 159. Bugly 日志收集原理是什么？

崩溃 SDK 通常注册 Java UncaughtExceptionHandler、Native signal handler 和 ANR 监控，采集堆栈、设备、线程、日志和自定义键值，落盘后择机上传。不同 SDK 实现细节不同，核心是异常捕获、现场保存和上报聚合。

### 160. APK 打包过程是什么？

大致流程：资源编译与链接生成 R、AIDL/源码编译、Kotlin/Java 编译、字节码转换 dex、资源和 dex 打包、签名、zipalign。现代构建还包含 R8 混淆压缩、资源 shrink、增量构建和 App Bundle。

### 161. class 文件由什么组成？常量池有什么？

class 文件包含魔数、版本、常量池、访问标志、类索引、父类索引、接口、字段、方法和属性。常量池保存字符串、类/方法/字段符号引用、数字常量、NameAndType 等。

### 162. 自动装箱发生在编译期还是运行期？

装箱和拆箱由编译器在编译期插入 `Integer.valueOf`、`intValue` 等调用，实际对象创建或缓存命中发生在运行期。要注意包装类型比较、空指针和频繁装箱带来的性能问题。

### 163. Gradle、分包和项目搭建经验怎么回答？

可讲多模块拆分、buildSrc/Convention Plugin 统一配置、依赖版本管理、productFlavors、多渠道、R8、资源压缩、CI 构建缓存、动态特性和 multidex。分包重点是控制方法数、按模块治理依赖，避免只靠 multidex 兜底。

### 164. Compose 了解多少？

Compose 是声明式 UI 框架，UI 由状态驱动，状态变化触发重组。核心概念包括 Composable、State、remember、Modifier、重组、SlotTable。性能关注稳定参数、减少不必要重组、列表 key、状态下沉。

## 八、业务场景题

### 165. 登录成功跳转新 Activity，中间涉及什么？

从点击事件开始，经事件分发触发登录请求；网络层异步请求，成功后更新登录态和 token；主线程发起 Activity 跳转；系统通过 AMS/ATMS 处理任务栈和生命周期；新 Activity 创建并渲染。要注意防重复点击、请求取消、token 安全、异常兜底和页面栈清理。

### 166. 如果开发一个音频播放功能，怎么着手？

先明确场景：在线播放、本地播放、后台播放、列表续播、锁屏控制、耳机事件。技术上可选 ExoPlayer/Media3，处理音频焦点、前台服务、通知栏、缓存、弱网重试、进度同步、生命周期、权限和异常恢复。

### 167. 视频播放如何预加载避免 loading？

可使用播放器缓存、预加载下一个视频的媒体源、提前 DNS/TLS/连接、分片缓存、首帧占位、码率自适应和播放队列管理。要控制缓存大小、网络策略和列表滑动取消。

### 168. 如何实现一个下载功能接口？

接口应支持开始、暂停、恢复、取消、进度、错误、校验和状态查询。实现上用 OkHttp/DownloadManager/WorkManager，支持断点续传 Range、临时文件、MD5/SHA 校验、通知栏和网络约束。

### 169. 如何求当前 Activity View 树深度？

从 `window.decorView` 开始递归遍历 ViewGroup，当前深度加一，取所有子节点最大值。注意跳过 null，避免在非主线程访问 View 树。

### 170. 如何实现长按事件？

在 ACTION_DOWN 时记录坐标并 postDelayed 一个长按 Runnable；MOVE 超过 touch slop 或 ACTION_UP/ACTION_CANCEL 时取消；延迟到达且手指仍按下则触发长按。系统 View 已内置 `setOnLongClickListener`。

### 171. 如何封装字符串转数字工具？

要处理 null、空串、正负号、空白字符、非法字符、溢出和默认值。可返回 Result/nullable，避免直接抛异常影响业务。面试算法版可参考 `atoi`，逐字符累积并在乘 10 前判断溢出。

## 九、算法题高频答案

### 172. 一个大致有序的数组如何排序，最快复杂度？

如果每个元素距离最终位置不超过 k，可用大小为 k+1 的小顶堆，时间复杂度 O(n log k)，空间 O(k)。如果只是整体接近有序，插入排序在近乎有序时表现很好，最好接近 O(n)。

### 173. 删除有序数组中的重复元素怎么做？

双指针。慢指针指向去重后最后一个位置，快指针扫描数组；遇到新值就写到 `++slow`。时间 O(n)，空间 O(1)。无序数组可用 HashSet 或先排序。

### 174. 反转数组怎么做？

左右双指针交换，`left++`、`right--`，直到相遇。时间 O(n)，空间 O(1)。

### 175. 链表求和怎么做？

若低位在前，双指针遍历两个链表和进位，逐位创建新节点。若高位在前，可先压栈或反转链表。注意最后进位。

### 176. 二叉树最大深度怎么求？

递归：`maxDepth(root) = max(maxDepth(left), maxDepth(right)) + 1`，空节点为 0。也可 BFS 层序遍历计层数。

### 177. K 个一组反转链表怎么做？

用哑节点，每次检查后续是否有 k 个节点，有则局部反转并连接前后段，不足 k 个保持原样。关键是保存 groupPrev、groupNext 和反转后的头尾。

### 178. 二叉树每一层最左边节点怎么找？

BFS 层序遍历，每层第一个节点就是最左节点。DFS 也可按深度优先访问左子树，第一次到达某深度时记录节点。

### 179. 不同面值硬币求满足金额的最小个数怎么做？

动态规划。`dp[0]=0`，其他初始化为无穷；对每个金额 i，遍历硬币 coin，如果 `i >= coin`，则 `dp[i] = min(dp[i], dp[i-coin]+1)`。最终 `dp[amount]` 仍无穷表示无法凑出。

### 180. 斐波那契台阶怎么做？

一次走 1 或 2 阶时，`f(n)=f(n-1)+f(n-2)`。用两个变量滚动计算，时间 O(n)，空间 O(1)。注意 n 为 0、1、2 的边界。

### 181. 生产者消费者模型怎么写？

推荐 BlockingQueue：生产者 put，消费者 take，队列负责阻塞和唤醒。手写可用 synchronized wait/notifyAll 或 ReentrantLock Condition，核心是队列满时生产者等待，队列空时消费者等待。

## 十、开放题和项目题回答框架

### 182. 项目难点怎么回答？

按 STAR 结构：背景是什么、难点在哪里、你做了什么、结果如何。技术上要讲权衡和指标，不只讲“我参与了”。例如性能优化要给工具、数据和收益。

### 183. 为什么考虑换工作？

回答应正向：希望接触更复杂业务、更高工程挑战、更成熟团队或更大用户规模。避免抱怨前公司、同事或薪资。表达“现阶段成长诉求和目标岗位匹配”。

### 184. 你在团队中是什么角色？

可从业务交付、技术方案、质量保障、协作推进、新人辅导几个角度讲。最好配具体例子，如推动组件化、治理崩溃、统一网络库或优化构建。

### 185. 技术选型怎么考虑？

从业务目标、开发效率、性能、稳定性、维护成本、团队熟悉度、生态、迁移成本和风险兜底评估。不要只说“流行”或“别人都用”。

### 186. 有什么要问面试官？

可以问团队当前业务重点、技术栈、质量指标、岗位期望、入职后 3 到 6 个月目标、协作方式。避免一上来只问加班和薪资，薪资可在 HR 阶段谈。

### 187. 如何评价一个 App 当前的问题？

可从用户路径、性能稳定性、交互一致性、内容/业务转化、可访问性、包体和耗电分析。回答要具体且建设性，例如“某页面首屏信息密度高但操作入口不够清晰，可以通过埋点验证转化影响”。

### 188. 平时如何学习新技术？

可回答：官方文档和源码优先，结合技术博客、开源项目、会议分享，再通过 demo 或项目落地验证。最近读过的文章要能讲出核心观点和对项目的影响。
