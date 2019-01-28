package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/best-time-to-buy-and-sell-stock/
 */
public class Problem0121 {

    public static void main(String[] args) {
        Problem0121 problem0121 = new Problem0121();
        System.out.println(problem0121.maxProfit(new int[]{7, 1, 5, 3, 6, 4}));
        System.out.println(problem0121.maxProfit(new int[]{1, 2, 3, 4, 5}));
        System.out.println(problem0121.maxProfit(new int[]{7, 6, 4, 3, 1}));
        System.out.println(problem0121.maxProfit(new int[]{1, 2}));
        System.out.println(problem0121.maxProfit(new int[]{2, 1, 2, 0, 1}));
    }

    public int maxProfit(int[] prices) {
        if (prices == null || prices.length == 0) {
            return 0;
        }

        int min = prices[0], max = 0;
        for (int i = 0; i < prices.length; i++) {
            min = Math.min(min, prices[i]);
            max = Math.max(max, prices[i] - min);
        }
        return max;
    }
}
