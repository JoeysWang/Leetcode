# labuladong 算法小抄章节复习题单

来源：`/Users/joeys/IdeaProjects/Leetcode/labuladong的算法小抄（修订）.pdf`

使用方式：

1. 先做「套路自测」：不看代码，口述状态定义、选择、边界、遍历顺序或数据结构不变量。
2. 再做「LeetCode 实战」：每小节先挑 1 道基础题，能稳定 AC 后再做进阶题。
3. 复盘时只记三件事：题型识别信号、核心模板、最容易写错的边界条件。

说明：第五章偏计算机基础，和算法题不是一一对应关系，我只给出可用的 Shell、SQL、字符串匹配类平台题。

## 第零章、必读系列

### 学习算法和刷题的思路指南

- 套路自测：用「数组/链表」解释栈、队列、哈希表、堆、树、图的底层存储；分别写出数组、链表、二叉树、N 叉树、图的遍历框架。
- LeetCode 实战：104 Maximum Depth of Binary Tree；226 Invert Binary Tree；543 Diameter of Binary Tree；124 Binary Tree Maximum Path Sum。

### 学习数据结构和算法读什么书

- 套路自测：给一个新题，先判断它主要考「数据结构选型」还是「算法范式」；说明你会用哪些标签拆题。
- LeetCode 实战：20 Valid Parentheses；206 Reverse Linked List；146 LRU Cache；200 Number of Islands。

### 动态规划解题套路框架

- 套路自测：给定零钱兑换问题，写出状态、选择、base case、状态转移方程；再把自顶向下改成自底向上。
- LeetCode 实战：70 Climbing Stairs；322 Coin Change；509 Fibonacci Number；300 Longest Increasing Subsequence。

### 动态规划答疑篇

- 套路自测：解释「最优子结构」「重叠子问题」「状态压缩」分别怎么判断；说明备忘录和 DP table 的等价关系。
- LeetCode 实战：198 House Robber；213 House Robber II；72 Edit Distance；1143 Longest Common Subsequence。

### 回溯算法解题套路框架

- 套路自测：写出路径、选择列表、结束条件三要素；说明如何用撤销选择恢复现场。
- LeetCode 实战：46 Permutations；77 Combinations；78 Subsets；39 Combination Sum。

### 二分查找解题套路框架

- 套路自测：分别写出查找一个数、左边界、右边界的二分模板；解释 `left`、`right` 是闭区间还是半开区间。
- LeetCode 实战：704 Binary Search；34 Find First and Last Position of Element in Sorted Array；35 Search Insert Position；875 Koko Eating Bananas。

### 滑动窗口解题套路框架

- 套路自测：写出扩大右边界、收缩左边界、更新答案的位置；说明何时用 `need/window` 计数。
- LeetCode 实战：76 Minimum Window Substring；3 Longest Substring Without Repeating Characters；438 Find All Anagrams in a String；567 Permutation in String。

### 双指针技巧总结

- 套路自测：区分左右指针、快慢指针、同向指针三类题；说明每类指针移动的单调性来源。
- LeetCode 实战：141 Linked List Cycle；142 Linked List Cycle II；167 Two Sum II - Input Array Is Sorted；15 3Sum。

### BFS 算法套路框架

- 套路自测：写出队列层序 BFS 模板；说明 `visited` 什么时候入队时标记，什么时候出队时标记会出错。
- LeetCode 实战：111 Minimum Depth of Binary Tree；102 Binary Tree Level Order Traversal；752 Open the Lock；773 Sliding Puzzle。

### Linux 的进程、线程、文件描述符是什么

- 套路自测：解释进程、线程、文件描述符的关系；说出 socket 为什么也可以看作文件描述符。
- LeetCode 实战：本节不对应算法题；可选做 192 Word Frequency；193 Valid Phone Numbers；194 Transpose File；195 Tenth Line。

### Git/SQL/正则表达式的在线练习平台

