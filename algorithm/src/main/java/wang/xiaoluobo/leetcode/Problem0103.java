package wang.xiaoluobo.leetcode;

import com.alibaba.fastjson.JSON;

import java.util.*;

/**
 * https://leetcode-cn.com/problems/binary-tree-zigzag-level-order-traversal/
 */
public class Problem0103 {

    public static void main(String[] args) {
        Problem0103 problem0103 = new Problem0103();
//        TreeNode t1 = new TreeNode(3);
//        TreeNode t11 = new TreeNode(9);
//        TreeNode t12 = new TreeNode(20);
//        TreeNode t13 = new TreeNode(15);
//        TreeNode t14 = new TreeNode(7);
//        t1.left = t11;
//        t1.right = t12;
//        t12.left = t13;
//        t12.right = t14;
//        System.out.println(JSON.toJSONString(problem0103.zigzagLevelOrder(t1)));

        TreeNode t1 = new TreeNode(1);
        TreeNode t11 = new TreeNode(2);
        TreeNode t12 = new TreeNode(3);
        TreeNode t13 = new TreeNode(4);
        TreeNode t14 = new TreeNode(5);
        t1.left = t11;
        t1.right = t12;
        t11.left = t13;
        t12.right = t14;
        System.out.println(JSON.toJSONString(problem0103.zigzagLevelOrder(t1)));
        System.out.println(JSON.toJSONString(problem0103.zigzagLevelOrder1(t1)));
    }

    /**
     * BFS
     *
     * @param root
     * @return
     */
    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> list = new ArrayList<>();
        if (root == null) return list;
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

            if ((list.size() + 1) % 2 == 0) {
                Collections.reverse(tmpList);
            }
            list.add(tmpList);
        }
        return list;
    }


    private List<List<Integer>> list = new ArrayList<>();

    /**
     * DFS
     * DFS比BFS快
     *
     * @param root
     * @return
     */
    public List<List<Integer>> zigzagLevelOrder1(TreeNode root) {
        dfs(root, 0);
        for (int i = 0; i < list.size(); i++) {
            if(i%2 == 1){
                Collections.reverse(list.get(i));
            }
        }
        return list;
    }

    public void dfs(TreeNode root, int depth) {
        if (root == null) return;

        if (depth + 1 > list.size()) {
            list.add(new ArrayList<>());
        }

        list.get(depth).add(root.val);

        dfs(root.left, depth + 1);
        dfs(root.right, depth + 1);
    }
}
