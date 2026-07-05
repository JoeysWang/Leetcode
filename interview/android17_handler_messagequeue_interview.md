# Android 17 Handler / MessageQueue 重做面试笔记

## 1. 一句话结论

Android 17 不是把应用层 `Handler` API 推倒重来，而是重做了 `Handler/Looper` 背后的 `MessageQueue` 实现。

旧实现的核心是：

```text
一个按执行时间 when 排序的单链表 + synchronized 锁
```

Android 17 新实现的核心是：

```text
Treiber lock-free stack + Looper 私有 min-heap + tombstone 延迟删除
```

也就是：

```text
生产者线程只负责无锁投递消息
Looper 线程独占负责排序、取消息、执行和物理清理
```

主要收益是减少 `MessageQueue` 锁竞争，降低主线程被后台线程阻塞的概率，改善繁忙消息队列下的插入成本、尾延迟和 UI jank。

---

## 2. 老 Handler / MessageQueue 是怎么做的

经典结构：

```text
Handler.post() / sendMessage()
        ↓
MessageQueue.enqueueMessage()
        ↓
Looper.loop()
        ↓
MessageQueue.next()
        ↓
Handler.dispatchMessage()
```

`Handler` 本身不是线程，也不是队列。它只是把 `Runnable` 或 `Message` 投递到某个 `Looper` 关联的 `MessageQueue` 中。

老 `MessageQueue` 的核心结构可以理解成：

```java
class Message {
    long when;
    Handler target;
    Runnable callback;
    Message next;
}

class MessageQueue {
    Message mMessages; // 单链表头节点
}
```

队列按 `Message.when` 从小到大排列：

```text
mMessages
   ↓
msg(when=10) -> msg(when=30) -> msg(when=80) -> null
```

入队大致逻辑：

```java
synchronized (queue) {
    if (mMessages == null || msg.when < mMessages.when) {
        msg.next = mMessages;
        mMessages = msg;
    } else {
        Message p = mMessages;
        while (p.next != null && p.next.when <= msg.when) {
            p = p.next;
        }
        msg.next = p.next;
        p.next = msg;
    }
}
```

取消息大致逻辑：

```java
synchronized (queue) {
    Message msg = mMessages;
    if (msg != null && msg.when <= now) {
        mMessages = msg.next;
        msg.next = null;
        return msg;
    }
}
```

### 老实现的复杂度

| 操作 | 老 MessageQueue |
|---|---:|
| 插入消息 | 最坏 `O(N)` |
| 取队首消息 | `O(1)` |
| 查找消息 | `O(N)` |
| 删除消息 | `O(N)` |
| 同步控制 | `synchronized` 单锁 |

---

## 3. 老实现的主要问题

### 3.1 单链表插入成本高

因为链表按 `when` 排序，所以插入一个延迟消息时，需要从头开始找合适位置。

```text
msg10 -> msg30 -> msg80 -> msg120
```

插入 `when = 100` 的消息：

```text
msg10 -> msg30 -> msg80 -> msg100 -> msg120
```

最坏要扫描整条链表，所以是 `O(N)`。

### 3.2 所有线程竞争同一把锁

多个线程都可以向主线程 `Handler` 投递消息：

```text
Thread A -> mainHandler.post()
Thread B -> mainHandler.post()
Thread C -> mainHandler.post()
UI Thread -> MessageQueue.next()
```

老实现里，这些操作都要进入同一个 `synchronized (MessageQueue)`。

这会导致：

```text
后台低优先级线程持有 MessageQueue 锁
        ↓
主线程想取下一条消息，但拿不到锁
        ↓
主线程被阻塞
        ↓
输入、动画、绘制可能延迟
        ↓
产生 jank / 掉帧
```

这个问题本质上是锁竞争和优先级反转。

### 3.3 remove / has 系列 API 需要扫描

例如：

```java
handler.removeCallbacks(runnable);
handler.hasMessages(what);
```

这些 API 的语义是从 pending message queue 里查找匹配项，因此老实现通常要线性扫描链表。

队列很大时，成本会比较明显。

---

## 4. Android 17 新 MessageQueue：DeliQueue

Android 17 的新实现把原来的“一个有序链表”拆成两个核心结构：

```text
Treiber Stack：负责多线程无锁插入
Min-heap：负责 Looper 线程本地排序和取最早消息
```

