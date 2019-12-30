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
}
