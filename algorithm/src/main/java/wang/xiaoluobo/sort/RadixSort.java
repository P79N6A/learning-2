package wang.xiaoluobo.sort;

import java.util.Arrays;

/**
 * 基数排序是按照低位先排序，然后收集；再按照高位排序，然后再收集；依次类推，直到最高位。
 * 有时候有些属性是有优先级顺序的，先按低优先级排序，再按高优先级排序。
 * 最后的次序就是高优先级高的在前，高优先级相同的低优先级高的在前。
 * <p>
 * 1)取得数组中的最大数，并取得位数；
 * 2)data为原始数组，从最低位开始取每个位组成radix数组；
 * 3)对radix进行计数排序(利用计数排序适用于小范围数的特点);
 * <p>
 * <p>
 * 对于基数排序有两种方法：
 * 最高位优先法(MSD)(Most Significant Digit first)
 * 最低位优先法(LSD)(Least Significant Digit first)
 * <p>
 * 基数排序是稳定的排序算法，它的平均时间复杂程度为：O(d(r+n))，空间复杂度为：O(rd+n)。
 *
 * @author wangyd
 * @date 2018/11/6
 */
public class RadixSort<T extends Comparable<T>> extends AbstractSort<Integer> {

    @Override
    public void sort(Integer[] data, String className) {
        printStart(data, className);

        // 获取当前序列中最大值
        int max = 0;
        for (int i = 0; i < data.length; i++) {
            max = Math.max(max, data[i]);
        }
        System.out.println(max);

        // 获取最高位，从0开始计数
        int n = 0;
        while ((max = (max / 10)) > 0) {
            n++;
        }
        System.out.println(n);


        int mod = 10;
        int unit = 1;
        for (int i = 0; i <= n; i++, mod *= 10, unit *= 10) {
            // 考虑负数的情况，这里扩展一倍队列数，其中 [0-9]对应负数，[10-19]对应正数(bucket + 10)
            int[][] counter = new int[mod * 2][0];
            for (int j = 0; j < data.length; j++) {
                int bucket = ((data[j] % mod) / unit) + mod;
                counter[bucket] = arrayAppend(counter[bucket], data[j]);
            }

            int pos = 0;
            for (int[] bucket : counter) {
                for (int value : bucket) {
                    data[pos++] = value;
                }
            }
        }

        print(data);
    }

    /**
     * 扩容并保存数据
     *
     * @param arr
     * @param value
     * @return
     */
    private int[] arrayAppend(int[] arr, int value) {
        arr = Arrays.copyOf(arr, arr.length + 1);
        arr[arr.length - 1] = value;
        return arr;
    }

    public static void main(String[] args) {
        AbstractSort<Integer> abstractSort = new RadixSort<>();
        Class clazz = abstractSort.getClass();
        Integer[] nums = new Integer[]{9, 43, 1, 15, 5, 32, 34, 1, 26, 28, 555, 10001, 2003, 7, 7, 6, 11, 50, 8, 12, 41};
        abstractSort.sort(nums, clazz.getSimpleName());
    }
}
