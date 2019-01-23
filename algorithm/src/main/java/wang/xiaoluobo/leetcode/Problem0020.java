package wang.xiaoluobo.leetcode;

import java.util.Stack;

/**
 * https://leetcode-cn.com/problems/valid-parentheses/
 */
public class Problem0020 {

    public static void main(String[] args) {
        Problem0020 problem0020 = new Problem0020();
        System.out.println(problem0020.isValid("()[]{}"));
        System.out.println(problem0020.isValid("([)]"));
        System.out.println(problem0020.isValid("{[]}"));
    }

    public boolean isValid(String s) {
        if (s == null || s.length() % 2 == 1) {
            return false;
        }

        Stack<Character> stack = new Stack<>();
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if ('(' == chars[i] || '[' == chars[i] || '{' == chars[i]) {
                stack.push(chars[i]);
                continue;
            }

            if (stack.isEmpty()) {
                return false;
            }

            if (!((')' == chars[i] && '(' == stack.pop())
                    || (']' == chars[i] && '[' == stack.pop())
                    || ('}' == chars[i] && '{' == stack.pop()))) {
                return false;
            }
        }

        return stack.isEmpty();
    }
}
