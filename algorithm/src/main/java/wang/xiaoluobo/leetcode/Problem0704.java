package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/binary-search/
 *
 * @author wangyd
 * @date 2019/1/24
 */
public class Problem0704 {

    public static void main(String[] args) {
        Problem0704 problem0704 = new Problem0704();
        System.out.println(problem0704.search(new int[]{-1, 0, 3, 5, 9, 12}, 9));
        System.out.println(problem0704.search(new int[]{-1, 0, 3, 5, 9, 12}, 2));
        System.out.println(problem0704.search(new int[]{-1, 0}, 0));
        System.out.println(problem0704.search(new int[]{3}, 3));
        System.out.println(problem0704.search(new int[]{3}, 2));
        System.out.println(problem0704.search(new int[]{-1, 0, 3, 5, 9, 12}, 13));
    }

    public int search(int[] nums, int target) {
        int s = 0, e = nums.length - 1;
        while (s <= e) {
//            int m = (s + e) / 2;
            int m = s + (e - s) / 2;
            if (nums[m] == target) {
                return m;
            } else if (nums[m] < target) {
                s = ++m;
            } else if (nums[m] > target) {
                e = --m;
            }
        }

        return -1;
    }
}