整体模型：

```text
多个生产者线程
    ↓
CAS push 到 Treiber stack
    ↓
Looper 线程批量收集新消息
    ↓
插入 Looper 私有 min-heap
    ↓
heap[0] 是最早到期消息
    ↓
到期后 dispatch 给 Handler
```

关键思想：

```text
并发写入交给 lock-free stack
时间排序交给单消费者 Looper 的 heap
删除采用 tombstone 逻辑删除
```

---

## 5. Treiber Stack 是什么

Treiber stack 是一个无锁单链表栈。

普通栈的结构是：

```text
head
 ↓
msg3 -> msg2 -> msg1 -> null
```

新消息永远插到 head 前面：

```text
push msg4

head
 ↓
msg4 -> msg3 -> msg2 -> msg1 -> null
```

如果只有一个线程写，代码可以这样：

```java
msg.next = head;
head = msg;
```

但多个线程同时写时会丢消息。

例如当前：

```text
head = H
```

两个线程同时插入：

```text
T1 想插入 A
T2 想插入 B
```

没有 CAS 时可能发生：

```text
T1 读到 head = H
T1 设置 A.next = H

T2 读到 head = H
T2 设置 B.next = H

T1 写 head = A
T2 写 head = B
```

最终：

```text
head
 ↓
B -> H
```

`A` 丢了。

Treiber stack 用 CAS 解决这个问题：

```java
do {
    oldHead = head.get();
    msg.next = oldHead;
} while (!head.compareAndSet(oldHead, msg));
```

CAS 的含义是：

```text
只有当当前 head 仍然等于我刚才看到的 oldHead 时，才把 head 改成 msg。
如果 head 已经被其他线程改过，说明我看到的信息过期了，重试。
```

刚才的例子用 CAS 后：

```text
初始：head = H

T1:
oldHead = H
A.next = H
CAS(head, H, A) 成功

当前：head = A -> H

T2:
oldHead = H
B.next = H
CAS(head, H, B) 失败，因为当前 head 已经是 A

T2 重试：
oldHead = A
B.next = A
CAS(head, A, B) 成功
```

最终：

```text
head
 ↓
B -> A -> H
```

两个消息都保住了。

### Treiber stack 在新 MessageQueue 里的作用

它只解决一个问题：

```text
让多个线程可以快速、无锁地把 Message 投递进来
```

它不负责执行顺序，也不负责按 `when` 排序。

因为 Treiber stack 是 LIFO：

```text
后插入的在前面
```

但是这没有问题，因为 Looper 后面会把消息再放进 min-heap，最终仍然按 deadline 执行。

---

## 6. Min-heap 是什么

Min-heap 就是小顶堆。

它不是完整有序数组，而是满足一个局部性质：

```text
每个父节点的 key <= 它的子节点的 key
```

在 MessageQueue 里，这个 key 主要是：

```text
Message.when
```

也可以理解成消息的 deadline。

数组表示规则：

```text
index i 的左孩子 = 2i + 1
index i 的右孩子 = 2i + 2
index i 的父节点 = (i - 1) / 2
```

例如数组：

```text
[10, 30, 20, 80, 40, 25]
```

对应树：

```text
        10
      /    \
    30      20
   /  \    /
 80   40  25
```

这是合法小顶堆，因为：

```text
10 <= 30
10 <= 20
30 <= 80
30 <= 40
20 <= 25
```

但数组本身不是完全排序的：

```text
[10, 30, 20, 80, 40, 25]
```

它不等于：

```text
[10, 20, 25, 30, 40, 80]
```

小顶堆只保证：

```text
heap[0] 一定是最小的
```

这对 Looper 已经够了，因为 Looper 每次只需要知道：

```text
下一条最早到期的 Message 是谁
```

---

## 7. Min-heap 插入和取出过程

### 7.1 插入消息

当前 heap：

```text
[10, 30, 20, 80, 40, 25]
```

插入 `when = 15` 的消息。

第一步，先放到数组末尾：

```text
[10, 30, 20, 80, 40, 25, 15]
```

树结构：

```text
        10
      /    \
    30      20
   /  \    /  \
 80   40  25  15
```

第二步，和父节点比较。

`15` 的父节点是 `20`，`15 < 20`，交换：

```text
[10, 30, 15, 80, 40, 25, 20]
```

