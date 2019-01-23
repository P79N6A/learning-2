package wang.xiaoluobo.leetcode;

import java.util.Arrays;

/**
 * https://leetcode-cn.com/problems/sort-array-by-parity-ii/
 */
public class Problem0922 {

    public static void main(String[] args) {
        Problem0922 problem0922 = new Problem0922();

        System.out.println(Arrays.toString(problem0922.sortArrayByParityII(new int[]{4, 2, 5, 7})));
        System.out.println(Arrays.toString(problem0922.sortArrayByParityII(new int[]{1, 3, 4, 2, 6, 8, 5, 7})));

        System.out.println(Arrays.toString(problem0922.sortArrayByParityII1(new int[]{4, 2, 5, 7})));
        System.out.println(Arrays.toString(problem0922.sortArrayByParityII1(new int[]{1, 3, 4, 2, 6, 8, 5, 7})));
    }

    public int[] sortArrayByParityII(int[] A) {
        int i = 0, j = A.length - 1;
        while (true) {
            while (i < A.length - 1) {
                if (A[i] % 2 == 1 && i % 2 == 0) {
                    break;
                }
                i++;
            }

            while (j > 0) {
                if (A[j] % 2 == 0 && j % 2 == 1) {
                    break;
                }
                j--;
            }

            if (i == A.length - 1 && j == 0) {
                break;
            }

            int tmp = A[i];
            A[i] = A[j];
            A[j] = tmp;

            i++;
            j--;
        }
        return A;
    }

    public int[] sortArrayByParityII1(int[] A) {
        int j = 1;
        for (int i = 0; i < A.length - 1; i += 2) {
            if ((A[i] & 1) % 2 != 0) {
                while ((A[j] & 1) != 0) {
                    j += 2;
                }

                int tmp = A[i];
                A[i] = A[j];
                A[j] = tmp;
            }
        }

        return A;
    }
}
