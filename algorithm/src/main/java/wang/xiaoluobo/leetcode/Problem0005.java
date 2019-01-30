package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/longest-palindromic-substring/
 *
 * @author wangyd
 * @date 2019/1/30
 */
public class Problem0005 {
    public static void main(String[] args) {
        Problem0005 problem0005 = new Problem0005();
        System.out.println(problem0005.longestPalindrome("babad"));
        System.out.println(problem0005.longestPalindrome("abcda"));
        System.out.println(problem0005.longestPalindrome("aaabaaaa"));
    }

    public String longestPalindrome(String s) {
        if (s == null || s.length() <= 1) {
            return s;
        }

        String result = "";
        StringBuilder sb = new StringBuilder();
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int m = i, index = 0, n = chars.length - 1, tmp = chars.length - 1;
            while (m <= n) {
                if (chars[m] == chars[n]) {
                    if (m == n) {
                        sb.insert(index++, chars[m]);
                    } else {
                        sb.insert(index++, String.valueOf(chars[m]) + String.valueOf(chars[n]));
                    }
                    m++;
                    n--;
                } else {
                    sb = new StringBuilder();
                    index = 0;
                    m = i;
                    tmp--;
                    n = tmp;
                }
            }

            if (result.length() < sb.toString().length()) {
                result = sb.toString();
            }

            sb = new StringBuilder();
        }
        return result;
    }
}
