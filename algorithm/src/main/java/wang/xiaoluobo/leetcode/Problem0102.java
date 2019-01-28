package wang.xiaoluobo.leetcode;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * https://leetcode-cn.com/problems/binary-tree-level-order-traversal/
 */
public class Problem0102 {

    public static void main(String[] args) {
        Problem0102 problem0102 = new Problem0102();
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

        System.out.println(JSON.toJSONString(problem0102.levelOrder(t1)));
        System.out.println(JSON.toJSONString(problem0102.levelOrder1(t1)));
    }

    private List<List<Integer>> list = new ArrayList<>();

    public List<List<Integer>> levelOrder(TreeNode root) {
        level(root, 0);
        return list;
    }

    /**
     * DFS(Depth-First-Search)
     * 深度优先搜索
     * https://zh.wikipedia.org/wiki/%E6%B7%B1%E5%BA%A6%E4%BC%98%E5%85%88%E6%90%9C%E7%B4%A2
     *
     * @param root
     * @param n
     */
    public void level(TreeNode root, int n) {
        if (root == null) {
            return;
        }

        if (n + 1 > list.size()) {
            list.add(new ArrayList<>());
        }
        list.get(n).add(root.val);
        level(root.left, n + 1);
        level(root.right, n + 1);
    }

    /**
     * BFS(Breadth-First-Search)
     * 广度优先搜索
     * https://zh.wikipedia.org/wiki/%E5%B9%BF%E5%BA%A6%E4%BC%98%E5%85%88%E6%90%9C%E7%B4%A2
     *
     * @param root
     * @return
     */
    public List<List<Integer>> levelOrder1(TreeNode root) {
        List<List<Integer>> list = new ArrayList<>();
        if (root == null) {
            return list;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            List<Integer> tmpList = new ArrayList<>();
            int queueSize = queue.size();
            for (int i = 0; i < queueSize; i++) {
                TreeNode node = queue.poll();
                if (node.left != null) {
                    queue.add(node.left);
                }
                if (node.right != null) {
                    queue.add(node.right);
                }
                tmpList.add(node.val);
            }
            list.add(tmpList);
        }
        return list;
    }
}
