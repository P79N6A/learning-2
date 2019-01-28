package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/climbing-stairs/
 */
public class Problem0070 {

    public static void main(String[] args) {
        Problem0070 problem0070 = new Problem0070();
        System.out.println(problem0070.climbStairs(4));
        System.out.println(problem0070.climbStairs1(4));
    }

    /**
     * 动态规划
     * O(1)空间复杂度
     *
     * @param n
     * @return
     */
    public int climbStairs(int n) {
        if (n == 1) {
            return 1;
        }

        int a1 = 1, a2 = 2, total = a2;
        for (int i = 2; i < n; i++) {
            total = a1 + a2;
            a1 = a2;
            a2 = total;
        }
        return total;
    }

    /**
     * 动态规划
     * O(N)空间复杂度
     *
     * @param n
     * @return
     */
    public int climbStairs1(int n) {
        int[] arr = new int[n];
        arr[0] = 1;
        if (n == 1) {
            return 1;
        }

        arr[1] = 2;
        for (int i = 2; i < n; i++) {
            arr[i] = arr[i - 1] + arr[i - 2];
        }
        return arr[n - 1];
    }


}
