package wang.xiaoluobo.sort;

/**
 * 1)找出待排序的数组中最大和最小的元素；
 * 2)统计数组中每个值为i的元素出现的次数，存入数组C的第i项；
 * 3)对所有的计数累加(从C中的第一个元素开始，每一项和前一项相加);
 * 4)反向填充目标数组：将每个元素i放在新数组的第C(i)项，每放一个元素就将C(i)减去1。
 *
 * @author wangyd
 * @date 2018/11/5
 */
public class CountingSort<T extends Comparable<T>> extends AbstractSort<Integer> {

    @Override
    public void sort(Integer[] data, String className) {
        printStart(data, className);

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        // 找出当前序列最大值和最小值
        for (int i=0;i<data.length;i++){
            min = Math.min(min, data[i]);
            max = Math.max(max, data[i]);
        }

        System.out.println(min + "-------" + max);

        // 辅助数组，用于计数
        int[] tmp = new int[max];
        for(int i = 0; i < data.length; i++){
            // 减去最小值后，将数据存入辅助数组并计数
            int mapPos = data[i] - min;
            tmp[mapPos]++;
        }

        // 计数后，将数据写回原数组
        int index = 0;
        for (int i=0;i<tmp.length;i++) {
            while (tmp[i]-- > 0){
                data[index++] = i + min;
            }
        }

        print(data);
    }

    public static void main(String[] args) {
        AbstractSort<Integer> abstractSort = new CountingSort<>();
        Class clazz = abstractSort.getClass();
        Integer[] nums = new Integer[]{9, 3, 1, 15, 5, 2, 4, 1, 6, 8, 7, 7, 6, 11, 10, 8, 12, 11};
        abstractSort.sort(nums, clazz.getSimpleName());
    }
}
