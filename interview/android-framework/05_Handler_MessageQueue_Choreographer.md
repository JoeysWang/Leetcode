# Handler、MessageQueue、Choreographer：主线程为何既能等消息又能追帧

## 0. 两套调度不要混为一谈

```text
普通任务：Handler → MessageQueue → Looper → Handler.dispatchMessage

帧任务：VSYNC → Choreographer → input/animation/traversal/commit callbacks
                         ↘ 底层仍借助主 Looper/MessageQueue 唤醒执行
```

Handler 是投递/分发器，MessageQueue 是定时消息容器并桥接 native poll，Looper 是单线程消费循环；Choreographer 是按显示 VSYNC 编排一帧阶段的调度器。

本文主线描述 Android 14/15 及长期稳定的经典 MessageQueue。Android 17 对队列内部并发结构的重做，见 [Android 17 Handler / MessageQueue 重做面试笔记](../android17_handler_messagequeue_interview.md)。API 心智模型仍然成立，但不能把经典“有序单链表 + synchronized”说成 Android 17 永久实现。

---

## 第一层：入口 API

### 1.1 创建 Looper 与 Handler

```java
Looper.prepare();
Handler handler = new Handler(Looper.myLooper(), callback);
Looper.loop();
```

主线程由 `ActivityThread.main()` 调用 `Looper.prepareMainLooper()` 并最终 `Looper.loop()`。每个线程最多一个 Looper；一个 Looper 对应一个 MessageQueue；多个 Handler 可以绑定同一个 Looper。

构造 Handler 时应显式传 Looper，避免在线程上下文变化时绑定错误或旧式隐式构造报错。

### 1.2 投递消息

```text
post(Runnable)
sendMessage(Message)
sendMessageDelayed(...)
sendMessageAtTime(...)
  → enqueueMessage(queue, msg, uptimeMillis)
  → MessageQueue.enqueueMessage()
```

`postDelayed(1000)` 使用基于 uptime 的时钟语义；设备 deep sleep 期间 uptime 不增长，所以它不是精确定时器，也不适合持久任务。

### 1.3 消费消息

```text
Looper.loop()
  → loopOnce()
  → queue.next()
  → msg.target.dispatchMessage(msg)
      ├─ msg.callback.run()
      ├─ Handler.Callback.handleMessage()
      └─ Handler.handleMessage()
  → msg.recycleUnchecked()
```

分发优先级是 Runnable callback、Handler.Callback、子类 `handleMessage()`。Looper 循环不是 busy loop；没有到期消息时进入 native poll 休眠。

### 1.4 帧调度入口

```text
View.invalidate()/requestLayout()
  → ViewRootImpl.scheduleTraversals()
  → Choreographer.postCallback(CALLBACK_TRAVERSAL, ...)

ValueAnimator/动画系统
  → CALLBACK_ANIMATION

Choreographer.FrameDisplayEventReceiver
  ← DisplayEventReceiver 收到 VSYNC
  → scheduleVsync()/onVsync()
  → doFrame(frameTimeNanos, frameData)
```

应用可使用 `Choreographer.postFrameCallback()` 取得下一帧回调，但业务动画优先用官方动画 API，它们已经处理帧时间和生命周期。

---

## 第二层：关键类

| 类 | 职责 |
|---|---|
| `Looper` | 线程局部消息循环；取消息、分发、日志/observer、慢消息检测 |
| `MessageQueue` | 保存 pending 消息、同步屏障、IdleHandler；通过 native poll 等待 fd/超时 |
| `Message` | 可复用消息节点，含 target/callback/what/obj/when/flags |
| `Handler` | 把 Message/Runnable 投到指定队列并在该 Looper 线程分发 |
| `HandlerThread` | 启动后自动 prepare/loop 的后台线程 |
| `Choreographer` | ThreadLocal 帧编排器，订阅 VSYNC 并执行分阶段 callbacks |
| `DisplayEventReceiver` | native display event 与 Java 回调桥梁 |
| `ViewRootImpl` | 注册 traversal callback、消费输入并驱动一棵 View 树 |

### 2.1 MessageQueue 与 native poll

经典逻辑：

```text
MessageQueue.next()
  ├─ 找到到期同步/异步消息 → 返回
  ├─ 计算 nextPollTimeoutMillis
  ├─ nativePollOnce(ptr, timeout)
  ├─ 被新头消息、fd、VSYNC 或超时唤醒
  └─ 重新检查队列
```

native 层基于 Looper/epoll 等机制同时等待 Binder/输入/显示事件相关 fd 和定时唤醒。Java 消息队列不是简单地 `Thread.sleep()`。

### 2.2 为什么新消息能唤醒

经典 `enqueueMessage()` 将消息按 `when` 插入队列。如果插入成为新的队首，或同步屏障下插入了最早可执行异步消息，并且队列正 blocked，则调用 `nativeWake()` 唤醒 poll 重新计算等待时间。

### 2.3 同步屏障

屏障是一条 `target == null` 的特殊 Message：

