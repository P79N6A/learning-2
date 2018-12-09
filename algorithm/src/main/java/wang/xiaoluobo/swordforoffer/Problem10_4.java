package wang.xiaoluobo.swordforoffer;

/**
 * 一只青蛙一次可以跳上 1 级台阶，也可以跳上 2 级... 它也可以跳上 n 级。求该青蛙跳上一个 n 级的台阶总共有多少种跳法。
 * <p>
 * 解题思路：
 * 关于本题，前提是n个台阶会有一次n阶的跳法。分析如下:
 * f(1) = 1
 * f(2) = f(2-1) + f(2-2)         // f(2-2) 表示2阶一次跳2阶的次数。
 * f(3) = f(3-1) + f(3-2) + f(3-3)
 * ...
 * f(n) = f(n-1) + f(n-2) + f(n-3) + ... + f(n-(n-1)) + f(n-n)
 * <p>
 * 说明：
 * 1)这里的f(n) 代表的是n个台阶有一次1,2,...n阶的跳法数。
 * 2)n = 1时，只有1种跳法，f(1) = 1
 * 3) n = 2时，会有两个跳得方式，一次1阶或者2阶，这回归到了问题(1)，f(2) = f(2-1) + f(2-2)
 * 4) n = 3时，会有三种跳得方式，1阶、2阶、3阶，
 * 那么就是第一次跳出1阶后面剩下：f(3-1);第一次跳出2阶，剩下f(3-2)；第一次3阶，那么剩下f(3-3)
 * 因此结论是f(3) = f(3-1)+f(3-2)+f(3-3)
 * 5) n = n时，会有n中跳的方式，1阶、2阶...n阶，得出结论：
 * f(n) = f(n-1)+f(n-2)+...+f(n-(n-1)) + f(n-n) =>
 * f(0) + f(1) + f(2) + f(3) + ... + f(n-1)
 * 6) 由以上已经是一种结论，但是为了简单，我们可以继续简化：
 * f(n-1)=f(0)+f(1)+f(2)+f(3)+...+f((n-1)-1)=f(0)+f(1)+f(2)+f(3)+...+f(n-2)
 * f(n)=f(0)+f(1)+f(2)+f(3)+...+f(n-2)+f(n-1)=f(n-1)+f(n-1)
 * 可以得出：f(n) = 2*f(n-1)
 *
 * @author wangyd
 * @date 2018/11/8
 */
public class Problem10_4 {

    /**
     * 青蛙变态跳
     * f(1) = 1
     * f(2) = f(2-1) + f(2-2)
     * f(3) = f(3-1) + f(3-2) + f(3-3)
     * ...
     * f(n) = f(n-1) + f(n-2) + f(n-3) + ... + f(n-(n-1)) + f(n-n)
     *
     * @param target
     * @return
     */
    private static int metamorphosisJump(int target) {
        if (target < 0) {
            return 0;
        }

        if (target == 0 || target == 1) {
            return 1;
        }

        int tmp = 0;
        while (target-- > 0) {
            tmp += 2 * jump(target - 1);
        }

        return tmp;
    }

    public static void main(String[] args) {
        System.out.println(metamorphosisJump(12));
        System.out.println(jump(12));
    }

    /**
     * 青蛙普通跳，每次跳一级或二级
     * f(1) = 1
     * f(2) = f(1) + 1 = 2
     * f(3) = f(3 - 1) + f(3 - 2) = 3
     * f(4) = f(4 - 1) + f(4 - 2) = 5
     * ...
     * f(n) = f(n - 1) + f(n - 2)
     *
     * @param target
     * @return
     */
    private static int jump(int target) {
        if (target <= 0) {
            return 0;
        }

        if (target == 1) {
            return 1;
        }

        if (target == 2) {
            return 2;
        }

        int tmp = 0;
        tmp += jump(target - 1) + jump(target - 2);

        return tmp;
    }
}
