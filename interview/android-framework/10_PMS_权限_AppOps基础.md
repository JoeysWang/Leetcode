# PMS、权限、AppOps 基础：从“包已安装”到“这次操作能否执行”

## 0. 三个系统问题

| 系统 | 回答的问题 |
|---|---|
| PMS / PackageManager | 设备装了哪些包、组件和签名是什么、Intent 匹配谁、每个用户是否启用 |
| PermissionManager | UID/包是否被授予某项声明权限，权限保护级别和运行时授权是什么 |
| AppOps | 某 UID/包/attribution 当前能否执行具体操作，并记录 note/start/finish 使用状态 |

权限与 AppOps 经常共同保护一个 API，但不能互换：权限更像静态/授予资格；AppOps 增加用户策略、前后台模式、审计和持续操作管理。

---

## 第一层：入口 API

### 1.1 查询包与解析组件

```text
PackageManager（ApplicationPackageManager）
  → ActivityThread.getPackageManager()
  → IPackageManager Proxy
  ⇢ Binder ⇢ PackageManagerService
  → Computer snapshot 查询
```

常见 API：

```java
getPackageInfo()
getApplicationInfo()
queryIntentActivities()/resolveActivity()
getInstalledPackages()      // 受包可见性过滤
checkPermission()
getPackageUid()
```

Android 11+ 包可见性意味着“安装了”不等于调用者能查询到；`<queries>`、Intent 交互、同 UID/系统权限等决定可见集合。

### 1.2 安装 APK

应用/商店侧典型入口：

```text
PackageInstaller.createSession(params)
  → Session.openWrite()/fsync()
  → Session.commit(statusReceiver)
  ⇢ PackageInstallerService/PackageInstallerSession
  → 校验、解析、签名/版本/空间/用户策略
  → staged 或普通安装提交
  → PMS scan/reconcile/commit package state
  → dexopt/数据目录/权限/组件与广播收尾
```

安装不是把 APK 复制到目录这么简单。需处理 split、签名继承、shared library、回滚/staged session、增量安装、用户状态、SELinux/data 目录和原子性。

### 1.3 运行时权限

```text
Activity.requestPermissions()
  → PermissionController UI/系统流程
  → PermissionManagerService 更新用户授权状态
  → 回调 onRequestPermissionsResult / Activity Result API

Context.checkSelfPermission()
  → checkPermission(pid, uid)
  → PermissionManager/PMS 权限状态
```

请求 API 只是发起用户决策，不保证弹窗：已拒绝、不再询问、策略固定、自动重置、一次性权限、目标版本兼容都可能改变结果。

### 1.4 AppOps 入口

系统服务保护敏感操作时常见：

```text
权限检查
  + AppOpsManager.noteOp()/noteProxyOp()       // 一次性使用
  + startOp() ... finishOp()                   // 持续操作
  + checkOp()                                  // 只检查，不记一次使用
```

例如相机、麦克风、定位、读取剪贴板等在不同版本会结合 permission、AppOps、前后台、attribution chain 和传感器隐私开关。不能只看 Manifest permission。

---

## 第二层：关键类

### 2.1 PMS 与安装

| 类 | 职责 |
|---|---|
| `PackageManagerService` | 包扫描、解析结果提交、查询与组件解析的核心服务 |
| `PackageInstallerService` | 管理安装 session、installer 身份和历史 |
| `PackageInstallerSession` | 一次安装事务的文件、参数、校验、状态和 commit |
| `InstallPackageHelper` | PMS 安装/扫描/提交相关流程拆分 |
| `DeletePackageHelper` | 卸载与用户数据/全局包状态处理 |
| `Computer` | 新版 PMS 的只读快照查询接口，降低大锁竞争并保证一致视图 |
| `Settings` | PMS 持久化包、shared user、权限/组件等设置的核心管理 |
| `PackageParser2` / parsing 包 | 解析 AndroidManifest、split 和组件声明 |

### 2.2 权限

