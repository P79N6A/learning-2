package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/search-in-a-binary-search-tree/
 */
public class Problem0700 {

    public static void main(String[] args) {
        Problem0700 problem0700 = new Problem0700();
        TreeNode t1 = new TreeNode(4);
        TreeNode t11 = new TreeNode(2);
        TreeNode t12 = new TreeNode(7);
        TreeNode t13 = new TreeNode(1);
        TreeNode t14 = new TreeNode(3);
        t1.left = t11;
        t1.right = t12;
        t11.left = t13;
        t11.right = t14;

        TreeNode node = problem0700.searchBST(t1, 5);
        if (node != null) {
            System.out.println(node.val);
        } else {
            System.out.println("null");
        }
    }

    public TreeNode searchBST(TreeNode root, int val) {
        if (root == null) return null;
        if (root.val == val) {
            return root;
        } else if (root.val > val) {
            return searchBST(root.left, val);
        } else {
            return searchBST(root.right, val);
        }
    }
}
