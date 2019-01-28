package wang.xiaoluobo.leetcode;

import java.util.Arrays;

/**
 * https://leetcode-cn.com/problems/maximum-depth-of-n-ary-tree/
 */
public class Problem0559 {

    public static void main(String[] args) {
        Problem0559 problem0559 = new Problem0559();
        Node c5 = new Node(5, null);
        Node c6 = new Node(6, null);
        Node c1 = new Node(3, Arrays.asList(c5, c6));
        Node c2 = new Node(2, null);
        Node c3 = new Node(4, null);
        Node node = new Node(1, Arrays.asList(c1, c2, c3));
        System.out.println(problem0559.maxDepth(node));
    }

    public int maxDepth(Node root) {
        if (root == null) {
            return 0;
        }

        int max = 1;
        if (root.children != null) {
            for (Node node : root.children) {
                max = Math.max(max, maxDepth(node) + 1);
            }
        }
        return max;
    }
}
