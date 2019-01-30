package wang.xiaoluobo.leetcode;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * https://leetcode-cn.com/problems/n-ary-tree-preorder-traversal/
 *
 * @author wangyd
 * @date 2019/1/30
 */
public class Problem0589 {

    public static void main(String[] args) {
        Problem0589 problem0589 = new Problem0589();
        Node c5 = new Node(5, null);
        Node c6 = new Node(6, null);
        Node c1 = new Node(3, Arrays.asList(c5, c6));
        Node c2 = new Node(2, null);
        Node c3 = new Node(4, null);
        Node node = new Node(1, Arrays.asList(c1, c2, c3));
        System.out.println(JSON.toJSONString(problem0589.preorder(node)));
    }

    public List<Integer> preorder(Node root) {
        List<Integer> list = new ArrayList<>();
        p(root, list);
        return list;
    }

    public void p(Node root, List<Integer> list) {
        if (root == null) {
            return;
        }

        list.add(root.val);

        if (root.children != null && root.children.size() > 0) {
            for (Node node : root.children) {
                if (node == null) {
                    break;
                }

                p(node, list);
            }
        }
    }
}
