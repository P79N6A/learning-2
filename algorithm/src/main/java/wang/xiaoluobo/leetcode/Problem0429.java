package wang.xiaoluobo.leetcode;

import com.alibaba.fastjson.JSON;

import java.util.*;

/**
 * https://leetcode-cn.com/problems/n-ary-tree-level-order-traversal/
 */
public class Problem0429 {

    public static void main(String[] args) {
        Problem0429 problem0429 = new Problem0429();
        Node c5 = new Node(5, null);
        Node c6 = new Node(6, null);
        Node c1 = new Node(3, Arrays.asList(c5, c6));
        Node c2 = new Node(2, null);
        Node c3 = new Node(4, null);
        Node node = new Node(1, Arrays.asList(c1, c2, c3));
        System.out.println(JSON.toJSONString(problem0429.levelOrder(node)));
    }

    /**
     * BFS
     *
     * @param root
     * @return
     */
    public List<List<Integer>> levelOrder(Node root) {
        List<List<Integer>> list = new ArrayList<>();
        if (root == null) return list;
        Queue<Node> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            List<Integer> tmpList = new ArrayList<>();
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Node node = queue.poll();
                if (node.children != null) {
                    for (int j = 0; j < node.children.size(); j++) {
                        queue.add(node.children.get(j));
                    }
                }
                tmpList.add(node.val);
            }
            list.add(tmpList);
        }
        return list;
    }
}
