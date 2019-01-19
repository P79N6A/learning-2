package wang.xiaoluobo.leetcode;

import java.util.PriorityQueue;

/**
 * https://leetcode-cn.com/problems/kth-largest-element-in-a-stream/
 */
public class Problem0703 {

    PriorityQueue<Integer> priorityQueue = null;
    int k;

    public static void main(String[] args) {
        int k = 3;
        int[] arr = {4, 5, 8, 2};
        Problem0703 kthLargest = new Problem0703(k, arr);
        System.out.println(kthLargest.add(3));   // returns 4
        System.out.println(kthLargest.add(5));   // returns 5
        System.out.println(kthLargest.add(10));  // returns 5
        System.out.println(kthLargest.add(9));   // returns 8
        System.out.println(kthLargest.add(4));   // returns 8
    }


    public Problem0703(int k, int[] nums) {
//    public KthLargest(int k, int[] nums) {
        priorityQueue = new PriorityQueue<>(k);
        this.k = k;
        for (int i = 0; i < nums.length; i++) {
            add(nums[i]);
        }
    }

    public int add(int val) {
        if (priorityQueue.size() < k) {
            priorityQueue.offer(val);
        } else if (priorityQueue.peek() < val) {
            priorityQueue.poll();
            priorityQueue.offer(val);
        }

        return priorityQueue.peek();
    }
}