- 套路自测：写出 SQL 去重、分组计数、连接查询、删除重复行的基本套路；写出常见正则字符类和锚点。
- LeetCode 实战：175 Combine Two Tables；182 Duplicate Emails；196 Delete Duplicate Emails；10 Regular Expression Matching；44 Wildcard Matching。

## 第一章、动态规划系列

### 动态规划设计：最长递增子序列

- 套路自测：分别用 `dp[i]` 和「牌堆/二分」思路求 LIS；说明两种做法的复杂度。
- LeetCode 实战：300 Longest Increasing Subsequence；354 Russian Doll Envelopes；673 Number of Longest Increasing Subsequence。

### 经典动态规划：0-1 背包问题

- 套路自测：定义 `dp[i][w]`，解释「第 i 个物品选或不选」；把二维 DP 压缩成一维并说明为什么容量要倒序遍历。
- LeetCode 实战：416 Partition Equal Subset Sum；494 Target Sum；1049 Last Stone Weight II。

### 经典动态规划：完全背包问题

- 套路自测：比较 0-1 背包和完全背包的一维遍历方向；说明「组合数」和「排列数」循环顺序有何不同。
- LeetCode 实战：322 Coin Change；518 Coin Change II；279 Perfect Squares；377 Combination Sum IV。

### 经典动态规划：子集背包问题

- 套路自测：把「能否分成等和子集」转成背包容量问题；写出布尔 DP 的状态转移。
- LeetCode 实战：416 Partition Equal Subset Sum；698 Partition to K Equal Sum Subsets；473 Matchsticks to Square。

### 经典动态规划：编辑距离

- 套路自测：定义 `dp[i][j]` 表示两个前缀的编辑距离；列出插入、删除、替换、不操作四种选择。
- LeetCode 实战：72 Edit Distance；583 Delete Operation for Two Strings；712 Minimum ASCII Delete Sum for Two Strings。

### 经典动态规划：高楼扔鸡蛋

- 套路自测：定义鸡蛋数和楼层数的状态；解释为什么朴素转移需要遍历扔鸡蛋楼层。
- LeetCode 实战：887 Super Egg Drop；375 Guess Number Higher or Lower II。

### 经典动态规划：高楼扔鸡蛋（进阶）

- 套路自测：用「操作次数 m、鸡蛋数 k 能覆盖多少楼层」重新定义状态；推导 `dp[m][k] = dp[m-1][k-1] + dp[m-1][k] + 1`。
- LeetCode 实战：887 Super Egg Drop。

### 经典动态规划：最长公共子序列

- 套路自测：定义两个字符串前缀的 LCS；说明字符相等和不等时的状态转移。
- LeetCode 实战：1143 Longest Common Subsequence；583 Delete Operation for Two Strings；1092 Shortest Common Supersequence。

### 动态规划之子序列问题解题模板

- 套路自测：比较「一维子序列 DP」和「二维两个字符串 DP」；说明什么时候 `dp[i]` 需要回看 `j < i`。
- LeetCode 实战：300 Longest Increasing Subsequence；516 Longest Palindromic Subsequence；1312 Minimum Insertion Steps to Make a String Palindrome。

### 动态规划之博弈问题

- 套路自测：定义先手、后手收益；解释为什么区间 DP 要按长度递增遍历。
- LeetCode 实战：877 Stone Game；486 Predict the Winner；1140 Stone Game II。

### 动态规划之正则表达

- 套路自测：定义 `dp[i][j]` 表示两个前缀是否匹配；说明 `.` 和 `*` 的转移分别如何处理。
- LeetCode 实战：10 Regular Expression Matching；44 Wildcard Matching。

### 动态规划之四键键盘

- 套路自测：定义第 `i` 次操作后最多字符数；解释什么时候选择连续粘贴优于直接按 A。
- LeetCode 实战：651 4 Keys Keyboard；650 2 Keys Keyboard。

### 动态规划之 KMP 字符匹配算法

