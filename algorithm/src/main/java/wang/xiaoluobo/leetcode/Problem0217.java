package wang.xiaoluobo.leetcode;

import java.util.Arrays;

/**
 * https://leetcode-cn.com/problems/contains-duplicate/
 *
 * @author wangyd
 * @date 2019/1/28
 */
public class Problem0217 {

    public static void main(String[] args) {
        Problem0217 problem0217 = new Problem0217();
        System.out.println(problem0217.containsDuplicate(new int[]{1, 2, 3, 1}));
        System.out.println(problem0217.containsDuplicate(new int[]{1, 2, 3, 4}));
    }

    public boolean containsDuplicate(int[] nums) {
        Arrays.sort(nums);
        for (int i = 1; i < nums.length; i++) {
            if (nums[i - 1] == nums[i]) {
                return true;
            }
        }
        return false;
    }
}