| 类/组件 | 职责 |
|---|---|
| `PermissionManagerService` | 权限定義、授予/撤销、检查与用户状态 |
| `PermissionManager` | 应用/系统侧权限管理 API 门面 |
| PermissionController 模块 | 运行时授权 UI、自动重置、权限使用等可更新系统组件能力 |
| `AccessCheckingService` 等新架构 | 新版本逐步承载访问策略状态与决策，内部会持续演进 |
| `DevicePolicyManagerService` | 企业/设备所有者对权限和应用策略的控制 |

### 2.3 AppOps

| 类 | 职责 |
|---|---|
| `AppOpsManager` | op 常量、mode 与客户端 API |
| `AppOpsService` | UID/package/op 状态、访问记录、watcher、active op 管理 |
| `AttributedOp` 等内部记录 | 按 attribution tag 追踪访问、拒绝和持续 operation |
| `PermissionChecker` | 组合 permission、AppOps、代理数据源等检查的便捷入口 |

### 2.4 为什么还要核对 UID 与包名

Binder 驱动可信地提供 calling UID，但 packageName 是客户端参数。system_server 收到 `(uid, packageName)` 时必须验证该包属于此 UID；涉及 attributionSource 还需校验链和 token。否则攻击者可冒充拥有不同 AppOps 状态的包。

---

## 第三层：核心数据结构

### 3.1 `AndroidPackage` 与 `PackageState`

可以把 PMS 状态分成：

```text
AndroidPackage：解析 APK 得到的相对静态清单模型
  packageName、version、usesPermissions、activities/services/providers、signing...

PackageStateInternal/PackageSetting：系统保存的安装状态
  appId、codePath、flags、签名/更新关系、每用户状态...

PackageUserState：某 user 下 installed/enabled/stopped/suspended/hidden 等
```

同一个 APK 对 user 0 可安装启用，对工作资料用户可未安装/被停用。查询必须带 userId，不能只看全局包记录。

### 3.2 `Settings` 持久化

PMS 会在 `/data/system/` 相关 XML/目录中保存包设置、运行时权限等状态，具体文件随版本模块化。开机先读取持久化状态，再扫描 system/data APK 并 reconcile；扫描不是每次都从零相信 APK。

### 3.3 Intent Resolver

PMS 为 Activity/Service/Receiver/Provider 的 intent filter 建立 resolver 索引。解析时综合：

- action/category/data scheme/host/path/MIME；
- exported、permission、enabled、user state；
- preferred/default/role/domain verification；
- instant app、visibility、cross-profile/cross-user；
- 调用 flags 与 target SDK 兼容。

### 3.4 权限保护级别

- `normal`：安装时自动授予，风险较低；
- `dangerous`：通常需要运行时用户授权；
- `signature`：调用者签名需与定义者/平台认可关系匹配；
- privileged、development、role、installer、appop 等 flags 进一步限定；
- `signatureOrSystem` 已过时，不应作为现代设计答案。

权限通常按 UID 评估，多包共享 UID 会共享安全边界。`sharedUserId` 已被弃用并限制新增使用，因为升级、签名和隔离风险很大。

### 3.5 AppOps mode

常见 mode：

```text
MODE_ALLOWED     允许
MODE_IGNORED     静默拒绝/返回空或降级结果（由调用 API 决定）
MODE_ERRORED     拒绝并可能抛 SecurityException
MODE_DEFAULT     回落到默认/权限语义
MODE_FOREGROUND  仅在 UID 满足前台条件时允许
```

AppOps 状态可按 UID、package、op、attribution tag 组织。持续操作必须 `startOp`/`finishOp` 成对，否则隐私指示、活跃记录和审计会错误。

### 3.6 签名与更新

`SigningDetails` 不只是当前证书，还可能含签名轮换 lineage。应用更新必须满足包名、版本策略和签名能力/继承规则；简单比较一个 `signature.toCharsString()` 可能无法正确处理证书轮换与多 signer。

---

## 第四层：异常场景

### 4.1 安装失败

