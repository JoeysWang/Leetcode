# Binder Java 层：从 AIDL 调用到 Binder 线程池

## 0. 一句话模型

Java Binder 是 Binder 驱动之上的对象化 RPC：客户端 `Proxy` 把方法参数写入 `Parcel`，调用 `transact()`；服务端 Binder 线程从驱动取事务，`Stub.onTransact()` 解包并调用真实实现，再把结果写回 reply。

```text
Client thread
  → IXXX.Proxy.method()
  → Parcel.write...
  → BinderProxy.transact()
  → JNI / libbinder / binder driver
  ⇢ target process Binder thread
  → Binder.execTransactInternal()
  → IXXX.Stub.onTransact()
  → service method
  → reply Parcel / exception
```

Binder 解决跨进程调用、身份传递、对象引用和死亡通知，但不自动解决线程安全、超时、业务重试与大数据传输。

---

## 第一层：入口 API

### 1.1 AIDL 接口调用

```aidl
interface IBookService {
    Book getBook(int id);
    oneway void notifyChanged(int id);
}
```

生成代码的稳定结构：

```text
IInterface
  ├─ Default
  └─ Stub extends Binder
       ├─ asInterface(IBinder)
       ├─ onTransact(code, data, reply, flags)
       └─ Proxy implements IBookService
```

客户端拿到 `IBinder` 后调用 `Stub.asInterface()`：

- `queryLocalInterface()` 命中：服务和客户端同进程，直接返回 Stub/本地对象，不走序列化；
- 未命中：返回 Proxy，后续才是真正 Binder IPC。

### 1.2 注册和查询服务

系统服务典型入口：

```text
ServiceManager.addService(name, binder)  // 服务注册
ServiceManager.getService(name)          // 客户端查询
Stub.asInterface(rawBinder)               // 转类型接口
```

普通应用的 bound service 则由 AMS 维护发布与连接关系，客户端在 `ServiceConnection.onServiceConnected()` 得到 `IBinder`。

### 1.3 `transact()` 的同步与 oneway

- 默认同步调用：调用线程阻塞，直到服务端返回或失败；
- `oneway`：客户端把异步事务提交给驱动后返回，没有 reply；同一 Binder node 的异步事务保持串行语义，但不能把它当无界消息队列；
- Binder 没有通用业务超时参数。同步卡住通常只能从调用设计、线程隔离或上层 watchdog 处理。

### 1.4 手写 Binder 的入口

不使用 AIDL 时也可以：

```java
data.enforceInterface(DESCRIPTOR);
data.writeInt(...);
remote.transact(TRANSACTION_xxx, data, reply, 0);
reply.readException();
```

AIDL 的价值是稳定生成 transaction code、序列化、接口 token、异常和版本相关样板，减少协议错误。

---

## 第二层：关键类

| 类 | 位置与职责 |
|---|---|
| `IBinder` | Binder 对象通用协议：`transact`、`linkToDeath`、`queryLocalInterface` |
| `Binder` | Java 服务端本地 Binder；连接 JNI native Binder 对象 |
| `BinderProxy` | Java 客户端远程 Binder 代理；代表另一个进程的 Binder node 引用 |
| `IInterface` | 强类型 Binder 接口的基类，`asBinder()` 返回底层句柄 |
| AIDL `Stub` | transaction code 分发与参数解包 |
| AIDL `Proxy` | 参数打包、发起 transact、读取 reply |
| `Parcel` | 紧凑 IPC 序列化容器，不是通用持久化格式 |
| `Parcelable` | 自定义对象写入/读出 Parcel 的契约 |
| `ServiceManager` | `servicemanager` 的 Java 客户端门面和缓存 |
| `RemoteException` | 远程 Binder 调用基础异常；系统 API 常转为运行时异常或系统服务特定异常 |

### 2.1 客户端 Proxy 伪代码

```java
Parcel data = Parcel.obtain(asBinder());
Parcel reply = Parcel.obtain();
try {
    data.writeInterfaceToken(DESCRIPTOR);
    data.writeInt(id);
    mRemote.transact(TRANSACTION_getBook, data, reply, 0);
    reply.readException();
    return reply.readTypedObject(Book.CREATOR);
} finally {
    reply.recycle();
    data.recycle();
}
```

### 2.2 服务端 Stub

```text
Binder thread
  → Binder.execTransactInternal()
  → Stub.onTransact(code, data, reply, flags)
      ├─ enforceInterface(DESCRIPTOR)
      ├─ read 参数 / enforceNoDataAvail()
      ├─ 调用真实 service method
      └─ writeNoException() + write 返回值
```

服务方法默认运行在 Binder 线程池，不是服务进程主线程。只有本地接口优化时，调用会直接发生在客户端当前线程，这种“同一接口因部署方式改变线程模型”的差异必须在设计时考虑。

### 2.3 Java 到 native

Java 层通过 JNI 连接 native libbinder，再与 `/dev/binder` 家族驱动通信。概念上：

```text
BinderProxy.transactNative()
  → native IPCThreadState::transact()
  → ioctl(BINDER_WRITE_READ)
  → Binder driver 路由 transaction
```

面试不要求背每个 JNI 函数，但要知道 Java `BinderProxy` 不是 socket 客户端，真正调度和引用管理由 native/驱动完成。

### 2.4 Binder 线程池与嵌套调用

