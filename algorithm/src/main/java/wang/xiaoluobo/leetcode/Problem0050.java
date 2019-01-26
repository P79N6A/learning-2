package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/powx-n/
 */
public class Problem0050 {

    public static void main(String[] args) {
        Problem0050 problem0050 = new Problem0050();
        System.out.println(problem0050.myPow(2, 10));
        System.out.println(problem0050.myPow(2, -2));
    }

    public double myPow(double x, int n) {
        if (n == 1) {
            return x;
        }

        if (n < 0) {
            return 1 / myPow(x, -n);
        }

        if (n % 2 == 1) {
            return x * myPow(x, n - 1);
        }

        return myPow(x * x, n / 2);
    }
}
