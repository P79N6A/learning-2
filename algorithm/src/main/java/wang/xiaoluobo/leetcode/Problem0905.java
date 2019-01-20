package wang.xiaoluobo.leetcode;

import java.util.Arrays;

/**
 * https://leetcode-cn.com/problems/sort-array-by-parity/submissions/
 */
public class Problem0905 {

    public static void main(String[] args) {
        Problem0905 problem0905 = new Problem0905();
//        int[] arr = {2, 4};
        int[] arr = {3, 1, 2, 4};
        System.out.println(Arrays.toString(problem0905.sortArrayByParity(arr)));
    }

    public int[] sortArrayByParity(int[] A) {
        int i = 0;
        int j = A.length - 1;
        while (i < j) {
            // 奇数
            while (i <= j) {
                if ((A[i] & 1) == 1) {
                    break;
                }
                i++;
            }

            // 偶数
            while (j > 0) {
                if ((A[j] & 1) == 0) {
                    break;
                }
                j--;
            }

            if(i >= j){
                break;
            }

            int tmp = A[i];
            A[i] = A[j];
            A[j] = tmp;
        }
        return A;
    }

    public int[] sortArrayByParity1(int[] A) {
        int index = 0;
        int lastIndex = A.length-1;
        while(index < lastIndex){
            if((A[index] & 1) == 1){
                while(index < lastIndex){
                    if((A[lastIndex] & 1 )== 0){
                        A[index] = A[index] ^ A[lastIndex];
                        A[lastIndex] = A[index] ^ A[lastIndex];
                        A[index] = A[index] ^ A[lastIndex];
                        break;
                    }else{
                        lastIndex--;
                    }
                }
            }
            index++;
        }
        return A;
    }
}
