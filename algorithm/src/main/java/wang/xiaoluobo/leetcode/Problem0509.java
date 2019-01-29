package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/fibonacci-number/
 *
 * @author wangyd
 * @date 2019/1/29
 */
public class Problem0509 {

    public static void main(String[] args) {
        Problem0509 problem0509 = new Problem0509();
        System.out.println(problem0509.fib(4));
    }

    public int fib(int N) {
        if (N == 0) return 0;
        if (N == 1) return 1;

        int[] arr = new int[N + 1];
        arr[0] = 0;
        arr[1] = 1;
        for (int i = 2; i <= N; i++) {
            arr[i] = arr[i - 1] + arr[i - 2];
        }
        return arr[N];
    }
}
