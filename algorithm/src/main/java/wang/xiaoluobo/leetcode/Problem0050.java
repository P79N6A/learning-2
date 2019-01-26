package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/powx-n/
 */
public class Problem0050 {

    public static void main(String[] args) {
        Problem0050 problem0050 = new Problem0050();
        System.out.println(problem0050.myPow(2, 10));
        System.out.println(problem0050.myPow(2, -2));
        System.out.println(problem0050.myPow(2.10000, 3));
        System.out.println(problem0050.myPow(0.44528, 3));
        System.out.println(problem0050.myPow(1.00000, -2147483648));
        System.out.println(problem0050.myPow(2.00000, -2147483648));
    }

    public double myPow(double x, int n) {
        double result = 1.0;
        boolean flag = false;
        if (n < 0) {
            flag = true;
            if (n == Integer.MIN_VALUE) {
                result *= x;
                n = Integer.MAX_VALUE;
            }
            n = Math.abs(n);
        }

        while (n > 0) {
            if ((n & 1) == 1) {
                result *= x;
            }
            x *= x;
            n >>= 1;
        }

        if (flag) {
            return 1 / result;
        } else {
            return result;
        }
    }

    public double myPow1(double x, int n) {
        if (n == 0) {
            return 1;
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
