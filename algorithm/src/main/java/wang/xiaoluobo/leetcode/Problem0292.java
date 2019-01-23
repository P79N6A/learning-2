package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/nim-game/comments/
 * <p>
 * 博弈论
 * https://blog.csdn.net/Lionel_D/article/details/43939605
 *
 * https://baike.baidu.com/item/Nim%E6%B8%B8%E6%88%8F/6737105
 */
public class Problem0292 {

    public static void main(String[] args) {
        Problem0292 problem0292 = new Problem0292();
        System.out.println(problem0292.canWinNim(4));
        System.out.println(problem0292.canWinNim(16));
    }

    /**
     * 巴什博弈
     *
     * @param n
     * @return
     */
    public boolean canWinNim(int n) {
        return n % 4 != 0;
    }
}
