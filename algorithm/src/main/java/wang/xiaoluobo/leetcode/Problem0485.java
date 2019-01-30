package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/max-consecutive-ones/
 */
public class Problem0485 {

    public static void main(String[] args) {
        Problem0485 problem0485 = new Problem0485();
        System.out.println(problem0485.findMaxConsecutiveOnes(new int[]{0}));
        System.out.println(problem0485.findMaxConsecutiveOnes(new int[]{1}));
        System.out.println(problem0485.findMaxConsecutiveOnes(new int[]{1, 0}));
        System.out.println(problem0485.findMaxConsecutiveOnes(new int[]{0, 1}));
        System.out.println(problem0485.findMaxConsecutiveOnes(new int[]{1, 1, 0, 1, 1, 1}));
    }

    public int findMaxConsecutiveOnes(int[] nums) {
        int count = 0;
        int max = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == 1) {
                count++;
            } else {
                max = Math.max(max, count);
                count = 0;
            }
        }

        max = Math.max(max, count);
        return max;
    }
}
