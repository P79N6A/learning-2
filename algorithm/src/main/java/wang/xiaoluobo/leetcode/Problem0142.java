package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/linked-list-cycle-ii/
 *
 * @author wangyd
 * @date 2019/1/28
 */
public class Problem0142 {

    public static void main(String[] args) {
        Problem0142 problem0142 = new Problem0142();
        ListNode listNode = new ListNode(3);
        ListNode listNode2 = new ListNode(2);
        ListNode listNode3 = new ListNode(0);
        ListNode listNode4 = new ListNode(4);
        listNode.next = listNode2;
        listNode2.next = listNode3;
        listNode3.next = listNode4;
        listNode4.next = listNode2;
        ListNode node = problem0142.detectCycle(listNode);
        System.out.println(node.val);
    }

    public ListNode detectCycle(ListNode head) {
        if (head == null) return null;
        ListNode fast = head, slow = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;

            // 链表存在环
            if (slow == fast) {
                fast = head;
                while (fast != slow) {
                    fast = fast.next;
                    slow = slow.next;
                }
                return fast;
            }
        }

        return null;
    }
}
