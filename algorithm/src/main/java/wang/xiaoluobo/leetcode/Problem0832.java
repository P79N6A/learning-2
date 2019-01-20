package wang.xiaoluobo.leetcode;

import java.util.Arrays;

/**
 * https://leetcode-cn.com/problems/flipping-an-image/
 */
public class Problem0832 {

    public static void main(String[] args) {
        Problem0832 problem0832 = new Problem0832();
//        int[][] arr = problem0832.flipAndInvertImage(new int[][]{{1}});
        int[][] arr = problem0832.flipAndInvertImage(new int[][]{{1, 1, 0}, {1, 0, 1}, {0, 0, 0}});
        for (int i = 0; i < arr.length; i++) {
            System.out.println(Arrays.toString(arr[i]));
        }
    }

    public int[][] flipAndInvertImage(int[][] A) {
        for (int i = 0; i < A.length; i++) {
            int m = 0, n = A[i].length - 1;
            while (m <= n) {
                if (m == n) {
                    A[i][m] ^= 1;
                }

                // 交换位置，并取反
                int tmp = A[i][m];
                A[i][m] = A[i][n];
                A[i][n] = tmp ^ 1;
                A[i][m] ^= 1;

                m++;
                n--;
            }
        }
        return A;
    }
}