第三步，继续和父节点比较。

`15` 的父节点是 `10`，`15 > 10`，停止。

最终：

```text
        10
      /    \
    30      15
   /  \    /  \
 80   40  25  20
```

这个过程叫：

```text
sift up / 上浮
```

复杂度：

```text
O(log N)
```

### 7.2 取出最早消息

最早消息一定在：

```text
heap[0]
```

如果：

```java
heap[0].when <= now
```

说明这条消息到期了，可以执行。

弹出堆顶时：

```text
1. 取出 heap[0]
2. 把最后一个元素挪到 heap[0]
3. 从 heap[0] 开始向下调整
```

这个过程叫：

```text
sift down / 下沉
```

复杂度：

```text
O(log N)
```

### 7.3 when 相同时怎么办

如果多个 Message 的 `when` 一样，需要稳定顺序。

通常比较规则可以理解成：

```text
先比较 when
when 相同再比较 insertSeq
```

也就是：

```text
更早到期的先执行
同一时间到期的，先入队的先执行
```

---

## 8. 删除流程：tombstone 延迟清理

Android 17 新实现里，删除分成两步：

```text
逻辑删除：其他线程 CAS 设置 removed 标志
物理清理：Looper 线程稍后从 heap / stack 中清理
```

### 8.1 为什么不让其他线程直接删 heap

因为 heap 是 Looper 线程私有维护的。

如果其他线程也直接修改 heap，会变成：

```text
后台线程 removeCallbacks() 正在改 heap 数组
Looper 线程同时 pop heap[0]
```

这又需要锁或者复杂的并发 heap，等于重新引入锁竞争。

所以新实现选择：

```text
非 Looper 线程不改 heap 结构
非 Looper 线程只把 Message 标记为 removed
Looper 线程负责真正清理结构
```

### 8.2 removed 标志只需要标一次

`stack` 和 `heap` 里存的不是两份 Message，而是同一个 Message 对象的引用。

```text
Treiber stack 里有 msgA 引用
Min-heap 里也可能有 msgA 引用

msgA.removed = false
```

删除时：

```java
msgA.removed.compareAndSet(false, true);
```

这个标志在 `Message` 对象自己身上。

所以不需要：

```text
把 stack 里的 msgA 标一遍
再把 heap 里的 msgA 标一遍
```

因为它们指向同一个对象。

### 8.3 如果消息还在 stack，没进 heap

生命周期可能是：

```text
其他线程 post msg
    ↓
msg 被 push 到 Treiber stack
    ↓
还没被 Looper 搬进 min-heap
    ↓
另一个线程 remove 了它
```

此时：

```text
msg.removed = true
msg.heapIndex = -1
```

Looper 下次收集 stack 新消息时：

```java
if (msg.removed) {
    skip;
} else {
    heap.add(msg);
}
```

这种消息根本不会进入 heap。

### 8.4 如果消息已经在 heap 里

消息进入 heap 后，可以记录自己的 heap 下标：

```java
class Message {
    long when;
    boolean removed;
    int heapIndex;
}
```

例如：

```text
heap array:

index:  0   1   2   3   4   5
when:  10  30  20  80  40  25
```

如果要删除 `when = 30` 的消息，它知道：

```text
heapIndex = 1
```

Looper 清理时可以直接定位：

```java
heap.removeAt(msg.heapIndex);
```

`removeAt(i)` 的过程：

```text
1. 用 heap 最后一个元素替换 heap[i]
2. 删除最后一个槽位
3. 对替换上来的元素做 sift up 或 sift down
```

复杂度：

```text
O(log N)
```

不需要扫描整个 heap。

### 8.5 removed list / freelist 的作用

其他线程删除消息时，除了设置：

```text
msg.removed = true
```

还可以把这个 tombstone 加入一个无锁 removed list / freelist。

```text
removedList
   ↓
msgX -> msgY -> msgZ
```

Looper 稍后处理这个 list：

```java
while ((msg = removedList.pop()) != null) {
    if (msg.heapIndex >= 0) {
        heap.removeAt(msg.heapIndex);
    }
    recycleLater(msg);
}
```

重点：

```text
Looper 处理的是被删除消息列表，不是每次扫描整个 heap。
```

如果本轮删除了 `K` 条消息，清理成本大致是：

```text
K * O(log N)
```

