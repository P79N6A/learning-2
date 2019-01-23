package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/invert-binary-tree/
 */
public class Problem0226 {

    public static void main(String[] args) {
        Problem0226 problem0226 = new Problem0226();
        TreeNode t1 = new TreeNode(4);
        TreeNode t11 = new TreeNode(2);
        TreeNode t12 = new TreeNode(7);
        TreeNode t13 = new TreeNode(1);
        TreeNode t14 = new TreeNode(3);
        TreeNode t15 = new TreeNode(6);
        TreeNode t16 = new TreeNode(9);
        t1.left = t11;
        t1.right = t12;
        t11.left = t13;
        t11.right = t14;
        t12.left = t15;
        t12.right = t16;
        TreeNode treeNode = problem0226.invertTree(t1);
        System.out.println(treeNode.val);
    }

    public TreeNode invertTree(TreeNode root) {
        if (root == null) {
            return null;
        }

        TreeNode treeNode = new TreeNode(root.val);
        treeNode.left = invertTree(root.right);
        treeNode.right = invertTree(root.left);
        return treeNode;
    }
}
