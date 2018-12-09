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
public class MergeSort<T extends Comparable<T>> extends AbstractSort<Integer> {

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

        int center = (left + right) / 2;
        sort(data, 0, center);
        sort(data, center + 1, right);

        mergeSort(data, left, center, right);

        System.out.println("center-->" + center);
        print(data);
    }

    private void mergeSort(Integer[] data, int left, int center, int right) {
        // 存放数据的数组
        Integer[] tmp = new Integer[data.length];
        // 记录临时数组的索引
        int k = left;
        // 记录合并数据的起始位置
        int temp = left;
        // 中间值
        int mid = center + 1;
        while (left <= center && mid <= right) {
            // 从左右两个数组找出最小的数存入tmp数组
            if (than(data[left], data[mid])) {
                tmp[k++] = data[mid++];
            } else {
                tmp[k++] = data[left++];
            }
        }

        // 剩余部分依次放入临时数组(实际上两个while只会执行其中一个)
        while (left <= center) {
            tmp[k++] = data[left++];
        }

        while (mid <= right) {
            tmp[k++] = data[mid++];
        }

        // 将临时数组中的数据复制回原数组
        while (temp <= right) {
            data[temp] = tmp[temp++];
        }
    }

    public static void main(String[] args) {
        AbstractSort<Integer> abstractSort = new MergeSort<>();
        Class clazz = abstractSort.getClass();
        abstractSort.sort(nums, clazz.getSimpleName());
    }
}
