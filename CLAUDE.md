# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 在此仓库中操作代码时提供指导说明。

## 项目概述

个人 LeetCode 算法练习与面试准备仓库，围绕 labuladong 的《算法小抄》组织。

- **语言**：Java 17，无外部依赖（纯 JDK）
- **IDE**：IntelliJ IDEA（`.iml` + `.idea/`）
- **构建系统**：无 — 直接使用 `javac` 编译
- **所有注释和文档均使用中文**

## 构建与运行

每个题解文件都是带有独立 `main()` 的完整程序。无构建工具、无测试框架、无代码检查器。

### 编译并运行单个文件

```bash
javac -encoding UTF-8 src/main/java/TestUtil.java src/main/java/<File>.java -d /tmp/testout
cd /tmp/testout && java -Dfile.encoding=UTF-8 <ClassName>
```

### 使用公共数据结构（`data.ListNode`、`data.TreeNode`、`data.Node`）的文件

```bash
javac -encoding UTF-8 src/main/java/data/*.java src/main/java/TestUtil.java src/main/java/<File>.java -d /tmp/testout
```

### 带包名的文件（位于 `sort/`、`tree/`、`review/` 等目录下）

```bash
cd src/main/java && javac -encoding UTF-8 <package>/<File>.java -d /tmp/testout
cd /tmp/testout && java <package>.<ClassName>
```

始终使用 `-encoding UTF-8` — 源文件中包含中文字符。

## 测试

无测试框架。每个题解都有 `main()` 方法，其中调用 `test()` 方法并使用 `TestUtil`（自定义断言类，位于 `src/main/java/TestUtil.java`）进行测试。

- 测试前调用 `TestUtil.reset()`，测试后调用 `TestUtil.printSummary()`
- 可用断言方法：`assertEquals`、`assertArrayEquals`、`assertArrayEquals2D`、`assertTrue`、`assertFalse`
- `printSummary()` 在有任何失败时会调用 `System.exit(1)`

直接运行任意文件即可执行其测试。

## 架构

### 学习工作流

本仓库遵循三层学习结构：

1. **labuladong章节复习题单.md** — 按章节组织的复习清单，将书中内容映射到 LeetCode 题目，包含自测提示和推荐做题顺序
2. **review/** — 每章对应的骨架类（`Chapter00MustReadReview.java` 至 `Chapter05ComputerBasicsReview.java`），方法体为 `throw todo()`。练习时填入具体实现。答案位于 `review/answer/` 目录
3. **题解文件** — 每道题一个 Java 文件，平铺在 `src/main/java/` 下，或按专题分组放在子目录中

### 源码目录结构

- `src/main/java/Main*.java` — 平铺的 LeetCode 题解，命名格式为 `Main<题号><中文题名>.java`（如 `Main206反转链表.java`）。类名与文件名一致，无包名。
- `src/main/java/tree/` — 树相关题目，按子主题组织（基础、遍历、路径、二叉搜索树、最近公共祖先、构建树、树形DP）。`tree/README.md` 中有推荐做题顺序。
- `src/main/java/sort/` — 排序算法实现
- `src/main/java/data/` — 公共数据结构：`ListNode`、`TreeNode`、`Node`
- `src/main/java/review/` — 章节复习骨架（方法抛出 `todo()`），`answer/` 子目录存放已完成的实现
- `src/main/java/offer/` — 剑指 Offer 题目
- `src/main/java/wirte/` — 并发编程示例（死锁、生产者-消费者、锁机制）
- `interview/` — 按章节组织的 Markdown 面试笔记（中文），`README.md` 作为索引和算法模板速查表

### 命名规范

- 题解文件：`Main<题号><中文题名>.java`
- 类名与文件名一致（平铺文件无包名）
- 每个文件：包含用于独立运行的 `main()` 方法，通常还有一个 `test()` 方法
