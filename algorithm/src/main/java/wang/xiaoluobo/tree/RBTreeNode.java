package wang.xiaoluobo.tree;

/**
 * @author wangyd
 * @date 2018/11/12
 */
public class RBTreeNode<T extends Comparable<T>> {

    public static final boolean RED = false;
    public static final boolean BLACK = true;

    boolean color;          // 颜色
    T key;                  // 关键字(键值)
    RBTreeNode<T> left;     // 左孩子
    RBTreeNode<T> right;    // 右孩子
    RBTreeNode<T> parent;   // 父结点

    public RBTreeNode(T key, boolean color, RBTreeNode<T> parent, RBTreeNode<T> left, RBTreeNode<T> right) {
        this.key = key;
        this.color = color;
        this.parent = parent;
        this.left = left;
        this.right = right;
    }

    public T getKey() {
        return key;
    }

    public String toString() {
        return "" + key + (this.color == RED ? "(R)" : "B");
    }
}
