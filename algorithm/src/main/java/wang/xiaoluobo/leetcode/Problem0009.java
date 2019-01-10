package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/palindrome-number/
 */
public class Problem0009 {

    public static void main(String[] args) {
        Problem0009 problem0009 = new Problem0009();
        System.out.println(problem0009.isPalindrome(121));
        System.out.println(problem0009.isPalindrome(122));
        System.out.println(problem0009.isPalindrome(-121));
        System.out.println(problem0009.isPalindrome(12321));
        System.out.println(problem0009.isPalindrome(1221));
        System.out.println(problem0009.isPalindrome(11));
        System.out.println(problem0009.isPalindrome(10));


        System.out.println(problem0009.isPalindrome1(12321));
        System.out.println(problem0009.isPalindrome1(1221));
    }

    /**
     * 111ms
     *
     * @param x
     * @return
     */
    public boolean isPalindrome(int x) {
        if (x < 0) return false;

        String s = String.valueOf(x);
        int l = 0, r = s.length() - 1;
        while (l != r) {
            if (s.charAt(l) != s.charAt(r))
                return false;

            l++;
            r--;

            if (l >= r)
                return true;
        }

        return true;
    }

    /**
     * 76ms
     * @param x
     * @return
     */
    public boolean isPalindrome1(int x) {
        if (x < 0 || (x % 10 == 0 && x != 0))
            return false;

        int revertedNumber = 0;
        while (x > revertedNumber) {
            revertedNumber = revertedNumber * 10 + x % 10;
            x = x / 10;
        }

        return x == revertedNumber || x == revertedNumber / 10;
    }
}
