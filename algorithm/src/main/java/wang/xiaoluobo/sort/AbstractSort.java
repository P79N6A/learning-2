package wang.xiaoluobo.sort;

/**
 * @author wangyd
 * @date 2018/10/23
 */
public abstract class AbstractSort<T extends Comparable<T>> {

    public abstract void sort(T[] t);

    /**
     * t1 < t2
     * @param t1
     * @param t2
     * @return
     */
    protected boolean less(T t1, T t2){
        return t1.compareTo(t2) < 0;
    }

    protected void swap(T[] t, int i, int j){
        T tmp = t[i];
        t[i] = t[j];
        t[j] = tmp;
    }

    protected void print(T[] t){
        for (T t1: t){
            System.out.print(t1 + "\t");
        }
        System.out.println();
    }

    protected void println(T[] t){
        for (T t1: t){
            System.out.println(t1);
        }
        System.out.println();
    }
}