- 套路自测：构造前缀函数或 DFA；说明失配时为什么不回退主串指针。
- LeetCode 实战：28 Find the Index of the First Occurrence in a String；459 Repeated Substring Pattern；686 Repeated String Match。

### 贪心算法之区间调度问题

- 套路自测：证明按结束时间排序的贪心正确性；说明「最多不重叠区间」如何转成「最少删除区间」。
- LeetCode 实战：435 Non-overlapping Intervals；452 Minimum Number of Arrows to Burst Balloons；646 Maximum Length of Pair Chain。

### 团灭 LeetCode 股票买卖问题

- 套路自测：统一定义 `dp[i][k][0/1]`；分别写出不限次数、最多两次、含冷冻期、含手续费的转移。
- LeetCode 实战：121 Best Time to Buy and Sell Stock；122 Best Time to Buy and Sell Stock II；123 Best Time to Buy and Sell Stock III；188 Best Time to Buy and Sell Stock IV；309 Best Time to Buy and Sell Stock with Cooldown；714 Best Time to Buy and Sell Stock with Transaction Fee。

### 团灭 LeetCode 打家劫舍问题

- 套路自测：解释相邻约束如何转成 `rob/not rob`；分别处理线性、环形、树形房屋。
- LeetCode 实战：198 House Robber；213 House Robber II；337 House Robber III。

## 第二章、数据结构系列

### 算法学习之路

- 套路自测：给 5 道题，按数组、链表、栈队列、树、图、哈希、堆打标签，并说明首选数据结构。
- LeetCode 实战：1 Two Sum；20 Valid Parentheses；206 Reverse Linked List；102 Binary Tree Level Order Traversal；200 Number of Islands。

### 二叉堆详解实现优先级队列

- 套路自测：写出堆的上浮、下沉、插入、删除堆顶；说明数组下标和父子节点的关系。
- LeetCode 实战：215 Kth Largest Element in an Array；347 Top K Frequent Elements；295 Find Median from Data Stream；23 Merge k Sorted Lists。

### LRU 算法详解

- 套路自测：说明为什么 LRU 需要「哈希表 + 双向链表」；写出 `get` 和 `put` 的不变量。
- LeetCode 实战：146 LRU Cache；460 LFU Cache。

### 二叉搜索树操作集锦

- 套路自测：写出 BST 查找、插入、删除、验证模板；说明中序遍历为什么有序。
- LeetCode 实战：98 Validate Binary Search Tree；700 Search in a Binary Search Tree；701 Insert into a Binary Search Tree；450 Delete Node in a BST；230 Kth Smallest Element in a BST；538 Convert BST to Greater Tree。

### 如何计算完全二叉树的节点数

- 套路自测：比较普通二叉树计数和完全二叉树计数；说明如何利用左右子树高度判断满二叉树。
- LeetCode 实战：222 Count Complete Tree Nodes。

### 特殊数据结构：单调栈

- 套路自测：写出「下一个更大元素」模板；说明栈中元素保持递增还是递减。
- LeetCode 实战：496 Next Greater Element I；503 Next Greater Element II；739 Daily Temperatures；84 Largest Rectangle in Histogram；42 Trapping Rain Water。

### 特殊数据结构：单调队列

- 套路自测：实现单调队列的 `push`、`pop`、`max`；说明过期元素如何移出窗口。
- LeetCode 实战：239 Sliding Window Maximum；862 Shortest Subarray with Sum at Least K。

### 设计 Twitter

- 套路自测：设计用户关注关系、推文时间戳、新闻流合并；说明为什么需要优先队列。
- LeetCode 实战：355 Design Twitter；23 Merge k Sorted Lists。

### 递归反转链表的一部分

- 套路自测：写出反转整个链表、反转前 N 个节点、反转区间 `[m,n]` 的递归定义。
- LeetCode 实战：206 Reverse Linked List；92 Reverse Linked List II；25 Reverse Nodes in k-Group。

