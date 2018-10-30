package wang.xiaoluobo.sort;

/**
 * 插入排序
 * 每次都将当前元素插入到左侧已经排序的数组中，使得插入之后左侧数组依然有序。
 * @author wangyd
 * @date 2018/10/30
 */
public class Insertion<T extends Comparable<T>> extends AbstractSort<Integer> {

    @Override
    public void sort(Integer[] t, String className) {
        printStart(t, className);

        for (int i = 1; i < t.length; i++) {
            for (int j = i; j > 0; j--) {
                if(less(t[j], t[j - 1])) {
                    swap(t, j, j - 1);
                }
            }

            System.out.println("----------------------" + i + "----------------------");
        }
    }

    public static void main(String[] args) {
        AbstractSort<Integer> abstractSort = new Insertion<>();
        Class clazz = abstractSort.getClass();
        abstractSort.sort(n, clazz.getSimpleName());
    }
}
