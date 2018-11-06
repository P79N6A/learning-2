package wang.xiaoluobo.sort;

/**
 * 基数排序是按照低位先排序，然后收集；再按照高位排序，然后再收集；依次类推，直到最高位。
 * 有时候有些属性是有优先级顺序的，先按低优先级排序，再按高优先级排序。
 * 最后的次序就是高优先级高的在前，高优先级相同的低优先级高的在前。
 *
 * 1)取得数组中的最大数，并取得位数；
 * 2)arr为原始数组，从最低位开始取每个位组成radix数组；
 * 3)对radix进行计数排序(利用计数排序适用于小范围数的特点);
 *
 * @author wangyd
 * @date 2018/11/6
 */
public class RadixSort<T extends Comparable<T>> extends AbstractSort<Integer> {
    @Override
    public void sort(Integer[] data, String className) {

    }

    public static void main(String[] args) {
        AbstractSort<Integer> abstractSort = new RadixSort<>();
        Class clazz = abstractSort.getClass();
        Integer[] nums = new Integer[]{9, 43, 1, 15, 5, 32, 34, 1, 26, 28, 7, 7, 6, 11, 50, 8, 12, 41};
        abstractSort.sort(nums, clazz.getSimpleName());
    }
}
