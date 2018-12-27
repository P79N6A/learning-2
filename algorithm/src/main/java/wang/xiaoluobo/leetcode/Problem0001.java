package wang.xiaoluobo.leetcode;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * https://leetcode.com/problems/two-sum/
 */
public class Problem0001 {

    public static void main(String[] args) {
        Problem0001 problem0001 = new Problem0001();
        System.out.println(JSON.toJSONString(problem0001.twoSum(new int[]{2, 7, 11, 15}, 9)));
    }

    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            if (map.get(target - nums[i]) == null) {
                map.put(nums[i], i);
            } else {
                return new int[]{map.get(target - nums[i]), i};
            }
        }

        return new int[2];
    }
}
