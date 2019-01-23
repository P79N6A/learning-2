package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/delete-node-in-a-linked-list/
 */
public class Problem0237 {

    private static ListNode head = new ListNode(0);

    public static void main(String[] args) {
        Problem0237 problem0237 = new Problem0237();

        ListNode listNode = new ListNode(1);
        ListNode listNode2 = new ListNode(2);
        ListNode listNode3 = new ListNode(3);
        ListNode listNode4 = new ListNode(4);
        ListNode listNode5 = new ListNode(5);
        listNode.next = listNode2;
        listNode2.next = listNode3;
        listNode3.next = listNode4;
        listNode4.next = listNode5;
        head.next = listNode;
        problem0237.deleteNode(listNode4);

        while (head != null) {
            System.out.println(head.val);
            head = head.next;
        }
    }

    public void deleteNode(ListNode node) {
        ListNode listNode = new ListNode(0);
        listNode.next = head;
        ListNode pre = null;
        while (listNode != null) {
            ListNode curr = listNode.next;
            if (curr == null) {
                break;
            }

            if (curr.val == node.val) {
                if (pre == null) {
                    listNode = curr.next;
                } else {
                    pre.next = curr.next;
                }

                break;
            }

            pre = curr;
            listNode = listNode.next;
        }
    }

    public void deleteNode1(ListNode node) {
        node.val = node.next.val;
        node.next = node.next.next;
    }
}
