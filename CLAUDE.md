# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Personal LeetCode algorithm practice and interview prep repo, organized around labuladong's *算法小抄* (algorithm handbook).

- **Language**: Java 17, no external dependencies (pure JDK)
- **IDE**: IntelliJ IDEA (`.iml` + `.idea/`)
- **Build system**: None — compile directly with `javac`
- **All comments and documentation are in Chinese**

## Build and Run

Every solution file is a standalone program with its own `main()`. No build tool, no test framework, no linter.

### Compile and run a single file

```bash
javac -encoding UTF-8 src/main/java/TestUtil.java src/main/java/<File>.java -d /tmp/testout
cd /tmp/testout && java -Dfile.encoding=UTF-8 <ClassName>
```

### Files using shared data structures (`data.ListNode`, `data.TreeNode`, `data.Node`)

```bash
javac -encoding UTF-8 src/main/java/data/*.java src/main/java/TestUtil.java src/main/java/<File>.java -d /tmp/testout
```

### Packaged files (in `sort/`, `tree/`, `review/`, etc.)

```bash
cd src/main/java && javac -encoding UTF-8 <package>/<File>.java -d /tmp/testout
cd /tmp/testout && java <package>.<ClassName>
```

Always use `-encoding UTF-8` — source files contain Chinese characters.

## Testing

No test framework. Each solution has a `main()` with a `test()` method using `TestUtil` (custom assertion class at `src/main/java/TestUtil.java`).

- `TestUtil.reset()` before tests, `TestUtil.printSummary()` after
- Available assertions: `assertEquals`, `assertArrayEquals`, `assertArrayEquals2D`, `assertTrue`, `assertFalse`
- `printSummary()` calls `System.exit(1)` on any failure

Run any file directly to execute its tests.

## Architecture

### Study workflow

The repo follows a three-layer learning structure:

1. **labuladong章节复习题单.md** — chapter-by-chapter review checklist mapping the book to LeetCode problems, with self-test prompts and recommended problem order
2. **review/** — skeleton classes per chapter (`Chapter00MustReadReview.java` through `Chapter05ComputerBasicsReview.java`) with methods that `throw todo()`. Fill in implementations during practice. Answers live in `review/answer/`
3. **Solution files** — one Java file per problem in `src/main/java/` (flat) and subdirectories for grouped topics

### Source layout

- `src/main/java/Main*.java` — flat LeetCode solutions, named `Main<number><Chinese title>.java` (e.g., `Main206反转链表.java`). Class name matches filename, no package.
- `src/main/java/tree/` — tree problems organized by subtopic (basic, traversal, path, bst, lca, build, dp). `tree/README.md` has the recommended solving order.
- `src/main/java/sort/` — sorting algorithm implementations
- `src/main/java/data/` — shared data structures: `ListNode`, `TreeNode`, `Node`
- `src/main/java/review/` — chapter review skeletons (methods throw `todo()`) and `answer/` subdirectory with completed implementations
- `src/main/java/offer/` — Sword refers to Offer problems
- `src/main/java/wirte/` — concurrency examples (deadlock, producer-consumer, locks)
- `interview/` — Markdown interview notes organized by chapter (Chinese), with `README.md` as index and quick-reference for algorithm templates

### Naming conventions

- Solution files: `Main<number><Chinese problem title>.java`
- Class names match filenames (no package for flat files)
- Each file: `main()` for standalone execution, typically a `test()` method
