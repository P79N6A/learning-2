package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/sqrtx/
 *
 * @author wangyd
 * @date 2019/1/30
 */
public class Problem0069 {

    public static void main(String[] args) {
        Problem0069 problem0069 = new Problem0069();
        System.out.println(problem0069.mySqrt(8));
        System.out.println(problem0069.mySqrt(6));
        System.out.println(problem0069.mySqrt(2147395599));
        System.out.println(problem0069.mySqrt(25));
        System.out.println(problem0069.mySqrt(2));
        System.out.println(problem0069.mySqrt(100));
    }

    public int mySqrt(int x) {
        if (x <= 1) return x;

        int l = 0, r = x, tmp = 0;
        while (l <= r) {
            int m = l + (r - l) / 2;
            // 防止溢出
            if (m < x / m) {
                l = m + 1;
                tmp = m;
            } else if (m > x / m) {
                r = m - 1;
            } else {
                return m;
            }
        }
        return tmp;
    }
}
