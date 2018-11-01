package wang.xiaoluobo.sort;

/**
 * 插入排序
 * 每次都将当前元素插入到左侧已经排序的数组中，使得插入之后左侧数组依然有序。
 * @author wangyd
 * @date 2018/10/30
 */
public class InsertionSort<T extends Comparable<T>> extends AbstractSort<T> {

    @Override
    public void sort(T[] data, String className) {
        printStart(data, className);

        for (int i = 1; i < data.length; i++) {
            for (int j = i; j > 0; j--) {
                if(less(data[j], data[j - 1])) {
                    swap(data, j, j - 1);
                }
            }

            System.out.println("----------------------" + i + "----------------------");
        }
    }

    public static void main(String[] args) {
        AbstractSort<Integer> abstractSort = new InsertionSort<>();
        Class clazz = abstractSort.getClass();
        abstractSort.sort(nums, clazz.getSimpleName());
    }
}
