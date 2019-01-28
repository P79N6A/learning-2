package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/number-complement/
 */
public class Problem0476 {

    public static void main(String[] args) {
        Problem0476 problem0476 = new Problem0476();
        System.out.println(problem0476.findComplement(5));
    }

    public int findComplement(int num) {
        return ~num & (Integer.highestOneBit(num) - 1);
    }
}
