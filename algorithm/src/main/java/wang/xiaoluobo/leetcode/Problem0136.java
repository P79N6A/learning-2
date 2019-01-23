package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/single-number/
 *
 * @author wangyd
 * @date 2019/1/23
 */
public class Problem0136 {

    public static void main(String[] args) {
        Problem0136 problem0136 = new Problem0136();
        System.out.println(problem0136.singleNumber(new int[]{4,1,2,1,2}));
    }

    public int singleNumber(int[] nums) {
        int n = 0;
        for (int num : nums) {
            n ^= num;
        }
        return n;
    }
}