- 进程会向驱动注册 Binder 线程；线程池大小受 runtime/libbinder 与进程配置约束；
- 同一服务可能被多个 Binder 线程并发调用，服务状态必须加锁或转单线程；
- 同步 Binder 支持嵌套调用：A 调 B，B 回调 A 时可能由等待线程或其他 Binder 线程处理；锁顺序错误非常容易跨进程死锁；
- App 主线程也可以直接发同步 Binder，一旦服务慢就会卡 UI。

---

## 第三层：核心数据结构

### 3.1 `Parcel`

Parcel 内部主要是连续数据区加 Binder 对象偏移表。可写入：

- primitive、String、数组、Bundle；
- Parcelable 的字段数据；
- Binder 强/弱引用和文件描述符；
- typed object/list。

Parcel 使用位置游标读取，读写顺序必须一致。它可能包含进程相关 Binder/FD 语义，因此不适合作为长期磁盘格式。

### 3.2 handle、node 与 Binder 引用

客户端看到的远程 Binder 在 Java 层是 `BinderProxy`，native/驱动侧通过 handle 指向服务端 node。把一个 Binder 写入 Parcel，相当于把“可调用对象引用”跨进程传递，驱动负责转换引用语义，而不是复制服务对象。

### 3.3 transaction code 与 flags

- `FIRST_CALL_TRANSACTION + n`：AIDL 方法 code；
- `INTERFACE_TRANSACTION`：查询 descriptor；
- `FLAG_ONEWAY`：异步无 reply；
- `FLAG_ACCEPTS_FDS` 等内部/版本相关 flag。

code 只是一个接口内分发编号；双方接口版本不兼容会表现为未知 transaction、字段错读或语义差异。稳定 AIDL 会提供更严格的版本管理。

### 3.4 调用身份

驱动随事务传递 calling pid/uid。服务端可通过：

```java
Binder.getCallingUid();
long token = Binder.clearCallingIdentity();
try {
    // 以当前服务进程身份访问其他资源
} finally {
    Binder.restoreCallingIdentity(token);
}
```

`clearCallingIdentity()` 不是提权 API，而是避免把上游调用者身份错误传播给下游检查；必须在最小作用域并保证 finally 恢复。

### 3.5 死亡通知

```java
binder.linkToDeath(recipient, flags);
// 远程承载进程死亡 → binderDied()
binder.unlinkToDeath(...);
```

本地 Binder 不会以同样方式死亡；注册与死亡之间存在竞态，要把 `DeadObjectException` 和初次 `linkToDeath` 失败一起处理。

---

## 第四层：异常场景

### 4.1 `TransactionTooLargeException`

Binder 事务缓冲区是进程共享的有限资源，并非“单次精确 1 MB 就安全”。并发事务、reply、Bundle 嵌套都会占用。Activity state、Intent extras 和大列表最常中招。

修复：只传 ID/分页数据；大 blob 用文件描述符、共享内存或内容 URI；不要仅捕获异常重试同样大数据。

### 4.2 `DeadObjectException`

远程进程已经死亡或 Binder node 不可用。一次失败不代表可直接对同一 Proxy 无限重试；应丢弃旧引用、重新绑定/查询服务，并保证操作幂等。

### 4.3 Binder 线程池耗尽

服务端所有 Binder 线程都阻塞于锁、I/O 或反向同步调用，新事务排队；调用方主线程可能连锁 ANR。诊断时同时抓客户端阻塞栈和服务端 Binder 线程栈，Perfetto 看 `binder transaction`/`binder reply` 与调度延迟。

### 4.4 跨进程死锁

```text
App main 持有 L1 → 同步调用 System
System Binder 持有 L2 → 同步回调 App
App 回调需要 L1 / 主线程执行
```

避免持锁做同步 IPC；回调尽量 oneway/异步；明确全局锁顺序；系统服务尤其不能在重量级 global lock 下调用不可信 App Binder。

### 4.5 权限和身份漏洞

- 只相信客户端传入的 packageName，不核对 UID；
- 清除 calling identity 后才做权限检查；
- 回调异步执行时误以为仍保留原 Binder calling identity；
- 导出 Binder 服务但未做 permission/AppOps/用户隔离。

### 4.6 oneway 洪泛与顺序误解

oneway 不阻塞 reply，但事务仍占驱动和服务端资源。高频大 oneway 可填满异步空间或饿死处理。顺序保证只应在明确的同一 Binder node/发送关系内理解，跨对象、跨线程业务顺序仍要自建序号或确认机制。

---

## 面试追问

### Binder 调用服务端在哪个线程？

远程调用通常在服务端 Binder 线程池；同进程 `asInterface()` 命中本地 Stub 时就是调用者当前线程。因此接口实现必须同时考虑并发与本地优化。

### Binder 与 Handler 的关系？

Binder 负责跨进程把调用送到 Binder 线程；Handler 负责进程内切换到特定 Looper。Framework 常在 Binder Stub 中只封装消息，再交主线程/服务线程执行。

## 源码阅读锚点

- `frameworks/base/core/java/android/os/IBinder.java`
- `frameworks/base/core/java/android/os/Binder.java`
- `frameworks/base/core/java/android/os/BinderProxy.java`
- `frameworks/base/core/java/android/os/Parcel.java`
- `frameworks/native/libs/binder/`
- `drivers/android/binder.c`（内核树版本路径可能不同）

