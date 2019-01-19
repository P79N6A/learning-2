package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/linked-list-cycle/
 */
public class Problem0141 {

    public static void main(String[] args) {
        Problem0141 problem0141 = new Problem0141();

        ListNode listNode = new ListNode(1);
        ListNode listNode2 = new ListNode(2);
        ListNode listNode3 = new ListNode(3);
        ListNode listNode4 = new ListNode(4);
        ListNode listNode5 = new ListNode(5);
        ListNode listNode6 = new ListNode(6);
        listNode.next = listNode2;
        listNode2.next = listNode3;
        listNode3.next = listNode4;
        listNode4.next = listNode5;
        listNode5.next = listNode6;
        listNode6.next = listNode4;

        System.out.println(problem0141.hasCycle(listNode));
    }

    public boolean hasCycle(ListNode head) {
        ListNode fast = head, slow = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {
                return true;
            }
        }

        return false;
    }

    private static class ListNode {
        int val;
        public ListNode next;

        ListNode(int x) {
            val = x;
        }
    }
}
