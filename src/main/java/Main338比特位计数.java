

public class Main338比特位计数 {


    public int[] countBits(int num) {
        int[] result = new int[num + 1];
        result[0] = 0;
        for (int i = 1; i <= num; i++) {
            result[i] = count(i);
        }

        return result;
    }

    private int count(int value) {
        int count = 0;
        while (value != 0) {
            count++;
            value = value & (value - 1);
        }
        return count;
    }

    public static void main(String[] args) {
        test();
    }

    public static void test() {
        TestUtil.reset();
        Main338比特位计数 solution = new Main338比特位计数();

        // 测试用例1: 示例用例 num=2 → [0,1,1]
        TestUtil.assertArrayEquals(new int[]{0, 1, 1}, solution.countBits(2), "示例: num=2");

        // 测试用例2: 示例用例 num=5 → [0,1,1,2,1,2]
        TestUtil.assertArrayEquals(new int[]{0, 1, 1, 2, 1, 2}, solution.countBits(5), "示例: num=5");

        // 测试用例3: 边界用例 num=0 → [0]
        TestUtil.assertArrayEquals(new int[]{0}, solution.countBits(0), "边界: num=0");

        // 测试用例4: 边界用例 num=1 → [0,1]
        TestUtil.assertArrayEquals(new int[]{0, 1}, solution.countBits(1), "边界: num=1");

        // 测试用例5: 普通用例 num=8
        TestUtil.assertArrayEquals(new int[]{0, 1, 1, 2, 1, 2, 2, 3, 1}, solution.countBits(8), "普通: num=8");

        TestUtil.printSummary();
    }
}
