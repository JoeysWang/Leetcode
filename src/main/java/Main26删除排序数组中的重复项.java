import java.util.Arrays;

public class Main26删除排序数组中的重复项 {


    public static void main(String[] args) {


        Solution solution = new Solution();

//        System.out.println(Arrays.toString(solution.removeDuplicates(
//                new int[]{0, 0, 1, 1, 2, 3, 4}
//                )));
    }

    /**
     * 双指针
     */
    static class Solution {
        public int removeDuplicates(int[] nums) {

            int i = 0;

            for (int j = 1; j < nums.length; j++) {
                if (nums[i] == nums[j]) {
                    continue;
                } else {
                    i++;
                    nums[i] = nums[j];
                }

            }


            return i + 1;
        }
    }

    static class Solution2 {


        public int removeDuplicates(int[] a) {
            int slow = 0;
            int fast = 1;

            if (a.length == 0)
                return 0;


            while (fast < a.length) {
                if (a[slow] != a[fast]) {
                    slow++;
                    a[slow] = a[fast];

                }
                fast++;
            }
            return slow + 1;
        }
    }

}
