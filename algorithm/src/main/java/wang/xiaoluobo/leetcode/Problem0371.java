package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/sum-of-two-integers/comments/
 *
 * @author wangyd
 * @date 2019/1/30
 */
public class Problem0371 {

    public static void main(String[] args) {
        Problem0371 problem0371 = new Problem0371();
        System.out.println(problem0371.getSum(5, 3));
    }

    public int getSum(int a, int b) {
        return b == 0 ? a : getSum(a ^ b, (a & b) << 1);
    }
}
