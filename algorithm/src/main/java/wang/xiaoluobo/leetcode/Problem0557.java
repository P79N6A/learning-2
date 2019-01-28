package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/reverse-words-in-a-string-iii/
 *
 * @author wangyd
 * @date 2019/1/28
 */
public class Problem0557 {

    public static void main(String[] args) {
        Problem0557 problem0557 = new Problem0557();
        System.out.println(problem0557.reverseWords("Let's take LeetCode contest"));
    }

    public String reverseWords(String s) {
        if (s == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        while (s.length() > 0) {
            int index = s.indexOf(" ");
            if (index == -1) {
                if(s.length() > 0 && s.indexOf(" ") == -1){
                    s += " ";
                    continue;
                }
                break;
            }

            char[] chars = s.substring(0, index).toCharArray();
            for (int i = chars.length - 1; i >= 0; i--) {
                sb.append(chars[i]);
            }

            s = s.substring(index + 1);
            if (s.length() > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
}
