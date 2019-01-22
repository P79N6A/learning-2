package wang.xiaoluobo.leetcode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * https://leetcode-cn.com/problems/unique-morse-code-words/
 */
public class Problem0804 {


    public static void main(String[] args) {
        Problem0804 problem0804 = new Problem0804();
        String[] words = {"gin", "zen", "gig", "msg"};
        System.out.println(problem0804.uniqueMorseRepresentations(words));
        System.out.println(problem0804.uniqueMorseRepresentations1(words));
    }

    public int uniqueMorseRepresentations(String[] words) {
        String[] codes = {".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--.."};
        Set<String> set = new HashSet<>();
        for (String w : words) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < w.length(); i++) {
                sb.append(codes[w.charAt(i) - 97]);
            }
            set.add(sb.toString());
        }
        return set.size();
    }

    /**
     * lambda表达式效率太低
     *
     * @param words
     * @return
     */
    public int uniqueMorseRepresentations1(String[] words) {
        String[] codes = {".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--.."};
        Object[] objects = Arrays.stream(words).map(w -> {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < w.length(); i++) {
                sb.append(codes[w.charAt(i) - 97]);
            }
            return sb.toString();
        }).distinct().toArray();
        return objects.length;
    }
}
