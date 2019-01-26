package wang.xiaoluobo.leetcode;

import java.util.Arrays;

/**
 * https://leetcode-cn.com/problems/valid-anagram/
 */
public class Problem0242 {

    public static void main(String[] args) {
        Problem0242 problem0242 = new Problem0242();
        System.out.println(problem0242.isAnagram("anagram", "nagaram"));
    }

    public boolean isAnagram(String s, String t) {
        if (s == null || t == null) {
            return false;
        }
        char[] ss = s.toCharArray();
        char[] tt = t.toCharArray();
        Arrays.sort(ss);
        Arrays.sort(tt);
        return Arrays.equals(ss, tt);
    }
}