而不是：

```text
O(N) 全堆扫描
```

### 8.6 removeCallbacks 仍可能有 O(N) 查找成本

需要区分两个动作：

```text
A. 找到哪些消息匹配 runnable / what / token
B. 把这些消息从数据结构里移除
```

`removeCallbacks(runnable)` 的语义是：

```text
删除所有 callback == runnable 的 pending message
```

为了找出这些 message，系统仍然可能需要遍历 pending messages。

所以不能说：

```text
Android 17 后 removeCallbacks 一定是 O(1)
```

更准确的说法是：

```text
匹配查找可能仍是 O(N)
但删除线程不再直接持有全局 MessageQueue 锁修改复杂结构
真正的物理清理由 Looper 完成
heap 中的物理删除可以通过 heapIndex 做到 O(log N)
```

---

## 9. 新旧实现核心对比

| 维度 | 老 MessageQueue | Android 17 新 MessageQueue |
|---|---|---|
| 核心结构 | 按 `when` 排序的单链表 | Treiber stack + min-heap |
| 并发控制 | `synchronized` 单锁 | CAS / atomic |
| 生产者插入 | 持锁后按时间扫描链表 | CAS push 到无锁栈 |
| 插入复杂度 | 最坏 `O(N)` | 生产者 `O(1)`，Looper 摊销 `O(log N)` |
| 消费端 | Looper 也要拿同一把锁 | Looper 独占 heap，无需锁 |
| 删除方式 | 持锁扫描并摘链 | tombstone 逻辑删除，Looper 延迟清理 |
| 主线程风险 | 可能被后台线程持锁阻塞 | 显著减少锁竞争 |
| API 兼容性 | 传统实现 | `Handler` API 基本不变 |
| 反射风险 | 可能反射 `mMessages` | `mMessages` 字段保留但不代表真实队列 |

---

## 10. 为什么新实现能减少 jank

老实现的问题链路：

```text
后台线程 post 消息
    ↓
进入 synchronized(MessageQueue)
    ↓
扫描链表插入
    ↓
主线程想 next() 取消息
    ↓
主线程也要同一把锁
    ↓
主线程等待
    ↓
输入 / 动画 / 绘制延迟
    ↓
jank / missed frame
```

新实现的链路：

```text
后台线程 post 消息
    ↓
CAS push 到 Treiber stack
    ↓
无需持有 MessageQueue 大锁
    ↓
主线程 Looper 自己收集并排序
    ↓
主线程不再因为后台线程持有队列锁而阻塞
```

核心收益：

```text
1. 减少 MessageQueue 锁竞争
2. 避免低优先级后台线程阻塞高优先级 UI 线程
3. 降低繁忙队列下的插入尾延迟
4. 让 Looper 独占排序结构，避免复杂并发 heap
5. 保持 Handler API 兼容
```

---

## 11. 面试常见问题

### Q1：Android 17 是不是把 Handler 重写了？

不是严格意义上重写应用层 `Handler` API，而是重写了 `MessageQueue` 的内部实现。

`Handler.post()`、`sendMessage()`、`Looper.loop()` 这些使用方式基本不变。变化主要在底层队列结构：从老的 lock-based sorted linked list，变成 lock-free stack + Looper-local min-heap。

### Q2：老 MessageQueue 为什么会卡主线程？

因为所有线程都通过同一个 `MessageQueue` 锁操作队列。

后台线程投递消息时，如果持有队列锁，主线程 `Looper` 想取消息也要等这把锁。低优先级线程持锁时，高优先级 UI 线程被迫等待，就可能产生优先级反转和 jank。

### Q3：Treiber stack 是什么？

Treiber stack 是基于 CAS 的无锁单链表栈。

新节点插入 head 前面，但不会直接写 head，而是：

```java
do {
    oldHead = head.get();
    msg.next = oldHead;
} while (!head.compareAndSet(oldHead, msg));
```

CAS 保证只有当 head 没被别人改过时，才把新节点设置为 head。失败就重试，所以不会丢消息。

### Q4：为什么 Treiber stack 不按时间排序也可以？

因为 Treiber stack 只负责接收多线程投递的新消息，不负责最终执行顺序。

Looper 会把 stack 中的新消息搬到 min-heap 中，min-heap 以 `Message.when` 为 key，保证堆顶是最早到期的消息。

