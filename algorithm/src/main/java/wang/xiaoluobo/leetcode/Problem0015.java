package wang.xiaoluobo.leetcode;

import java.util.*;

/**
 * https://leetcode-cn.com/problems/3sum/
 *
 * @author wangyd
 * @date 2019/1/24
 */
public class Problem0015 {

    public static void main(String[] args) {
        Problem0015 problem0015 = new Problem0015();
        List<List<Integer>> list = problem0015.threeSum(new int[]{-1, 0, 1, 2, -1, -4});
        System.out.println(list.size());
    }

    /**
     * a + b + c = 0
     * a + b = -c
     * 1. 先选出c
     * 2. 两边指针向中间移动
     *
     * @param nums
     * @return
     */
    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> list = new ArrayList<>();

        Arrays.sort(nums);

        int i, j, k, tmp;
        for (i = 0; i < nums.length - 2; i++) {
            tmp = -nums[i]; // c
            j = i + 1;  // a
            k = nums.length - 1; // b
            while (j < k) {
                if (nums[j] + nums[k] == tmp) {
                    list.add(Arrays.asList(nums[i], nums[j], nums[k]));
                    j++;
                    while (j < k && nums[j - 1] == nums[j]) {   // 数据判重
                        j++;
                    }
                    k--;
                } else if (nums[j] + nums[k] < tmp) {   // 说明nums[j]值过小，需要向右移动指针
                    j++;
                    while (j < k && nums[j - 1] == nums[j]) {
                        j++;
                    }
                } else {    // 说明nums[k]值过大，需要向左移动指针
                    k--;
                }
            }

            // 移动外层循环指针，过滤重复值，执行一次即可
            while (i + 1 < nums.length - 2 && nums[i] == nums[i + 1]) {
                i++;
            }
        }
        return list;
    }

}
