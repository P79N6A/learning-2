package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/lowest-common-ancestor-of-a-binary-tree/
 */
public class Problem0236 {

    public static void main(String[] args) {
        Problem0236 problem0235 = new Problem0236();
        TreeNode t1 = new TreeNode(5);
        TreeNode t11 = new TreeNode(3);
        TreeNode t12 = new TreeNode(7);
        TreeNode t13 = new TreeNode(1);
        TreeNode t14 = new TreeNode(4);
        TreeNode t15 = new TreeNode(6);
        TreeNode t16 = new TreeNode(9);
        t1.left = t11;
        t1.right = t12;
        t11.left = t13;
        t11.right = t14;
        t12.left = t15;
        t12.right = t16;

        System.out.println(problem0235.lowestCommonAncestor(t1, t15, t12).val);
    }

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || p == root || q == root) {
            return root;
        }

        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);
        return left == null ? right : right == null ? left : root;
    }
}
