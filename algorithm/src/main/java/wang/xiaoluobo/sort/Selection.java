package wang.xiaoluobo.sort;

/**
 * 选择排序
 * 1)找出当前循环最小值的数组下标
 * 2)对最小下标和当前循环下标进行交换
 * @author wangyd
 * @date 2018/10/23
 */
public class Selection<T extends Comparable<T>> extends AbstractSort<Integer>{

    @Override
    public void sort(Integer[] t) {
        for(int i=0;i<t.length;i++){
            int min = i;
            for(int j=i+1;j<t.length;j++){
                if(less(t[j], t[min])){
                    min = j;
                }
            }
            swap(t, i, min);

            print(t);
        }

        print(t);
    }

    public static void main(String[] args) {
        /**
            1	2	4	5	3
            1	2	4	5	3
            1	2	3	5	4
            1	2	3	4	5
            1	2	3	4	5
            1	2	3	4	5
         */
        Selection selection = new Selection<Integer>();
        Integer[] n = new Integer[]{2, 1, 4, 5, 3};
        selection.sort(n);
    }
}
