package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/reverse-string/
 *
 * @author wangyd
 * @date 2019/1/23
 */
public class Problem0344 {

    public static void main(String[] args) {
        Problem0344 problem0344 = new Problem0344();
        problem0344.reverseString("hello".toCharArray());
    }

    public void reverseString(char[] s) {
        int i = 0, j = s.length - 1;
        while (i < j) {
            char tmp = s[i];
            s[i] = s[j];
            s[j] = tmp;

            i++;
            j--;
        }
    }
}
