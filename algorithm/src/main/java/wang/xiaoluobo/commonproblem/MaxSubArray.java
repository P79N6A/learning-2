package wang.xiaoluobo.commonproblem;

/**
 * 最大子数组求和
 *
 * @author wangyd
 * @date 2018/11/16
 */
public class MaxSubArray {

    public static void main(String[] args) {
        int[] nums = new int[]{9, -3, 5, -21, 30, 2, -4, 1, -50, 7, 10, 8, 12, -11};
        int[] nums1 = new int[]{30, -50, 10, 8, 12, -11};

        System.out.println(getMaxSubArray(nums));

        System.out.println(getMaxSubArray(nums1));
    }

    /**
     * 动态规划思想解决问题
     *
     * @param nums
     * @return
     */
    private static int getMaxSubArray(int[] nums) {
        int max = 0;
        int sum = 0;
        for (int n : nums) {
            sum = sum <= 0 ? n : sum + n;
            max = Math.max(max, sum);
        }

        return max;
    }
}
