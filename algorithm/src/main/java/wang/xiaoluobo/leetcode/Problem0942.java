package wang.xiaoluobo.leetcode;

import java.util.Arrays;

/**
 * https://leetcode-cn.com/problems/di-string-match/
 */
public class Problem0942 {

    public static void main(String[] args) {
        Problem0942 problem0942 = new Problem0942();
        System.out.println(Arrays.toString(problem0942.diStringMatch("IDID")));
        System.out.println(Arrays.toString(problem0942.diStringMatch("III")));
        System.out.println(Arrays.toString(problem0942.diStringMatch("DDI")));
    }

    public int[] diStringMatch(String S) {
        int[] result = new int[S.length() + 1];
        int min = 0, max = S.length();
        for (int i = 0; i < S.length(); i++) {
            if('D' == S.charAt(i)){
                result[i] = max;
                max--;
            }else {
                result[i] = min;
                min++;
            }
        }

        result[result.length - 1] = max;
        return result;
    }
}
