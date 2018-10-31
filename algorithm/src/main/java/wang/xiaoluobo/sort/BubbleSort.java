package wang.xiaoluobo.sort;

/**
 * 冒泡排序
 * 每趟循环比较j和j+1，如果t[j]比t[j+1]值大，将t[j]和t[j+1]交换
 *
 * @author wangyd
 * @date 2018/10/30
 */
public class BubbleSort<T extends Comparable<T>> extends AbstractSort<Integer> {

    @Override
    public void sort(Integer[] data, String className) {
        printStart(data, className);

        for (int i = 0; i < data.length; i++) {    // 需要排序n次
            boolean flag = true;
            for (int j = 0; j < data.length - i - 1; j++) {    // 每趟排序比较j和j+1的值，将大值沉底
                if (than(data[j], data[j + 1])) {
                    swap(data, j, j + 1);
                    flag = false;
                }
            }

            if(flag){
                break;
            }

            System.out.println("----------------------" + i + "----------------------");
            System.out.println();
        }
    }

    public static void main(String[] args) {
        AbstractSort<Integer> abstractSort = new BubbleSort<>();
        Class clazz = abstractSort.getClass();
        abstractSort.sort(nums, clazz.getSimpleName());
    }
}
