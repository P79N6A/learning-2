package wang.xiaoluobo.leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * https://leetcode-cn.com/problems/pascals-triangle/
 */
public class Problem0118 {

    public static void main(String[] args) {
        Problem0118 problem0118 = new Problem0118();
        problem0118.generate(5);
    }

    public List<List<Integer>> generate(int numRows) {
        List<List<Integer>> list = new ArrayList<>();
        if (numRows == 0) {
            return list;
        }

        list.add(Arrays.asList(1));

        for (int i = 1; i < numRows; i++) {
            List<Integer> subList = new ArrayList<>();
            subList.add(1);
            for (int j = 1; j < i; j++) {
                subList.add(list.get(i - 1).get(j - 1) + list.get(i - 1).get(j));
            }
            subList.add(1);
            list.add(subList);
        }
        return list;
    }
}