| 错误类别 | 常见原因 |
|---|---|
| 签名不兼容 | 更新包证书不匹配、lineage 不合法 |
| 版本降级 | versionCode 降低且无调试/系统允许 |
| split 无效 | base 缺失、split name/ABI/density/config 不一致 |
| 空间/文件失败 | staging、data、dexopt 空间不足或 I/O 错误 |
| shared user/library | 签名、ABI、shared library 依赖不满足 |
| 用户/策略阻止 | DPM、未知来源、installer allowlist、设备限制 |

安装 commit 应对调用方返回结构化 status；不能只解析一行 logcat。

### 4.2 明明授权仍然 `SecurityException`

可能还缺：AppOps 允许、前台状态、正确 FGS type、角色、特殊访问、用户解锁、传感器隐私开关、URI grant、导出组件权限或 attribution 合法性。沿服务端检查顺序看第一个拒绝点。

### 4.3 `checkSelfPermission()` 通过但拿不到数据

AppOps 可能是 ignored/foreground，API 选择返回空数据而不是抛异常；系统隐私开关也可提供虚拟/静音结果。权限通过只回答一层条件。

### 4.4 包可见性导致“未安装”假象

`getPackageInfo()`/query 返回 NameNotFound 或空，但目标实际安装。检查 `<queries>`、显式 Intent 交互与是否真的需要广泛 `QUERY_ALL_PACKAGES`；后者是高敏权限且受商店政策限制。

### 4.5 多用户与工作资料

包全局存在不代表当前 user installed/enabled；跨用户 API 需要系统权限，工作资料还受 profile owner 和 cross-profile filter。日志必须打印 userId，不能只打印 uid/package。

### 4.6 运行时权限被自动撤销或一次性到期

长期未使用、用户手动修改、系统升级、策略变化、一次性授权结束都可能改变状态。应用每次敏感操作前按需检查，并能处理拒绝/降级，不缓存“永久已授权”布尔值。

### 4.7 AppOps start/finish 不配对

异常分支漏 `finishOp()` 会留下活跃操作；重复 finish 或 attribution 不一致又导致状态错乱。用 token/AutoCloseable 风格封装 finally，生命周期结束时确保收口。

---

## 一次敏感 API 的标准判定模型

```text
Binder callingUid 是否可信
  → packageName 是否属于该 UID
  → 组件是否 exported / 调用用户是否允许
  → Manifest/runtime/signature permission 是否授予
  → AppOps 当前 mode 是否允许（含 foreground）
  → 角色、DPM、传感器隐私、特殊访问等策略
  → note/start 操作并执行
  → 持续操作结束时 finish
```

具体 API 未必包含每一层，但这个顺序能帮助排查“有权限仍失败”。

## 面试追问

### PMS 与 PackageInstaller 谁负责安装？

PackageInstaller 管面向调用者的 session、数据写入和 commit 事务；最终包解析、系统状态 reconcile/commit、组件查询等核心权威状态由 PMS 体系维护，二者协作而非二选一。

### Permission 和 AppOps 有什么区别？

Permission 决定调用身份是否具备某项权限；AppOps 对更细的实际操作做模式控制、前后台限制和使用记录。同一 API 常需两者都通过。

### 为什么权限按 UID，但 AppOps 还要 packageName？

一个 UID 可能关联多个包，且审计/策略需区分 package 与 attribution；服务端必须验证 uid-package 归属，防止冒充。

## 源码阅读锚点

- `frameworks/base/services/core/java/com/android/server/pm/PackageManagerService.java`
- `frameworks/base/services/core/java/com/android/server/pm/PackageInstallerSession.java`
- `frameworks/base/services/core/java/com/android/server/pm/Computer.java`
- `frameworks/base/services/core/java/com/android/server/pm/Settings.java`
- `frameworks/base/services/core/java/com/android/server/pm/permission/`
- `frameworks/base/services/core/java/com/android/server/appop/AppOpsService.java`
- `frameworks/base/core/java/android/app/AppOpsManager.java`

