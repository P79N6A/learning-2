package wang.xiaoluobo.leetcode;

import java.util.Arrays;

/**
 * https://leetcode-cn.com/problems/largest-perimeter-triangle/comments/
 *
 * @author wangyd
 * @date 2019/1/30
 */
public class Problem0976 {

    public static void main(String[] args) {
        Problem0976 problem0976 = new Problem0976();
        System.out.println(problem0976.largestPerimeter(new int[]{3, 6, 2, 3}));
    }

    public int largestPerimeter(int[] A) {
        if (A == null || A.length < 3) {
            return 0;
        }

        Arrays.sort(A);
        for (int i = A.length - 3; i >= 0; i--) {
            if (A[i] + A[i + 1] > A[i + 2])
                return A[i] + A[i + 1] + A[i + 2];
        }

        return 0;
    }
}
