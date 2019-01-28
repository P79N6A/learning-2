package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/detect-capital/
 */
public class Problem0520 {

    public static void main(String[] args) {
        Problem0520 problem0520 = new Problem0520();
        System.out.println(problem0520.detectCapitalUse("USA"));
        System.out.println(problem0520.detectCapitalUse("leetcode"));
        System.out.println(problem0520.detectCapitalUse("Google"));
        System.out.println(problem0520.detectCapitalUse("FlaG"));
    }

    public boolean detectCapitalUse(String word) {
        char[] chars = word.toCharArray();
        // 标识首字母是否大写
        boolean flag = chars[0] >= 65 && chars[0] <= 90;
        int m = 1;
        for (int i = 1; i < chars.length; i++) {
            if (chars[i] >= 65 && chars[i] <= 90) {
                m++;
            }
        }

        return flag ? m == 1 || m == chars.length : m == 1;
    }
}
