package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/robot-return-to-origin/
 */
public class Problem0657 {

    public static void main(String[] args) {
        Problem0657 problem0657 = new Problem0657();
        System.out.println(problem0657.judgeCircle("LLRUDDUR"));
    }

    /**
     * L 76
     * R 82
     * D 68
     * U 85
     * @param moves
     * @return
     */
    public boolean judgeCircle(String moves) {
        int[] arr = new int[86];
        char[] chars = moves.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            arr[chars[i]]++;
        }
        return arr[76] == arr[82] && arr[68] == arr[85];
    }
}
