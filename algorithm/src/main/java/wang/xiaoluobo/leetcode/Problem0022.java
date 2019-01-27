package wang.xiaoluobo.leetcode;

import java.util.ArrayList;
import java.util.List;

/**
 * https://leetcode-cn.com/problems/generate-parentheses/
 */
public class Problem0022 {

    public static void main(String[] args) {
        Problem0022 problem0022 = new Problem0022();
        problem0022.generateParenthesis(3);
    }

    private List<String> list = new ArrayList<>();

    public List<String> generateParenthesis(int n) {
        generate(0, 0, n, "");
        return list;
    }

    /**
     * 剪枝
     *
     * @param left
     * @param right
     * @param n
     * @param str
     */
    public void generate(int left, int right, int n, String str) {
        if (left == n && right == n) {
            list.add(str);
        }

        if (left < n) {
            generate(left + 1, right, n, str + "(");
        }

        if (left > right && right < n) {
            generate(left, right + 1, n, str + ")");
        }
    }
}
