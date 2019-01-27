package wang.xiaoluobo.leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * https://leetcode-cn.com/problems/binary-tree-level-order-traversal-ii/comments/
 */
public class Problem0107 {

    public static void main(String[] args) {
        Problem0107 problem0107 = new Problem0107();
        TreeNode t1 = new TreeNode(1);
        TreeNode t11 = new TreeNode(2);
        TreeNode t12 = new TreeNode(2);
        TreeNode t13 = new TreeNode(3);
        TreeNode t14 = new TreeNode(4);
        TreeNode t15 = new TreeNode(4);
        TreeNode t16 = new TreeNode(3);
        t1.left = t11;
        t1.right = t12;
        t11.left = t13;
        t11.right = t14;
        t12.left = t15;
        t12.right = t16;
        problem0107.levelOrderBottom(t1);
    }

    private List<List<Integer>> list = new ArrayList<>();

    public List<List<Integer>> levelOrderBottom(TreeNode root) {
        dfsLevelOrderBottom(root, 0);
        Collections.reverse(list);
        return list;
    }

    public void dfsLevelOrderBottom(TreeNode root, int n) {
        if (root == null) {
            return;
        }

        if (n + 1 > list.size()) {
            list.add(new ArrayList<>());
        }

        List<Integer> tmpList = list.get(n);
        tmpList.add(root.val);
        dfsLevelOrderBottom(root.left, n + 1);
        dfsLevelOrderBottom(root.right, n + 1);
    }
}
