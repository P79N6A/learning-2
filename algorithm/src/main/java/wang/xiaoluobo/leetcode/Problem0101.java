package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/symmetric-tree/
 */
public class Problem0101 {

    public static void main(String[] args) {
        Problem0101 problem0101 = new Problem0101();
//        TreeNode t1 = new TreeNode(4);
//        TreeNode t11 = new TreeNode(2);
//        TreeNode t12 = new TreeNode(7);
//        TreeNode t13 = new TreeNode(1);
//        TreeNode t14 = new TreeNode(3);
//        TreeNode t15 = new TreeNode(6);
//        TreeNode t16 = new TreeNode(9);
//        t1.left = t11;
//        t1.right = t12;
//        t11.left = t13;
//        t11.right = t14;
//        t12.left = t15;
//        t12.right = t16;
//        System.out.println(problem0101.isSymmetric(t1));

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
        System.out.println(problem0101.isSymmetric(t1));
    }

    public boolean isSymmetric(TreeNode root) {
        if (root == null) {
            return true;
        }

        return isSymmetricTree(root.left, root.right);
    }

    public boolean isSymmetricTree(TreeNode left, TreeNode right) {
        if (left == null && right == null) {
            return true;
        } else if (left != null && right != null) {
            if (left.val == right.val) {
                return isSymmetricTree(left.left, right.right) && isSymmetricTree(left.right, right.left);
            }
        }

        return false;
    }
}
