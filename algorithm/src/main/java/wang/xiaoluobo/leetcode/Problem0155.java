package wang.xiaoluobo.leetcode;

import java.util.Stack;

/**
 * @author wangyd
 * @date 2019/1/31
 */
public class Problem0155 {

    public static void main(String[] args) {
        MinStack minStack = new MinStack();
        minStack.push(-2);
        minStack.push(0);
        minStack.push(-3);
        System.out.println(minStack.getMin());
        minStack.pop();
        System.out.println(minStack.top());
        System.out.println(minStack.getMin());
    }

    static class MinStack {

        private Stack<Integer> stack;
        private Stack<Integer> minStack;

        /**
         * initialize your data structure here.
         */
        public MinStack() {
            stack = new Stack<>();
            minStack = new Stack<>();
        }

        public void push(int x) {
            stack.push(x);
            if (minStack.empty() || minStack.peek() >= x) {
                minStack.push(x);
            }
        }

        public void pop() {
            if (!minStack.isEmpty()) {
                if (minStack.peek().intValue() == stack.peek().intValue()) {
                    minStack.pop();
                }
                stack.pop();
            }
        }

        public int top() {
            return stack.peek();
        }

        public int getMin() {
            return minStack.peek();
        }
    }
}
