package wang.xiaoluobo.leetcode;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

/**
 * https://leetcode-cn.com/problems/self-dividing-numbers/
 *
 * @author wangyd
 * @date 2019/1/30
 */
public class Problem0728 {

    public static void main(String[] args) {
        Problem0728 problem0728 = new Problem0728();
        System.out.println(JSON.toJSONString(problem0728.selfDividingNumbers(1, 22)));
    }

    public List<Integer> selfDividingNumbers(int left, int right) {
        List<Integer> list = new ArrayList<>();
        for (int i = left; i <= right; i++) {
            int n = i;
            boolean flag = true;
            while (n != 0) {
                int mod = n % 10;
                if(mod == 0 || i % mod != 0){
                    flag = false;
                    break;
                }

                n /= 10;
            }

            if (flag) {
                list.add(i);
            }
        }

        return list;
    }
}
