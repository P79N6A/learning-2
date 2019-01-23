package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/merge-two-binary-trees/
 */
public class Problem0617 {

    public static void main(String[] args) {
        Problem0617 problem0617 = new Problem0617();
        TreeNode t1 = new TreeNode(1);
        TreeNode t11 = new TreeNode(3);
        TreeNode t12 = new TreeNode(2);
        TreeNode t13 = new TreeNode(5);
        t1.left = t11;
        t1.right = t12;
        t11.left = t13;

        TreeNode t2 = new TreeNode(2);
        TreeNode t21 = new TreeNode(1);
        TreeNode t22 = new TreeNode(3);
        TreeNode t23 = new TreeNode(4);
        TreeNode t24 = new TreeNode(7);
        t2.left = t21;
        t2.right = t22;
        t21.right = t23;
        t22.right = t24;

        TreeNode treeNode = problem0617.mergeTrees(t1, t2);
        System.out.println(treeNode.val);
    }

    public TreeNode mergeTrees(TreeNode t1, TreeNode t2) {
        if (t1 == null) {
            return t2;
        }

        if (t2 == null) {
            return t1;
        }

        TreeNode treeNode = new TreeNode(t1.val + t2.val);
        treeNode.left = mergeTrees(t1.left, t2.left);
        treeNode.right = mergeTrees(t1.right, t2.right);
        return treeNode;
    }
}
