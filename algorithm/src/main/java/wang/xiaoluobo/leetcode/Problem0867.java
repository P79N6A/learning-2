package wang.xiaoluobo.leetcode;

import com.alibaba.fastjson.JSON;

/**
 * https://leetcode-cn.com/problems/transpose-matrix/
 *
 * @author wangyd
 * @date 2019/1/29
 */
public class Problem0867 {

    public static void main(String[] args) {
        Problem0867 problem0867 = new Problem0867();
        System.out.println(JSON.toJSONString(problem0867.transpose(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})));
        System.out.println(JSON.toJSONString(problem0867.transpose(new int[][]{{1, 2, 3}, {4, 5, 6}})));
    }

    public int[][] transpose(int[][] A) {
        int[][] arr = new int[A[0].length][A.length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                arr[j][i] = A[i][j];
            }
        }
        return arr;
    }
}
