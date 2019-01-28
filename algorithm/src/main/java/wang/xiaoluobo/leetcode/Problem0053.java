package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/maximum-subarray/
 */
public class Problem0053 {

    public static void main(String[] args) {
        Problem0053 problem0053 = new Problem0053();
        System.out.println(problem0053.maxSubArray(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}));
    }

    /**
     * 比较前N项中最大子数组的和与前N+1项中最大子数组的和相比较大小即可
     *
     * @param nums
     * @return
     */
    public int maxSubArray(int[] nums) {
        int sum = 0, max = nums[0];
        for (int i = 0; i < nums.length; i++) {
            if (sum > 0) {
                sum += nums[i];
            } else {
                sum = nums[i];
            }
            max = Math.max(max, sum);
        }
        return max;
    }
}
