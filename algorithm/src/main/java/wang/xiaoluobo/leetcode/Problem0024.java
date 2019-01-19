package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/swap-nodes-in-pairs/
 */
public class Problem0024 {

    public static void main(String[] args) {
        Problem0024 problem0024 = new Problem0024();

        ListNode listNode = new ListNode(1);
        ListNode listNode2 = new ListNode(2);
        ListNode listNode3 = new ListNode(3);
        ListNode listNode4 = new ListNode(4);
        ListNode listNode5 = new ListNode(5);
        listNode.next = listNode2;
        listNode2.next = listNode3;
        listNode3.next = listNode4;
        listNode4.next = listNode5;

        ListNode result = problem0024.swapPairs(listNode);
        while (result != null) {
            System.out.println(result.val);
            result = result.next;
        }
    }

    public ListNode swapPairs(ListNode head) {
        ListNode listNode = new ListNode(0);
        listNode.next = head;
        // 临时节点
        ListNode pre = listNode;
        while (pre.next != null && pre.next.next != null) {
            ListNode node1 = pre.next;
            ListNode node2 = node1.next;

            node1.next = node2.next;
            node2.next = pre.next;
            // 保证链表完整
            pre.next = node2;
            pre = node1;
        }

        return listNode.next;
    }

    public ListNode swapPairs1(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        ListNode pre = head;
        ListNode temp = pre.next;
        ListNode resHead = temp;
        ListNode link = head;
        while (temp != null) {
            pre.next = temp.next;
            temp.next = pre;
            pre = pre.next;
            if (pre == null) {
                break;
            }

            temp = pre.next;
            if (temp != null) {
                link.next = temp;
                link = pre;
            } else {
                link.next = pre;
                break;
            }
        }
        return resHead;
    }

    public static class ListNode {
        int val;
        public ListNode next;

        ListNode(int x) {
            val = x;
        }
    }
}
