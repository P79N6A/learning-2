package wang.xiaoluobo.leetcode;

import java.util.HashMap;
import java.util.Map;

/**
 * https://leetcode-cn.com/problems/jewels-and-stones/
 */
public class Problem0771 {

    public static void main(String[] args) {
        String J = "aA", S = "aAAbbbb";
        Problem0771 problem0771 = new Problem0771();
        System.out.println(problem0771.numJewelsInStones(J, S));
    }

    public int numJewelsInStones(String J, String S) {
        if (J == null || "".equals(J) || S == null || "".equals(S)) {
            return 0;
        }

        char[] jc = J.toCharArray();
        char[] sc = S.toCharArray();

        Map<Character, Character> m = new HashMap<>(50);
        for (int i = 0; i < jc.length; i++) {
            m.put(jc[i], jc[i]);
        }

        int n = 0;
        for (int i = 0; i < sc.length; i++) {
            if (m.get(sc[i]) != null) {
                n++;
            }
        }

        return n;
    }
}
