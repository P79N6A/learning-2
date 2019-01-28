package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/power-of-two/
 *
 * @author wangyd
 * @date 2019/1/28
 */
public class Problem0231 {

    public static void main(String[] args) {
        Problem0231 problem0231 = new Problem0231();
        System.out.println(problem0231.isPowerOfTwo(32));
        System.out.println(problem0231.isPowerOfTwo(218));
    }

    public boolean isPowerOfTwo(int n) {
        return n > 0 && (n & n - 1) == 0;
    }
}
