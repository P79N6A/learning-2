package wang.xiaoluobo.leetcode;

import java.util.LinkedList;
import java.util.Queue;

/**
 * https://leetcode-cn.com/problems/add-two-numbers/
 *
 * @author wangyd
 * @date 2019/1/23
 */
public class Problem0002 {

    public static void main(String[] args) {
        Problem0002 problem0002 = new Problem0002();
        ListNode l1 = new ListNode(2);
        ListNode ll1 = new ListNode(4);
        ListNode ll2 = new ListNode(3);
        l1.next = ll1;
        ll1.next = ll2;

        ListNode l2 = new ListNode(5);
        ListNode l21 = new ListNode(6);
        ListNode l22 = new ListNode(4);
        ListNode l23 = new ListNode(5);
        l2.next = l21;
        l21.next = l22;
        l22.next = l23;
        ListNode result = problem0002.addTwoNumbers(l1, l2);
        while (result != null) {
            System.out.println(result.val);
            result = result.next;
        }
    }

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode listNode = new ListNode(0);
        ListNode currNode = listNode;
        int n = 0;
        while (l1 != null || l2 != null) {
            int val1 = 0;
            int val2 = 0;
            if (l1 != null) {
                val1 = l1.val;
                l1 = l1.next;
            }

            if (l2 != null) {
                val2 = l2.val;
                l2 = l2.next;
            }

            int sum = val1 + val2 + n;
            ListNode tmpNode = new ListNode(sum % 10);
            currNode.next = tmpNode;
            currNode = currNode.next;

            n = sum / 10;
        }

        if(n > 0){
            currNode.next = new ListNode(n);
        }
        return listNode.next;
    }

    public static class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }
}
