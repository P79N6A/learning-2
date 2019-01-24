package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/majority-element/
 * <p>
 * https://zh.wikipedia.org/wiki/%E4%BC%97%E6%95%B0_(%E6%95%B0%E5%AD%A6)
 *
 * @author wangyd
 * @date 2019/1/23
 */
public class Problem0169 {

    public static void main(String[] args) {
        Problem0169 problem0169 = new Problem0169();
        System.out.println(problem0169.majorityElement(new int[]{2, 2, 1, 1, 1, 2, 2, 2, 2}));
        System.out.println(problem0169.majorityElement(new int[]{6, 5, 5}));
    }

    /**
     * 摩尔投票法
     *
     * @param nums
     * @return
     */
    public int majorityElement(int[] nums) {
        int len = nums.length;
        if (len == 1) {
            return nums[0];
        }

        int count = 1, n = nums[0];
        for (int i = 1; i < len; i++) {
            if (n == nums[i]) {
                count++;
            } else {
                count--;
                if (count == 0) {
                    n = nums[i + 1];
                }
            }
        }
        return n;
    }

}