### 队列实现栈 | 栈实现队列

- 套路自测：用两个栈实现队列；用队列实现栈；说明哪个操作是摊还 O(1)。
- LeetCode 实战：232 Implement Queue using Stacks；225 Implement Stack using Queues；155 Min Stack。

## 第三章、算法思维系列

### 回溯算法团灭子集、排列、组合问题

- 套路自测：分别说明子集、组合、排列的决策树差异；处理含重复元素时如何排序和剪枝。
- LeetCode 实战：78 Subsets；90 Subsets II；77 Combinations；46 Permutations；47 Permutations II；39 Combination Sum；40 Combination Sum II。

### 回溯算法最佳实践：解数独

- 套路自测：定义每个空格的选择列表；说明行、列、宫格约束如何快速检查。
- LeetCode 实战：37 Sudoku Solver；36 Valid Sudoku。

### 回溯算法最佳实践：括号生成

- 套路自测：用左右括号剩余数量定义选择；说明什么时候剪枝非法前缀。
- LeetCode 实战：22 Generate Parentheses；20 Valid Parentheses；32 Longest Valid Parentheses。

### 滑动窗口技巧

- 套路自测：针对「最长」「最短」「计数」三类窗口题，分别说明何时更新答案。
- LeetCode 实战：3 Longest Substring Without Repeating Characters；76 Minimum Window Substring；438 Find All Anagrams in a String；567 Permutation in String；424 Longest Repeating Character Replacement。

### twoSum 问题的核心思想

- 套路自测：分别用哈希表和排序双指针解决 Two Sum；扩展到 3Sum、4Sum 时如何去重。
- LeetCode 实战：1 Two Sum；167 Two Sum II - Input Array Is Sorted；15 3Sum；18 4Sum。

### 常用的位操作

- 套路自测：解释 `n & (n - 1)`、异或消除相同数、取最低位 1 的含义。
- LeetCode 实战：136 Single Number；137 Single Number II；260 Single Number III；191 Number of 1 Bits；231 Power of Two；338 Counting Bits。

### 拆解复杂问题：实现计算器

- 套路自测：设计表达式解析流程；说明如何处理括号、运算符优先级和递归子表达式。
- LeetCode 实战：224 Basic Calculator；227 Basic Calculator II；150 Evaluate Reverse Polish Notation。

### 烧饼排序

- 套路自测：用递归把最大元素翻到末尾；说明每轮最多需要几次翻转。
- LeetCode 实战：969 Pancake Sorting。

### 前缀和技巧

- 套路自测：写出一维、二维前缀和；说明「子数组和为 k」为什么要用哈希表记录历史前缀和。
- LeetCode 实战：303 Range Sum Query - Immutable；304 Range Sum Query 2D - Immutable；560 Subarray Sum Equals K；1248 Count Number of Nice Subarrays。

### 字符串乘法

- 套路自测：模拟竖式乘法，说明 `num1[i] * num2[j]` 对结果数组哪两个位置产生影响。
- LeetCode 实战：43 Multiply Strings；415 Add Strings。

### FloodFill 算法详解及应用

- 套路自测：写出 DFS/BFS 网格遍历模板；说明边界、已访问、颜色/陆地判断三类条件。
- LeetCode 实战：733 Flood Fill；200 Number of Islands；695 Max Area of Island；130 Surrounded Regions。

### 区间调度之区间合并问题

- 套路自测：按起点排序后维护当前合并区间；说明相交和不相交的判断条件。
- LeetCode 实战：56 Merge Intervals；57 Insert Interval；435 Non-overlapping Intervals。

### 区间调度之区间交集问题

- 套路自测：用双指针求两个区间列表交集；说明指针移动依据是哪一个区间先结束。
- LeetCode 实战：986 Interval List Intersections。

### 信封嵌套问题

