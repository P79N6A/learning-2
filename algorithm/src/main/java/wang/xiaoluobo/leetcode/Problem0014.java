package wang.xiaoluobo.leetcode;

/**
 * https://leetcode.com/problems/longest-common-prefix/
 */
public class Problem0014 {

    public static void main(String[] args) {
        Problem0014 problem0014 = new Problem0014();
        String[] strs = new String[]{"flower", "flower", "flowe"};
        System.out.println(problem0014.longestCommonPrefix(strs));

        String[] strs1 = new String[]{"dog","racecar","car"};
        System.out.println(problem0014.longestCommonPrefix(strs1));
    }

    /**
     * 7ms
     * @param strs
     * @return
     */
    public String longestCommonPrefix(String[] strs) {
        String result = "";

        if(strs == null || strs.length == 0) return "";

        if(strs.length == 1) return strs[0];

        Label:
        for (int i = 0; i < strs[0].length(); i++) {
            char prefix = strs[0].charAt(i);
            for (int j = 1; j < strs.length; j++) {
                // 避免数组越界
                if (strs[j].length() - 1 < i || prefix != strs[j].charAt(i)) {
                    break Label;
                }
            }

            result += prefix;
        }

        return result;
    }

    /**
     * 3ms
     * @param strs
     * @return
     */
    public String longestCommonPrefix1(String[] strs) {
        if (strs == null || strs.length == 0) {
            return "";
        }

        String prefix = strs[0];

        for (int i = 1; i < strs.length; ++i) {
            while (!strs[i].startsWith(prefix)) {
                prefix = prefix.substring(0, prefix.length() - 1);

                if (prefix.isEmpty()) {
                    break;
                }
            }
        }

        return prefix;
    }
}
