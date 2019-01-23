package wang.xiaoluobo.leetcode;

import java.util.HashSet;
import java.util.Set;

/**
 * https://leetcode-cn.com/problems/longest-substring-without-repeating-characters/
 *
 * @author wangyd
 * @date 2019/1/23
 */
public class Problem0003 {

    public static void main(String[] args) {
        Problem0003 problem0003 = new Problem0003();
        System.out.println(problem0003.lengthOfLongestSubstring("abcabcbb"));
        System.out.println(problem0003.lengthOfLongestSubstring(" "));
        System.out.println(problem0003.lengthOfLongestSubstring("dvdf"));
        System.out.println(problem0003.lengthOfLongestSubstring("pwwkew"));
        System.out.println();

        System.out.println(problem0003.lengthOfLongestSubstring1("abcabcbb"));
//        System.out.println(problem0003.lengthOfLongestSubstring1(" "));
//        System.out.println(problem0003.lengthOfLongestSubstring1("dvdf"));
        System.out.println(problem0003.lengthOfLongestSubstring1("pwwkewws"));
    }

    public int lengthOfLongestSubstring(String s) {
        Set<Character> set = new HashSet<>();
        char[] chars = s.toCharArray();
        StringBuilder max = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            // set中已经存在，说明存储重复字符
            if (!set.add(chars[i])) {
                if (max.length() < sb.length()) {
                    max.delete(0, max.length()).append(sb);
                }
                sb.delete(0, sb.indexOf(String.valueOf(chars[i])) + 1);
            }
            sb.append(chars[i]);
        }

        if (max.length() < sb.length()) {
            max.delete(0, max.length()).append(sb);
        }
        return max.length();
    }

    public int lengthOfLongestSubstring1(String s) {
        int n = s.length(), ans = 0;
        int[] index = new int[128];
        for (int j = 0, i = 0; j < n; j++) {
            i = Math.max(index[s.charAt(j)], i);
            ans = Math.max(ans, j - i + 1);
            index[s.charAt(j)] = j + 1;
        }
        return ans;
    }
}
