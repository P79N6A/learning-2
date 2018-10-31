package wang.xiaoluobo.sort;

/**
 * 选择排序
 * 1)找出当前循环最小值的数组下标
 * 2)对最小下标和当前循环下标进行交换
 *
 * @author wangyd
 * @date 2018/10/23
 */
public class SelectionSort<T extends Comparable<T>> extends AbstractSort<Integer> {

    @Override
    public void sort(Integer[] data, String className) {
        printStart(data, className);

        for (int i = 0; i < data.length; i++) {
            int min = i;
            for (int j = i + 1; j < data.length; j++) {
                if (less(data[j], data[min])) {
                    min = j;
                }
            }
            swap(data, i, min);

            System.out.println("----------------------" + i + "----------------------");
            System.out.println();
        }
    }

    public static void main(String[] args) {
        AbstractSort<Integer> abstractSort = new SelectionSort<>();
        Class clazz = abstractSort.getClass();
        abstractSort.sort(nums, clazz.getSimpleName());
    }
}
