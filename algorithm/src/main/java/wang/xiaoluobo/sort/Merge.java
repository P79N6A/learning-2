package wang.xiaoluobo.sort;

/**
 * 归并排序
 * 1)把长度为n的输入序列分成两个长度为n/2的子序列；
 * 2)对这两个子序列分别采用归并排序；
 * 3)将两个排序好的子序列合并成一个最终的排序序列
 *
 * @author wangyd
 * @date 2018/10/31
 */
public class Merge<T extends Comparable<T>> extends AbstractSort<Integer> {

    @Override
    public void sort(Integer[] t, String className) {
        printStart(t, className);

        sort(t, 0, t.length - 1);

        print(t);
    }

    private void sort(Integer[] t, int left, int right) {
        if (left >= right) {
            return;
        }

        int center = (left + right) / 2;
        sort(t, 0, center);
        sort(t, center + 1, right);

        mergeSort(t, left, center, right);

        System.out.println("center-->" + center);
        print(t);
    }

    private void mergeSort(Integer[] t, int left, int center, int right) {
        // 存放数据的数组
        Integer[] tmp = new Integer[t.length];
        // 记录临时数组的索引
        int k = left;
        // 记录合并数据的起始位置
        int temp = left;
        // 中间值
        int mid = center + 1;
        while (left <= center && mid <= right) {
            // 从左右两个数组找出最小的数存入tmp数组
            if (than(t[left], t[mid])) {
                tmp[k++] = t[mid++];
            } else {
                tmp[k++] = t[left++];
            }
        }

        // 剩余部分依次放入临时数组（实际上两个while只会执行其中一个）
        while (left <= center) {
            tmp[k++] = t[left++];
        }

        while (mid <= right) {
            tmp[k++] = t[mid++];
        }

        // 将临时数组中的数据复制回原数组
        while (temp <= right) {
            t[temp] = tmp[temp++];
        }
    }

    public static void main(String[] args) {
        AbstractSort<Integer> abstractSort = new Merge<>();
        Class clazz = abstractSort.getClass();
        abstractSort.sort(n, clazz.getSimpleName());
    }
}