### Q5：min-heap 是完整排序数组吗？

不是。

Min-heap 只保证父节点小于等于子节点，所以 `heap[0]` 一定是最小值。

它不保证整个数组从小到大排列。

对 Looper 来说，只需要知道下一条最早到期的消息，也就是 `heap[0]`，所以小顶堆很合适。

### Q6：min-heap 插入和取出复杂度是多少？

插入：

```text
先追加到数组末尾，再 sift up
复杂度 O(log N)
```

取出堆顶：

```text
取 heap[0]，用最后一个元素补位，再 sift down
复杂度 O(log N)
```

查看最早消息：

```text
heap[0]
复杂度 O(1)
```

### Q7：删除消息时为什么用 tombstone？

因为其他线程不应该直接修改 Looper 私有的 heap。

新实现中，其他线程只做逻辑删除：

```text
CAS 设置 msg.removed = true
```

真正从 heap 或 stack 中清理，由 Looper 线程稍后完成。

这样避免多个线程同时修改复杂结构，也避免重新引入锁竞争。

### Q8：Looper 清理 removed 消息时要扫描整个 heap 吗？

通常不需要。

Message 可以记录自己的 heapIndex。Looper 清理时，如果消息已经在 heap 里，可以直接：

```java
heap.removeAt(msg.heapIndex)
```

然后做 sift up / sift down，复杂度是 `O(log N)`。

如果消息还只在 stack 里，Looper 搬运 stack 新消息时看到 `removed = true`，直接跳过，不让它进入 heap。

### Q9：removeCallbacks 现在是不是 O(1)？

不是。

`removeCallbacks(runnable)` 要找出所有 `callback == runnable` 的消息，这个匹配查找过程仍然可能需要扫描 pending messages。

Android 17 的重点优化不是让所有 remove API 变成 O(1)，而是：

```text
减少全局锁竞争
避免后台线程直接修改 Looper 的排序结构
把物理删除推迟给 Looper
heap 内删除可通过 heapIndex 做到 O(log N)
```

### Q10：对普通 App 开发有什么影响？

正常使用 `Handler` 的代码基本不需要改。

需要注意的是，不要反射 `MessageQueue` 私有字段，尤其是旧实现里的 `mMessages`。Android 17 中为了兼容可能保留字段，但它不再代表真实队列内容。

测试框架也需要升级到兼容新 MessageQueue 的版本。

---

## 12. 面试回答模板

### 12.1 简短版

Android 17 主要重做的是 `MessageQueue`，不是应用层 `Handler` API。

老实现是一个按 `when` 排序的单链表，入队、删除、查找都在同一个 `synchronized` 锁里做，插入最坏 `O(N)`，而且后台线程 post 消息时可能和主线程 Looper 竞争同一把锁，导致优先级反转和 jank。

新实现使用 DeliQueue，把并发插入和时间排序拆开。生产者线程通过 CAS push 到 Treiber lock-free stack，插入是无锁的；Looper 线程再把新消息搬到自己私有的 min-heap，按 `when` 取最早消息执行。删除时用 tombstone，其他线程只 CAS 标记 `removed`，真正的物理删除由 Looper 延迟处理。

所以主要提升是减少 MessageQueue 锁竞争，降低主线程被后台线程阻塞的概率，并改善繁忙队列下的插入性能和 UI 流畅度。

### 12.2 展开版

Android 17 的 Handler 改造核心在 MessageQueue。

旧 MessageQueue 用的是有序单链表，`mMessages` 指向链表头，所有消息按 `when` 排序。插入消息时要持有 `MessageQueue` 的 monitor 锁，然后遍历链表找到插入位置；删除和查找也需要在线性链表里扫描。这个设计简单，但在高并发投递消息时会有两个问题：第一，插入最坏是 `O(N)`；第二，所有生产者线程和 Looper 消费线程都竞争同一把锁。如果后台低优先级线程持有这个锁，主线程想取下一条 UI 消息就会被阻塞，可能导致 jank。

Android 17 的新实现叫 DeliQueue。它把队列拆成 Treiber stack 和 Looper-local min-heap。多个生产者线程只负责用 CAS 把消息 push 到无锁栈里，这个过程不需要拿全局锁；Looper 线程作为唯一消费者，会批量读取 stack 中的新消息，把没有被删除的消息插入自己的 min-heap。min-heap 以 `Message.when` 为 key，堆顶就是最早到期的消息，所以 Looper 每次只看 `heap[0]` 就能知道下一条应该执行什么。