- 套路自测：解释为什么宽度升序、高度降序后可以转成 LIS；说明高度降序如何避免同宽误选。
- LeetCode 实战：354 Russian Doll Envelopes；300 Longest Increasing Subsequence。

### 几个反直觉的概率问题

- 套路自测：解释蓄水池抽样为什么每个元素最终被选中概率相同；说明随机洗牌的等概率条件。
- LeetCode 实战：382 Linked List Random Node；398 Random Pick Index；384 Shuffle an Array；528 Random Pick with Weight。

### 洗牌算法

- 套路自测：写出 Fisher-Yates 洗牌；证明每个排列出现概率相同。
- LeetCode 实战：384 Shuffle an Array；398 Random Pick Index。

### 递归详解

- 套路自测：区分「递归函数定义」和「递归过程细节」；用前序、后序位置分别解决一道树题。
- LeetCode 实战：206 Reverse Linked List；104 Maximum Depth of Binary Tree；226 Invert Binary Tree；124 Binary Tree Maximum Path Sum。

## 第四章、高频面试系列

### 如何高效寻找素数

- 套路自测：写出埃氏筛；说明为什么从 `i * i` 开始标记合数。
- LeetCode 实战：204 Count Primes。

### 如何高效进行模幂运算

- 套路自测：写出快速幂；说明指数奇偶分治和取模防溢出。
- LeetCode 实战：50 Pow(x, n)；372 Super Pow。

### 如何运用二分查找算法

- 套路自测：把「最小可行值」类问题转成搜索答案；定义 `check(x)` 的单调性。
- LeetCode 实战：704 Binary Search；34 Find First and Last Position of Element in Sorted Array；875 Koko Eating Bananas；1011 Capacity To Ship Packages Within D Days；410 Split Array Largest Sum。

### 如何高效解决接雨水问题

- 套路自测：分别用备忘录、双指针、单调栈解接雨水；说明左右最大值的含义。
- LeetCode 实战：42 Trapping Rain Water；11 Container With Most Water。

### 如何去除有序数组的重复元素

- 套路自测：用快慢指针原地覆盖；扩展到每个元素最多保留两次。
- LeetCode 实战：26 Remove Duplicates from Sorted Array；80 Remove Duplicates from Sorted Array II；27 Remove Element；283 Move Zeroes。

### 如何寻找最长回文子串

- 套路自测：用中心扩散枚举奇偶中心；比较中心扩散和 DP 的边界处理。
- LeetCode 实战：5 Longest Palindromic Substring；647 Palindromic Substrings；516 Longest Palindromic Subsequence。

### 如何运用贪心思想玩跳跃游戏

- 套路自测：维护当前能到达的最远位置；说明 Jump Game II 中何时增加步数。
- LeetCode 实战：55 Jump Game；45 Jump Game II。

### 如何 k 个一组反转链表

- 套路自测：写出反转 `[a,b)` 区间的函数；说明如何递归连接下一组。
- LeetCode 实战：25 Reverse Nodes in k-Group；92 Reverse Linked List II；206 Reverse Linked List。

### 如何判定括号合法性

- 套路自测：分别用栈和计数器处理括号；说明多种括号和单一括号的差异。
- LeetCode 实战：20 Valid Parentheses；921 Minimum Add to Make Parentheses Valid；1541 Minimum Insertions to Balance a Parentheses String；32 Longest Valid Parentheses。

### 如何寻找缺失的元素

- 套路自测：分别用数学求和、异或、原地哈希找缺失数；说明每种方法的适用前提。
- LeetCode 实战：268 Missing Number；448 Find All Numbers Disappeared in an Array；41 First Missing Positive。

### 如何同时寻找缺失和重复的元素

- 套路自测：用索引取负或位运算找重复和缺失；说明如何避免覆盖原信息。
- LeetCode 实战：645 Set Mismatch；287 Find the Duplicate Number。

### 如何判断回文链表

- 套路自测：用快慢指针找中点，反转后半段再比较；说明奇偶长度如何处理。
- LeetCode 实战：234 Palindrome Linked List。

