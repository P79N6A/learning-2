package wang.xiaoluobo.sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 桶排序的基本思想是：把数组划分为n个大小相同子区间(桶)，每个子区间各自排序，最后合并数组。
 * 计数排序是桶排序的一种特殊情况，可以把计数排序当成每个桶里只有一个元素的情况。
 *
 * 1)找出待排序数组中的最大值max、最小值min
 * 2)我们使用 动态数组ArrayList 作为桶，桶里放的元素也用 ArrayList 存储。桶的数量为(max-min)/arr.length+1
 * 3)遍历数组，计算每个元素 arr[i] 放的桶
 * 4)每个桶各自排序
 * 5)遍历桶数组，把排序好的元素放进输出数组
 *
 * @author wangyd
 * @date 2018/11/5
 */
public class BucketSort<T extends Comparable<T>> extends AbstractSort<Integer> {

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

        // bucket桶数
        int bucketNum = (max - min) / data.length + 1;
        List<List<Integer>> bucketList = new ArrayList<>(bucketNum);
        for(int i = 0; i < bucketNum; i++){
            bucketList.add(new ArrayList<>());
        }

        // 将每个元素放入桶
        for(int i = 0; i < data.length; i++){
            int num = (data[i] - min) / data.length;
            bucketList.get(num).add(data[i]);
        }

        // 对每个桶进行排序
        for(int i = 0; i < bucketList.size(); i++){
            Collections.sort(bucketList.get(i));
        }

        System.out.println(bucketList.toString());
    }

    public static void main(String[] args) {
        AbstractSort<Integer> abstractSort = new BucketSort<>();
        Class clazz = abstractSort.getClass();
        Integer[] nums = new Integer[]{9, 43, 1, 15, 5, 32, 34, 1, 26, 28, 7, 7, 6, 11, 50, 8, 12, 41};
        abstractSort.sort(nums, clazz.getSimpleName());
    }
}
