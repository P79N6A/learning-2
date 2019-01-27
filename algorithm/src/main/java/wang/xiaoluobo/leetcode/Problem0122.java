package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/best-time-to-buy-and-sell-stock-ii/
 */
public class Problem0122 {

    public static void main(String[] args) {
        Problem0122 problem0122 = new Problem0122();
        System.out.println(problem0122.maxProfit(new int[]{7,1,5,3,6,4}));
        System.out.println(problem0122.maxProfit(new int[]{1,2,3,4,5}));
        System.out.println(problem0122.maxProfit(new int[]{7,6,4,3,1}));
    }

    /**
     * 贪心算法
     *
     * @param prices
     * @return
     */
    public int maxProfit(int[] prices) {
        int result = 0;
        for (int i = 0; i < prices.length - 1; i++) {
            if(prices[i + 1] - prices[i] > 0){
                result += prices[i + 1] - prices[i];
            }
        }
        return result;
    }
}