### 如何在无限序列中随机抽取元素

- 套路自测：推导蓄水池抽样中第 `i` 个元素被保留的概率；扩展到抽取 k 个元素。
- LeetCode 实战：382 Linked List Random Node；398 Random Pick Index。

### 如何调度考生的座位

- 套路自测：设计区间优先级，支持入座和离座；说明如何同时按距离和座位号排序。
- LeetCode 实战：855 Exam Room。

### Union-Find 算法详解

- 套路自测：实现 `find`、`union`、`connected`；解释路径压缩和按重量合并。
- LeetCode 实战：547 Number of Provinces；684 Redundant Connection；990 Satisfiability of Equality Equations。

### Union-Find 算法应用

- 套路自测：把连通性、等式约束、岛屿合并、账户合并转成并查集模型。
- LeetCode 实战：200 Number of Islands；130 Surrounded Regions；721 Accounts Merge；1584 Min Cost to Connect All Points。

### 一行代码就能解决的算法题

- 套路自测：识别数学规律题，先枚举小规模样例，再归纳不变量。
- LeetCode 实战：292 Nim Game；319 Bulb Switcher；877 Stone Game。

### 二分查找高效判定子序列

- 套路自测：为目标字符串预处理每个字符出现位置；用二分查找匹配子序列。
- LeetCode 实战：392 Is Subsequence；792 Number of Matching Subsequences。

## 第五章、计算机技术

### 关于 Linux shell 你必须知道的

- 套路自测：用 `grep`、`awk`、`sed`、`sort`、`uniq`、`head`、`tail` 组合解决文本统计题。
- LeetCode 实战：192 Word Frequency；193 Valid Phone Numbers；194 Transpose File；195 Tenth Line。

### Linux shell 的实用小技巧

- 套路自测：写出统计词频、筛选电话号码、转置文件、输出第 N 行的命令思路。
- LeetCode 实战：192 Word Frequency；193 Valid Phone Numbers；194 Transpose File；195 Tenth Line。

### 一文看懂 session 和 cookie

- 套路自测：解释 cookie、session、token 的区别；说明登录态在客户端和服务端各保存什么。
- LeetCode 实战：本节更偏 Web 基础，无直接算法题；可用 146 LRU Cache 类比服务端会话淘汰策略。

### 加密算法的前身今世

- 套路自测：区分哈希、对称加密、非对称加密、签名；说明密码存储为什么不能明文或可逆加密。
- LeetCode 实战：本节无直接算法题；可选 242 Valid Anagram；49 Group Anagrams；706 Design HashMap 练习哈希基础。

### Git/SQL/正则表达式的在线练习平台

- 套路自测：写出 SQL join、group by、having、delete duplicate 的模板；写出正则匹配中 `.`、`*`、锚点的含义。
- LeetCode 实战：175 Combine Two Tables；176 Second Highest Salary；181 Employees Earning More Than Their Managers；182 Duplicate Emails；183 Customers Who Never Order；196 Delete Duplicate Emails；197 Rising Temperature；10 Regular Expression Matching。

## 总复习抽题规则

### 每日 60 分钟版

1. 10 分钟：随机抽一个小节，口述套路和边界。
2. 35 分钟：做该小节 1 道 LeetCode 基础题。
3. 15 分钟：复盘模板、错因、可迁移题型。

### 每章验收题

1. 第零章：手写 DP、回溯、二分、滑窗、双指针、BFS 六个模板。
2. 第一章：用同一套状态定义法解释股票、打家劫舍、背包、编辑距离。
3. 第二章：从零实现 LRU、单调队列、BST 删除、链表区间反转。
4. 第三章：任选一道复杂题，先画决策树或状态图，再写代码。
5. 第四章：限时做 5 道高频题，每题先说出套路再编码。
6. 第五章：完成 Shell 4 题和 SQL 5 题，熟悉平台输入输出。

