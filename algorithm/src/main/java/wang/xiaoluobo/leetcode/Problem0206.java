package wang.xiaoluobo.leetcode;

/**
 * 当前节点指向前驱节点即可
 * <p>
 * https://leetcode-cn.com/problems/reverse-linked-list/
 */
public class Problem0206 {

    public static void main(String[] args) {
        Problem0206 problem0206 = new Problem0206();

        ListNode listNode = new ListNode(1);
        ListNode listNode2 = new ListNode(2);
        ListNode listNode3 = new ListNode(3);
        ListNode listNode4 = new ListNode(4);
        ListNode listNode5 = new ListNode(5);
        listNode.next = listNode2;
        listNode2.next = listNode3;
        listNode3.next = listNode4;
        listNode4.next = listNode5;

        ListNode result = problem0206.reverseList(listNode);
        while (result != null) {
            System.out.println(result.val);
            result = result.next;
        }
    }

    public ListNode reverseList(ListNode head) {
        // 辅助链表
        ListNode pre = null;
        ListNode curr = head;
        ListNode next = null;
        // 当前节点不为null，表示链表还有其他元素
        while (curr != null){
            next = curr.next;

            // 将当前节点添加到辅助链表前面，进而实现链表反转
            curr.next = pre;
            pre = curr;
            curr = next;
        }

        return pre;
    }
}
