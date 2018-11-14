package wang.xiaoluobo.tree;

/**
 * @author wangyd
 * @date 2018/11/13
 */
public class BSTTreeNode<T extends Comparable<T>> {

    /**
     * 关键字(键值)
     */
    T key;

    /**
     * 左孩子
     */
    BSTTreeNode<T> left;

    /**
     * 右孩子
     */
    BSTTreeNode<T> right;

    /**
     * 父结点
     */
    BSTTreeNode<T> parent;

    public BSTTreeNode(T key, BSTTreeNode<T> parent, BSTTreeNode<T> left, BSTTreeNode<T> right) {
        this.key = key;
        this.parent = parent;
        this.left = left;
        this.right = right;
    }

    public T getKey() {
        return key;
    }

    public String toString() {
        return "key:" + key;
    }
}
