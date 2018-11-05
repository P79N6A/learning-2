package wang.xiaoluobo.sort;

/**
 * 完全二叉树性质：
 * 1)如果对具有n个节点二叉树的根节点从0开始编号，则序号为i的节点的双亲结点为(i-1)/2,左孩子的编号为2i+1，右孩子为2i+2。
 * 2)如果从1开始编号，则双亲结点编号为i/2，左孩子结点序号为2i，右孩子结点序号为2i+1.
 * <p>
 * 堆排序思想：
 * 1)建堆：对数组建堆可以采用从插入堆的方法，从头结点开始不断的进行插入堆。也可以使用堆化的方法从最后节点的父节点开始进行从下往上进行堆化。
 * 2)堆排序：每次选择堆顶元素，并将堆顶元素删除，调整堆后，再重复操作直至堆为空。
 *
 * @author wangyd
 * @date 2018/11/2
 */
public class HeapSort<T extends Comparable<T>> extends AbstractSort<Integer> {

    @Override
    public void sort(Integer[] data, String className) {
        printStart(data, className);

        // 构建一个大顶堆
        for (int i = data.length / 2 - 1; i >= 0; i--) {
            adjustHeap(data, i, data.length - 1);
        }

        print(data);

        // 堆排序
        // 由于是大顶堆，data[0]一定是当前序列的最大值
        for (int i = data.length - 1; i >= 0; i--) {
            swap(data, 0, i);
            // 每次排序序列不断减少，每次堆顶皆为最大值，故非排序序列为有序数列
            adjustHeap(data, 0, i - 1);
        }
    }

    /**
     * 查找节点i下所有子节点数据中最大值
     * @param data
     * @param i    根结点序号
     * @param len  数组长度
     */
    private void adjustHeap(Integer[] data, int i, int len) {
        // 根节点值
        int tmp = data[i];

        // j为序号为i的左子节点
        for (int j = 2 * i; j < len; j *= 2) {
            // 当前节点i的子节点为data[2i]和data[2i+1]，即data[j]和data[j+1]
            if (j < len && data[j] < data[j + 1]) {
                ++j;
            }

            // 如果根节点大等于子节点最大值，则跳出循环
            if (tmp >= data[j]) {
                break;
            }

            data[i] = data[j];
            i = j;
        }

        data[i] = tmp;
    }

    public static void main(String[] args) {
        AbstractSort<Integer> abstractSort = new HeapSort<>();
        Class clazz = abstractSort.getClass();
        abstractSort.sort(nums, clazz.getSimpleName());
    }
}
