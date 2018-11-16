package wang.xiaoluobo.sort;

/**
 * @author wangyd
 * @date 2018/10/31
 */
public class QuickSort<T extends Comparable<T>> extends AbstractSort<Integer> {

    @Override
    public void sort(Integer[] data, String className) {
        printStart(data, className);

        sort(data, 0, data.length - 1);

        print(data);
    }

    private void sort(Integer[] data, int left, int right) {
        if (left >= right) {
            return;
        }

        int j = partition(data, left, right);
        sort(nums, left, j - 1);
        sort(nums, j + 1, right);
    }

    /**
     * 数据分区
     *
     * @param data
     * @param left
     * @param right
     * @return 返回分区后的基准值的下标
     */
    private int partition(Integer[] data, int left, int right) {
        int i = left;   // 从小到大下标变量
        int j = right + 1;  // 从大到小下标变量
        int pivot = data[left]; // 基准值

        while (true) {
            // 从数组左至右，查找比基准值小的下标
            while (less(data[++i], pivot) && i != right) ;

            // 从数组右至左，查找比基准值大的下标
            while (than(data[--j], pivot) && j != left) ;

            if (i >= j) {
                break;
            }

            // 找出一个比基准值小的值(data[i])和一个比基准值大的值(data[j])，将位置交换
            swap(data, i, j);
        }

        // 将比基准值小的值(data[j])与基准值交换(由于前面i和j的位置做过交换，做j是比基准值小的值)
        swap(data, left, j);

        return j;
    }

    public static void main(String[] args) {
        AbstractSort<Integer> abstractSort = new QuickSort<>();
        Class clazz = abstractSort.getClass();
        abstractSort.sort(nums, clazz.getSimpleName());
    }
}
