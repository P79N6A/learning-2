package wang.xiaoluobo.sort;

/**
 * 希尔排序(缩小增量法) 属于插入类排序,由Shell提出，
 * 希尔排序对直接插入排序进行了简单的改进：它通过加大插入排序中元素之间的间隔，
 * 并在这些有间隔的元素中进行插入排序，从而使数据项大跨度地移动，当这些数据项排过一趟序之后，
 * 希尔排序算法减小数据项的间隔再进行排序，依次进行下去，进行这些排序时的数据项之间的间隔被称为增量，习惯上用字母h来表示这个增量。
 * <p>
 * 常用的h序列由Knuth提出，该序列从1开始，通过如下公式产生：
 * <p>
 * h = 3 * h +1
 * <p>
 * 反过来程序需要反向计算h序列，应该使用
 * <p>
 * h=(h-1)/3
 *
 * @author wangyd
 * @date 2018/10/30
 */
public class ShellSort<T extends Comparable<T>> extends AbstractSort<T> {

    @Override
    public void sort(T[] data, String className) {
        printStart(data, className);

        int h = 1;

        while (h < data.length / 3) {
            h = h * 3 + 1;
        }

        while (h >= 1) {
            for (int i = h; i < data.length; i++) {
                for (int j = i; j >= h; j -= h) {   // 循环
                    if (less(data[j], data[j - h])) {
                        swap(data, j, j - h);
                    }
                }
            }

            System.out.println("----------------------" + h + "----------------------");
            System.out.println();

            h = (h - 1) / 3;
        }
    }

    public static void main(String[] args) {
        AbstractSort<Integer> abstractSort = new ShellSort<>();
        Class clazz = abstractSort.getClass();
        abstractSort.sort(nums, clazz.getSimpleName());
    }
}