删除方面，新实现用 tombstone。其他线程 remove 消息时，不直接修改 heap，而是通过 CAS 把 Message 的 `removed` 标志设为 true，并加入 removed list。Looper 之后处理这些 tombstone。如果消息还在 stack 里，搬运时直接跳过；如果消息已经在 heap 里，可以通过消息记录的 heapIndex 直接定位并 removeAt，再做堆调整。因此删除被拆成逻辑删除和物理清理，避免多个线程同时修改 heap。

这套设计的本质是：并发写入由 lock-free stack 承担，排序和结构修改由单消费者 Looper 承担。这样既保留了 Handler API 的兼容性，又显著降低了 MessageQueue 的锁竞争和主线程卡顿风险。

---

## 13. 关键术语速记

| 术语 | 解释 |
|---|---|
| `Handler` | 消息发送和分发入口，绑定某个 `Looper` |
| `Looper` | 在线程上循环取消息并分发 |
| `MessageQueue` | 保存 pending message 的队列 |
| `Message.when` | 消息最早执行时间 |
| Treiber stack | 基于 CAS 的无锁单链表栈 |
| CAS | Compare-And-Set，比较当前值是否等于预期值，相等才更新 |
| Min-heap | 小顶堆，堆顶是最小 key |
| Tombstone | 逻辑删除标记，不立刻物理移除 |
| `heapIndex` | Message 在 heap 数组里的位置，用于快速删除 |
| Priority inversion | 低优先级线程持锁导致高优先级线程等待 |
| Jank | UI 卡顿、掉帧 |

---

## 14. 复杂度速记

| 操作 | 老实现 | 新实现 |
|---|---:|---:|
| 生产者插入 | `O(N)`，持锁 | `O(1)` CAS push |
| Looper 排序插入 | 已在链表插入时完成 | `O(log N)` 插入 heap |
| 查看最早消息 | `O(1)` | `O(1)` |
| 弹出最早消息 | `O(1)` | `O(log N)` |
| 按条件查找删除 | 通常 `O(N)` | 匹配查找仍可能 `O(N)` |
| 已知 heapIndex 删除 | 不适用 | `O(log N)` |
| 并发控制 | monitor lock | atomic / CAS + Looper 单消费者 |

注意：新实现不是所有操作都绝对更低复杂度，而是把最容易阻塞主线程的并发入队路径从持锁扫描链表改成了无锁 O(1) push，并把排序成本转移到 Looper 私有结构中。

---

## 15. 兼容性和排查

Android 17 中，正常 `Handler` API 使用方式基本不变。

风险点主要是：

```text
1. 反射 MessageQueue 私有字段，比如 mMessages
2. 测试框架依赖旧 MessageQueue 内部结构
3. 自定义 idle 判断逻辑依赖队列链表实现
```

可以用兼容开关做 A/B 排查：

```bash
adb am compat enable USE_NEW_MESSAGEQUEUE <your-package-name>
```

关闭新实现：

```bash
adb am compat disable USE_NEW_MESSAGEQUEUE <your-package-name>
```

---

## 16. 最后总结

Android 17 的新 MessageQueue 不是简单优化一段代码，而是改变了队列内部并发模型。

老模型：

```text
所有线程共同操作一个有序链表
所以必须用一把锁保护
```

新模型：

```text
生产者无锁投递
Looper 单线程排序和消费
删除先逻辑标记，再由 Looper 物理清理
```

可以用一句话记：

```text
Android 17 用 Treiber stack 解决多线程入队锁竞争，用 min-heap 解决按时间取最早消息，用 tombstone 解决跨线程删除结构冲突。
```

这是一次面向 UI 流畅度和系统调度尾延迟的 MessageQueue 内部架构升级。

---

## 17. 参考

- Android Developers: Android 17 MessageQueue changes  
  https://developer.android.com/about/versions/17/changes/messagequeue

- Android Developers Blog: Under the hood: Android 17 lock-free MessageQueue  
  https://developer.android.com/blog/posts/under-the-hood-android-17-lock-free-message-queue

- Android API Reference: Handler  
  https://developer.android.com/reference/android/os/Handler
