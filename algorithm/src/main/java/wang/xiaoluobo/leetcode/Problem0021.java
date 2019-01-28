package wang.xiaoluobo.leetcode;

/**
 * @author wangyd
 * @date 2019/1/28
 */
public class Problem0021 {

    public static void main(String[] args) {
        Problem0021 problem0021 = new Problem0021();
        ListNode listNode = new ListNode(6);
        ListNode listNode2 = new ListNode(2);
        ListNode listNode3 = new ListNode(4);
        listNode.next = listNode2;
        listNode2.next = listNode3;

        ListNode node = new ListNode(1);
        ListNode node2 = new ListNode(3);
        ListNode node3 = new ListNode(4);
        node.next = node2;
        node2.next = node3;

        ListNode ln = problem0021.mergeTwoLists(listNode, node);
        System.out.println();
    }

    /**
     * 归并排序
     *
     * @param l1
     * @param l2
     * @return
     */
    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        if (l1 == null) {
            return l2;
        }

        if (l2 == null) {
            return l1;
        }

        ListNode listNode = new ListNode(0);
        ListNode tmpNode = listNode;
        while (l1 != null && l2 != null) {
            if (l1.val < l2.val) {
                tmpNode.next = l1;
                l1 = l1.next;
            } else {
                tmpNode.next = l2;
                l2 = l2.next;
            }

            tmpNode = tmpNode.next;
        }

        if (l1 != null) {
            tmpNode.next = l1;
        }

        if (l2 != null) {
            tmpNode.next = l2;
        }

        return listNode.next;
    }
}
