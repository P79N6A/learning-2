package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/validate-binary-search-tree/
 */
public class Problem0098 {

    public static void main(String[] args) {
        Problem0098 problem0098 = new Problem0098();
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

        System.out.println(problem0098.isValidBST(t1));
    }

    public boolean isValidBST(TreeNode root) {
        return isValid(root, null, null);
    }

    public boolean isValid(TreeNode root, Integer min, Integer max) {
        if (root == null) {
            return true;
        }

        if (min != null && root.val <= min) {
            return false;
        }

        if (max != null && root.val >= max) {
            return false;
        }

        return isValid(root.left, min, root.val) && isValid(root.right, root.val, max);
    }
}
