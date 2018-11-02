package wang.xiaoluobo.sort;

/**
 * https://blog.csdn.net/Hk_john/article/details/79888992
 *
 * @author wangyd
 * @date 2018/10/23
 */
public abstract class AbstractSort<T extends Comparable<T>> {

    protected static final Integer[] nums = new Integer[]{9, 3, 5, 2, 4, 1, 6, 7, 10, 8, 12, 11};

    public abstract void sort(T[] data, String className);

    /**
     * t1 < t2
     * @param t1
     * @param t2
     * @return
     */
    protected boolean less(T t1, T t2){
        return t1.compareTo(t2) < 0;
    }

    /**
     * t1 > t2
     * @param t1
     * @param t2
     * @return
     */
    protected boolean than(T t1, T t2){
        return t1.compareTo(t2) > 0;
    }

    protected void swap(T[] data, int i, int j){
        T tmp = data[i];
        data[i] = data[j];
        data[j] = tmp;

        print(data);
    }

    protected void printStart(T[] data, String className){
        System.out.println("-----------" + className + " sort start------------------");
        print(data);
        System.out.println("*********************************************");
        System.out.println();
    }

    protected void print(T[] data){
        for (T t: data){
            System.out.print(t + "\t");
        }
        System.out.println();
    }
}