```text
[barrier] → sync A → async VSYNC → sync B
              跳过       先执行
```

ViewRootImpl 安排 traversal 时设置屏障，下一帧完成后移除。异步消息可越过屏障，保证显示/输入等关键事件不被普通同步消息完全压住。屏障是隐藏系统机制；遗失屏障会让同步消息长期饥饿。

### 2.4 `Choreographer.doFrame()` 阶段

典型顺序：

```text
CALLBACK_INPUT
  → CALLBACK_ANIMATION
  → CALLBACK_INSETS_ANIMATION
  → CALLBACK_TRAVERSAL
  → CALLBACK_COMMIT
```

不同版本内部细节会变，但核心顺序是先处理影响画面的输入，再推进动画，再 measure/layout/draw，最后提交收尾。这样同一帧的动画值能被 traversal 使用。

---

## 第三层：核心数据结构

### 3.1 `Message`

关键字段：

```text
what / arg1 / arg2 / obj
callback: Runnable
target: Handler
when: 到期 uptime
flags: in-use / asynchronous
next: 经典实现链表指针
```

`Message.obtain()` 使用静态池减少频繁分配。分发后框架回收，业务不能继续持有并再次使用正在回收的 Message。

### 3.2 经典有序消息链表

队列按 `when` 升序；队首到期即可执行。插入/删除/has 最坏 `O(N)`，取队首 `O(1)`，并用队列锁保护多生产者与单消费者。Android 17 的新实现改用无锁入站结构和 Looper 私有堆，详见延伸文章。

### 3.3 ThreadLocal 绑定

`Looper.sThreadLocal` 保证一个线程一个 Looper；`Choreographer` 也使用 ThreadLocal，因此 `Choreographer.getInstance()` 返回当前 Looper 线程的实例，不是整个进程唯一实例。

### 3.4 CallbackQueue

Choreographer 为不同 callback type 保存按 dueTime 排序的 `CallbackRecord` 链。VSYNC 到达后，`doCallbacks(type, frameTime)` 摘取到期记录执行并回收。

### 3.5 IdleHandler

当没有立即可执行消息时，MessageQueue 调用已登记的 `IdleHandler.queueIdle()`：返回 `false` 自动移除，返回 `true` 保留。Idle 不等于后台、不卡帧或永远会执行；队列持续繁忙时它可能迟迟没有机会。

---

## 第四层：异常场景

### 4.1 Handler 内存泄漏

延迟 Message 持有 Handler，非静态内部 Handler/Runnable 又持有 Activity，导致窗口销毁后仍不可回收。解决是缩短任务生命周期、在销毁时移除明确 token/callback、避免捕获大对象；静态 Handler + WeakReference 不是所有场景的万能模板。

### 4.2 主线程消息过长

一条 callback 执行 100 ms 时，队列中后续输入、生命周期和帧消息都无法并行。消息数量多与单条耗时长都可能卡顿。用 Looper slow log、Perfetto 主线程 slice、StrictMode 和调用栈定位，而不是只看队列长度。

### 4.3 同步屏障未移除

普通同步消息全部卡住，异步帧消息却仍可能运行，表现诡异。系统内部用 token 成对 post/remove；业务反射调用屏障 API 风险极高。

### 4.4 `quit()` 与 `quitSafely()`

- `quit()`：队列终止并丢弃未处理消息；
- `quitSafely()`：处理已到期消息，移除未来消息后退出；
- 主 Looper 不允许正常退出；
- HandlerThread 退出后再发送通常返回 false，不能假设任务一定执行。

### 4.5 VSYNC 到了但掉帧

可能是主线程上一条消息未结束、Runnable 调度延迟、RenderThread/GPU 慢、buffer dequeue 阻塞或 SurfaceFlinger 合成超时。`doFrame` 慢只是其中一层；必须结合 UI thread、RenderThread、FrameTimeline 与 SurfaceFlinger 看。

### 4.6 错误使用 `postDelayed`

进程死亡后消息丢失；deep sleep 改变相对时间；长延迟持有对象；系统不保证精确执行。跨进程/持久任务用 WorkManager、AlarmManager 或服务端调度，按业务语义选择。

---

## 面试追问

### MessageQueue 没消息时主线程为什么不 ANR？

它在 native poll 中休眠，不消耗 CPU；有消息/fd/VSYNC 时被唤醒。ANR 判断的是应响应的事件超时，不是线程必须一直运行。

### `invalidate()` 为什么不会立刻 draw？

它标记脏区并把 traversal 合并到下一次 Choreographer 帧阶段；这样同一帧多次变化只绘制一次。

## 源码阅读锚点

- `frameworks/base/core/java/android/os/Looper.java`
- `frameworks/base/core/java/android/os/MessageQueue.java`
- `frameworks/base/core/java/android/os/Handler.java`
- `frameworks/base/core/java/android/view/Choreographer.java`
- `frameworks/base/core/jni/android_os_MessageQueue.cpp`
- `system/core/libutils/Looper.cpp`

