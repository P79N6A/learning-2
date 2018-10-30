package wang.xiaoluobo.sort;

/**
 * @author wangyd
 * @date 2018/10/23
 */
public abstract class AbstractSort<T extends Comparable<T>> {

    protected static final Integer[] n = new Integer[]{9, 3, 5, 2, 4, 1, 6, 7, 10, 8, 12, 11};

    public abstract void sort(T[] t, String className);

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

    protected void swap(T[] t, int i, int j){
        T tmp = t[i];
        t[i] = t[j];
        t[j] = tmp;

        print(t);
    }

    protected void printStart(T[] t, String className){
        System.out.println("-----------" + className + " sort start------------------");
        print(t);
        System.out.println("*********************************************");
        System.out.println();
    }

    protected void print(T[] t){
        for (T t1: t){
            System.out.print(t1 + "\t");
        }
        System.out.println();
    }
}
