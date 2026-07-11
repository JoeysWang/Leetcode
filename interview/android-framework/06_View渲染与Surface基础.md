# View 渲染与 Surface 基础：从 traversal 到屏幕上的一帧

## 0. 先建立分层

```text
View 树：决定内容与布局
  → ViewRootImpl：组织 measure/layout/draw
  → HWUI：记录 RenderNode/DisplayList
  → RenderThread/GPU：栅格化并写入 Surface buffer
  → BufferQueue/BLAST：提交 buffer
  → SurfaceFlinger：组合各 layer
  → Hardware Composer / Display：显示
```

`Canvas.drawXxx()` 不一定当场把像素画到屏幕；硬件加速下，UI 线程多数是在记录绘制命令，RenderThread 才负责真正渲染。WMS 管窗口和 layer 元数据，SurfaceFlinger 管最终合成。

---

## 第一层：入口 API

### 1.1 触发布局和绘制

| API/事件 | 主要影响 |
|---|---|
| `requestLayout()` | 当前 View 及父链标记需要布局，下一帧可能重新 measure/layout |
| `invalidate()` | 标记需要重绘，通常不主动触发 measure/layout |
| `setLayoutParams()` | 通常触发 requestLayout，可能改变测量约束 |
| 属性动画 | 每帧更新属性并触发重绘/RenderNode 属性变化 |
| 窗口 resize/Insets | VRI 收到 WMS 回调后安排 traversal |

最终汇入：

```text
ViewRootImpl.scheduleTraversals()
  → Choreographer CALLBACK_TRAVERSAL
  → ViewRootImpl.doTraversal()
  → performTraversals()
```

### 1.2 Measure

```text
performMeasure(childWidthMeasureSpec, childHeightMeasureSpec)
  → DecorView.measure()
  → View.measure()
  → onMeasure()
  → setMeasuredDimension()
```

`MeasureSpec` 由 size + mode 编码：`EXACTLY`、`AT_MOST`、`UNSPECIFIED`。父容器负责生成对子 View 的约束；子 View 给出 measured dimension 和 state。一次 traversal 中可能多次 measure，不能在 `onMeasure()` 做副作用或重活。

### 1.3 Layout

```text
performLayout()
  → host.layout(0, 0, measuredWidth, measuredHeight)
  → View.layout()
  → ViewGroup.onLayout()
  → child.layout(l, t, r, b)
```

layout 确定相对父容器的位置。`translationX/Y` 等渲染属性通常不改变 layout bounds，因此视觉位置与点击/布局语义要分场景理解。

### 1.4 Draw

View 绘制的经典顺序可概括为：

```text
背景
  → 必要时保存/裁剪
  → onDraw()
  → dispatchDraw() 绘制 children
  → 前景、滚动条、overlay 等装饰
```

硬件加速路径：

```text
View.updateDisplayListIfDirty()
  → 记录 RenderNode display list
  → ThreadedRenderer.draw()
  → RenderThread 渲染
  → syncAndDrawFrame()
```

软件绘制则可能对 Surface lock Canvas，直接在 CPU buffer 上画，再 unlock/post。

---

## 第二层：关键类

| 类 | 职责 |
|---|---|
| `ViewRootImpl` | traversal、Surface 生命周期、窗口尺寸/Insets、向渲染器提交帧 |
| `View` / `ViewGroup` | 测量、布局、绘制与子树分发 |
| `RenderNode` | 一棵 View/子树的硬件加速绘制命令和可动画属性 |
| `RecordingCanvas` | 把绘制操作记录进 RenderNode |
| `ThreadedRenderer` | Java 侧 HWUI 渲染入口，连接 RenderProxy/RenderThread |
| `RenderThread` | native 独立渲染线程，执行 display list、GPU 工作与 buffer 提交 |
| `Surface` | buffer producer 的 Java 句柄，可供 Canvas/HWUI/媒体写入 |
| `SurfaceControl` | 控制合成 layer 的句柄和事务接口 |
| `BLASTBufferQueue` | 现代窗口 buffer 与 SurfaceControl transaction 同步提交机制 |
| `SurfaceFlinger` | 消费可见 layer buffer、选择合成方式并提交显示 |
| `Hardware Composer` | 利用显示硬件 plane 完成 device composition |

### 2.1 Surface 创建/变化链

窗口首次 relayout 时，WMS 创建或配置 `SurfaceControl`，客户端 VRI 获得可写 Surface/BLAST 连接。VRI 根据 Surface validity、尺寸、格式变化初始化或更新 `ThreadedRenderer`，之后才能提交真实内容。

```text
VRI relayoutWindow()
  ⇢ WMS relayout
  → SurfaceControl / BLAST 配置
  ⇠ 返回尺寸、Insets、surface state
  → VRI updateSurface/renderer
  → performDraw
```

### 2.2 UI Thread 与 RenderThread

- UI Thread：运行应用代码，构建/更新 View 树，执行 measure/layout，记录需要更新的 display list；
- RenderThread：消费 RenderNode，准备 GPU 命令、栅格化、提交 buffer；
- 属性动画若能完全在 RenderNode/RenderThread 上运行，可减少 UI 线程参与，但布局型动画仍会触发主线程 traversal；
- UI 线程与 RenderThread 会在帧同步点交互，任一侧过慢都可能 miss deadline。

### 2.3 SurfaceView 与普通 View

普通 View 画进宿主窗口同一个 Surface。`SurfaceView` 为内容创建独立 Surface/layer，通过打洞或现代同步 layer 机制与宿主组合，视频/相机/游戏可直接向它生产 buffer。

