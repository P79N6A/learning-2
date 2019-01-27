package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/minimum-depth-of-binary-tree/
 */
public class Problem0111 {

    public static void main(String[] args) {
        Problem0111 problem0111 = new Problem0111();
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
        System.out.println(problem0111.minDepth(t1));
        System.out.println(problem0111.minDepth1(t1));
    }

    public int minDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }

        if (root.left == null) {
            return 1 + minDepth(root.right);
        }

        if (root.right == null) {
            return 1 + minDepth(root.left);
        }

        return 1 + Math.min(minDepth(root.left), minDepth(root.right));
    }

    public int minDepth1(TreeNode root) {
        if (root == null) return 0;
        int left = minDepth(root.right);
        int right = minDepth(root.left);
        return (left == 0 || right == 0) ? left + right + 1 : 1 + Math.min(left, right);
    }
}
