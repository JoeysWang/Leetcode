import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Main1两数之和 {
    public static void main(String[] args) {
        twoSum(new int[]{3, 2, 4}, 6);
    }

    public static int[] twoSum(int[] nums, int target) {

        Map<Integer, Integer> numset = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            numset.put(nums[i], i);
        }


        for (int i = 0; i < nums.length; i++) {
            if (numset.containsKey(target - nums[i])
                    && numset.get(target - nums[i]) != i) {
                int[] result = new int[2];
                result[0] = i;
                result[1] = numset.get(target - nums[i]);
                return result;
            }
        }

        return null;

    }

}
