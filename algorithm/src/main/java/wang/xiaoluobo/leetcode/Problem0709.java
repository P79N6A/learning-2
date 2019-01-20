package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/to-lower-case/
 */
public class Problem0709 {

    public static void main(String[] args) {
        Problem0709 problem0709 = new Problem0709();
        String str = "Hello123";
        System.out.println(problem0709.toLowerCase(str));
        System.out.println(problem0709.toLowerCase1(str));
    }

    public String toLowerCase(String str) {
        if (str == null || str == "") {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c >= 65 && c <= 90) {
                c += 32;
            }
            sb.append(c);
        }

        return sb.toString();
    }

    public String toLowerCase1(String str) {
        char[] chars = str.toCharArray();
        int dist = 'A' - 'a';
        for (int i = 0; i < str.length(); i++) {
            if ('A' <= chars[i] && chars[i] <= 'Z') {
                chars[i] -= dist;
            }
        }
        return new String(chars);
    }
}