优点：独立生产、适合高吞吐；代价：生命周期、Z-order、变换、截图、动画同步更复杂。`TextureView` 把内容作为纹理参与普通 View 合成，变换方便但多一层 GPU 采样并受宿主窗口渲染约束。

---

## 第三层：核心数据结构

### 3.1 `MeasureSpec`

一个 int 的高位 mode + 低位 size。核心不是背位运算，而是能解释父子约束协商：

- 精确尺寸 → `EXACTLY`；
- wrap_content 在有限父空间 → `AT_MOST`；
- 某些滚动/自定义布局维度可能给 `UNSPECIFIED`。

### 3.2 RenderNode / DisplayList

每个 View 可对应 RenderNode，保存：

- 绘制命令列表；
- bounds、matrix、alpha、translation、elevation、clip；
- 是否需要重新录制。

没有 invalidate 的子树可复用旧 display list；只改 translation/alpha 时可能只更新节点属性，不必重跑 `onDraw()`。

### 3.3 BufferQueue

概念角色：

```text
Producer（App/HWUI/Codec）
  dequeueBuffer → 写入 → queueBuffer

Consumer（通常 SurfaceFlinger）
  acquireBuffer → 合成/显示 → releaseBuffer
```

多缓冲允许生产与显示流水化。buffer 个数、fence 和 backpressure 共同限制生产速度；不是无限队列。

### 3.4 Fence

同步 fence 表示 GPU/硬件何时完成对 buffer 的读写。Acquire fence 防止消费者过早读取，release/present 相关 fence 协调 buffer 复用和显示完成。错误等待或 GPU 长任务会表现为 RenderThread/SurfaceFlinger 时间拉长。

### 3.5 SurfaceControl.Transaction

把多个 layer 的 position、crop、matrix、alpha、z、reparent、visibility 等变化原子提交给 SurfaceFlinger。窗口转场需要让 buffer 与 layer 属性在正确帧同步，BLAST 的重要目标正是减少 resize/transaction 不同步导致的闪烁和拉伸。

### 3.6 FrameTimeline

现代 Perfetto 用 expected/actual timeline 表示一帧预期时限与实际完成，关联 App、SurfaceFlinger 和 display。判断 jank 不能只看“是否超过 16.67 ms”：高刷屏时预算更小，且应用与 SF 各有 deadline。

---

## 第四层：异常场景

### 4.1 过度绘制与大面积 invalidate

同一像素被背景、容器、子 View 多次覆盖会增加 GPU 成本；无差别 invalidate 根节点导致大量 display list 重录。先移除无意义背景、缩小失效范围，再用 GPU overdraw/debug tools 验证，不能仅凭层级深就判断性能差。

### 4.2 Layout thrashing

在 `onLayout/onMeasure` 中修改 LayoutParams/requestLayout，或监听布局后再次改变约束，会触发重复 traversal。症状是同帧多次 measure/layout、`requestLayout during layout` 警告和主线程帧超时。

### 4.3 `Surface` 无效或被销毁

Activity 停止、窗口移除、SurfaceView 生命周期变化后继续 lock/post 或让 native producer 写旧 Surface，会抛异常或黑屏。生产者必须跟随 `SurfaceHolder.Callback`/生命周期切换目标，并安全停止线程。

### 4.4 Buffer dequeue 阻塞

消费者未及时 release、GPU 慢、buffer 数耗尽或生产快于显示，producer 在 dequeue/swap buffers 等待。此时 UI `onDraw()` 可能不长，但 RenderThread slice 很长。Perfetto 同时看 RenderThread、GPU、FrameTimeline、SurfaceFlinger。

### 4.5 黑屏、闪屏与尺寸不同步

- 首 buffer 未到，starting window 已移除；
- buffer 尺寸与 layer crop/transform 不一致；
- SurfaceControl transaction 与 buffer 不在同一帧；
- alpha/visibility/z-order 错误；
- 安全窗口被截屏时主动变黑不是渲染失败。

### 4.6 软件层/硬件层误用

`setLayerType(HARDWARE)` 会创建可缓存的离屏 layer，不等于“开启整个窗口硬件加速”；大 View layer 占显存且更新时重绘昂贵。软件 layer 也可能触发额外 bitmap 和上传。只在动画/兼容需求下有证据地使用。

### 4.7 SurfaceView 同步问题

宿主 View 移动与独立 Surface transaction 不同步可出现短暂错位；圆角、alpha、裁剪或转场在旧版本/设备上行为差异大。现代 AOSP 用 SurfaceSyncGroup/BLAST 等改善，但仍应在目标设备验证。

---

## 面试追问

### `invalidate()` 一定调用 `onDraw()` 吗？

不保证“立刻且只调用一次”。请求会合并到帧 traversal；View 不可见、被裁剪、display list 可复用或窗口无 Surface 时，实际行为会不同。

### Surface、SurfaceControl、SurfaceFlinger 的区别？

Surface 是生产 buffer 的入口；SurfaceControl 控制 layer 元数据；SurfaceFlinger 消费 buffer 并合成所有可见 layer。

### 为什么 UI 线程不忙仍然掉帧？

RenderThread/GPU、buffer backpressure、SurfaceFlinger/HWC、调度抢占或频率变化都可能超时。需要沿整条 FrameTimeline 定位。

## 源码阅读锚点

- `frameworks/base/core/java/android/view/ViewRootImpl.java`
- `frameworks/base/core/java/android/view/View.java`
- `frameworks/base/core/java/android/view/ThreadedRenderer.java`
- `frameworks/base/graphics/java/android/graphics/RenderNode.java`
- `frameworks/base/core/java/android/view/Surface.java`
- `frameworks/native/libs/gui/`
- `frameworks/native/services/surfaceflinger/`

