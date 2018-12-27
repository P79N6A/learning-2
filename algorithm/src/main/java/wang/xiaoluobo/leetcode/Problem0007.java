package wang.xiaoluobo.leetcode;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * https://leetcode.com/problems/reverse-integer/
 */
public class Problem0007 {

    public static void main(String[] args) {
        Problem0007 problem0007 = new Problem0007();
        System.out.println(JSON.toJSONString(problem0007.reverse(123)));
        System.out.println(JSON.toJSONString(problem0007.reverse(-123)));
        System.out.println(JSON.toJSONString(problem0007.reverse(-120)));
        System.out.println(JSON.toJSONString(problem0007.reverse(1200)));
    }


    /**
     * 29ms
     * @param x
     * @return
     */
    public int reverse(int x) {
        int result = 0;

        while (x != 0) {
            int tail = x % 10;
            int newResult = result * 10 + tail;
            // 判断是否溢出
            if ((newResult - tail) / 10 != result) {
                return 0;
            }
            result = newResult;
            x = x / 10;
        }

        return result;
    }

    /**
     * 16ms
     * @param x
     * @return
     */
//    public int reverse(int x) {
//        long reversed = 0;
//
//        while (x != 0) {
//            reversed = reversed * 10 + (x % 10);
//            x = x / 10;
//        }
//
//        if (reversed != (int) reversed) return 0;
//        else return (int) reversed;
//    }
}
