package wang.xiaoluobo.leetcode;

import java.util.HashSet;
import java.util.Set;

/**
 * https://leetcode-cn.com/problems/n-repeated-element-in-size-2n-array/
 */
public class Problem0961 {

    public static void main(String[] args) {
        Problem0961 problem0961 = new Problem0961();
        int[] arr = {5, 1, 5, 2, 5, 3, 5, 4};
        System.out.println(problem0961.repeatedNTimes(arr));
    }

    public int repeatedNTimes(int[] A) {
        Set<Integer> set = new HashSet<>(A.length / 2 + 2);
        for (int n : A){
            if(!set.add(n)){
                return n;
            }
        }

        return 0;
    }
}
