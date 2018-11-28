package wang.xiaoluobo.commonproblem;

import java.util.Stack;

/**
 * 两个栈实现队列
 *
 * @author wangyd
 * @date 2018/10/23
 */
public class StackTest {

    private static Stack<Integer> pushStack = new Stack<>();
    private static Stack<Integer> popStack = new Stack<>();

    private static void push(int node) {
        pushStack.push(node);
    }

    private static int pop() {
        if(popStack.isEmpty()){
            while(!pushStack.isEmpty()){
                popStack.push(pushStack.pop());
            }
        }
        return popStack.pop();
    }

    public static void main(String[] args) {
        push(1);
        push(2);
        System.out.println(pop());
        push(3);
        push(4);
        System.out.println(pop());
        System.out.println(pop());
        System.out.println(pop());
    }
}
