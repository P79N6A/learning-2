package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/add-binary/
 *
 * @author wangyd
 * @date 2019/1/30
 */
public class Problem0067 {

    public static void main(String[] args) {
        Problem0067 problem0067 = new Problem0067();
        System.out.println(problem0067.addBinary("", ""));
        System.out.println(problem0067.addBinary("1", ""));
        System.out.println(problem0067.addBinary("11", ""));
        System.out.println(problem0067.addBinary("11", "1"));
        System.out.println(problem0067.addBinary("1010", "1011"));
        System.out.println(problem0067.addBinary("111010", "1011"));
    }

    public String addBinary(String a, String b) {
        if (a == null || a.length() == 0) {
            return b;
        }

        if (b == null || b.length() == 0) {
            return a;
        }

        StringBuilder sb = new StringBuilder();
        int i = a.length() - 1, j = b.length() - 1;

        // 是否需要进位
        boolean flag = false;
        char c1 = 'a', c2 = 'b';
        while (i >= 0 || j >= 0) {
            if (i >= 0) {
                c1 = a.charAt(i);
            }else {
                c1 = 'a';
            }

            if (j >= 0) {
                c2 = b.charAt(j);
            }else {
                c2 = 'b';
            }

            if (c1 == '1' && c2 == '1') {
                if (flag) {
                    sb.insert(0, '1');
                } else {
                    sb.insert(0, '0');
                }
                flag = true;
            } else if (c1 == '1' || c2 == '1') {
                if (flag) {
                    sb.insert(0, '0');
                } else {
                    sb.insert(0, '1');
                }
            } else {
                if(flag){
                    sb.insert(0, '1');
                }else {
                    sb.insert(0, '0');
                }
                flag = false;
            }

            i--;
            j--;
        }

        if(flag){
            sb.insert(0, '1');
        }
        return sb.toString();
    }
}
