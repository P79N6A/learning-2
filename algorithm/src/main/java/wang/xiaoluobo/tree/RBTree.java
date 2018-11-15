package wang.xiaoluobo.tree;

/**
 * https://www.cnblogs.com/skywang12345/p/3245399.html
 * http://wangkuiwu.github.io/2013/02/05/rbtree05/
 *
 * <p>
 * R-B Tree，全称是Red-Black Tree，又称为“红黑树”，它一种特殊的二叉查找树。红黑树的每个节点上都有存储位表示节点的颜色，可以是红(Red)或黑(Black)。
 * 红黑树的时间复杂度为: O(lgn)
 * <p>
 * 红黑树的特性:
 * （1）每个节点或者是黑色，或者是红色。
 * （2）根节点是黑色。
 * （3）每个叶子节点（NIL）是黑色。 [注意：这里叶子节点，是指为空(NIL或NULL)的叶子节点！]
 * （4）如果一个节点是红色的，则它的子节点必须是黑色的。
 * （5）从一个节点到该节点的子孙节点的所有路径上包含相同数目的黑节点。
 * <p>
 * 注意：
 * (01) 特性(3)中的叶子节点，是只为空(NIL或null)的节点。
 * (02) 特性(5)，确保没有一条路径会比其他路径长出俩倍。因而，红黑树是相对是接近平衡的二叉树。
 * <p>
 * 红-黑树主要通过三种方式对平衡进行修正，改变节点颜色、左旋和右旋。
 *
 * @author wangyd
 * @date 2018/11/2
 */
public class RBTree<T extends Comparable<T>> {

    private RBTreeNode<T> root;    // 根结点

    public RBTree() {
        root = null;
    }

    private RBTreeNode<T> parentOf(RBTreeNode<T> node) {
        return node != null ? node.parent : null;
    }

    private boolean colorOf(RBTreeNode<T> node) {
        return node != null ? node.color : RBTreeNode.BLACK;
    }

    private boolean isRed(RBTreeNode<T> node) {
        return node != null && node.color == RBTreeNode.RED;
    }

    private boolean isBlack(RBTreeNode<T> node) {
        return !isRed(node);
    }

    private void setBlack(RBTreeNode<T> node) {
        if (node != null) {
            node.color = RBTreeNode.BLACK;
        }
    }

    private void setRed(RBTreeNode<T> node) {
        if (node != null) {
            node.color = RBTreeNode.RED;
        }
    }

    private void setParent(RBTreeNode<T> node, RBTreeNode<T> parent) {
        if (node != null) {
            node.parent = parent;
        }
    }

    private void setColor(RBTreeNode<T> node, boolean color) {
        if (node != null) {
            node.color = color;
        }
    }

    /**
     * 前序遍历红黑树
     *
     * @param tree
     */
    private void preOrder(RBTreeNode<T> tree) {
        if (tree != null) {
            System.out.print(tree.key + " ");
            preOrder(tree.left);
            preOrder(tree.right);
        }
    }

    /**
     * 前序遍历红黑树
     */
    public void preOrder() {
        preOrder(root);
    }

    /**
     * 中序遍历红黑树
     *
     * @param tree
     */
    private void inOrder(RBTreeNode<T> tree) {
        if (tree != null) {
            inOrder(tree.left);
            System.out.print(tree.key + " ");
            inOrder(tree.right);
        }
    }

    /**
     * 中序遍历红黑树
     */
    public void inOrder() {
        inOrder(root);
    }

    /**
     * 后序遍历红黑树
     *
     * @param tree
     */
    private void postOrder(RBTreeNode<T> tree) {
        if (tree != null) {
            postOrder(tree.left);
            postOrder(tree.right);
            System.out.print(tree.key + " ");
        }
    }

    /**
     * 后序遍历红黑树
     */
    public void postOrder() {
        postOrder(root);
    }


    /**
     * 在指定红黑树根节点中查找key(递归实现)
     *
     * @param rbTreeNode 指定根节点
     * @param key
     * @return
     */
    private RBTreeNode<T> search(RBTreeNode<T> rbTreeNode, T key) {
        if (rbTreeNode == null) {
            return rbTreeNode;
        }

        int tmp = key.compareTo(rbTreeNode.key);
        if (tmp < 0) {
            return search(rbTreeNode.left, key);
        } else if (tmp > 0) {
            return search(rbTreeNode.right, key);
        }

        return rbTreeNode;
    }

    /**
     * 在红黑树中查找key(递归实现)
     *
     * @param key
     * @return
     */
    public RBTreeNode<T> search(T key) {
        return search(root, key);
    }

    /**
     * 在指定红黑树根节点中查找key(迭代实现)
     *
     * @param rbTreeNode
     * @param key
     * @return
     */
    private RBTreeNode<T> iterativeSearch(RBTreeNode<T> rbTreeNode, T key) {
        while (rbTreeNode != null) {
            int cmp = key.compareTo(rbTreeNode.key);
            if (cmp < 0) {
                rbTreeNode = rbTreeNode.left;
            } else if (cmp > 0) {
                rbTreeNode = rbTreeNode.right;
            } else {
                return rbTreeNode;
            }
        }

        return rbTreeNode;
    }

    /**
     * 在红黑树中查找key(递归实现)
     *
     * @param key
     * @return
     */
    public RBTreeNode<T> iterativeSearch(T key) {
        return iterativeSearch(root, key);
    }

    /**
     * 查找指定红黑树根节点中最小节点
     *
     * @param tree
     * @return
     */
    private RBTreeNode<T> minimum(RBTreeNode<T> tree) {
        if (tree == null) {
            return null;
        }

        while (tree.left != null) {
            tree = tree.left;
        }
        return tree;
    }

    /**
     * 查找红黑树中最小节点
     *
     * @return
     */
    public T minimum() {
        RBTreeNode<T> p = minimum(root);
        if (p != null) {
            return p.key;
        }

        return null;
    }

    /**
     * 查找指定红黑树根节点中最大节点
     *
     * @param tree
     * @return
     */
    private RBTreeNode<T> maximum(RBTreeNode<T> tree) {
        if (tree == null) {
            return null;
        }

        while (tree.right != null) {
            tree = tree.right;
        }
        return tree;
    }

    /**
     * 查找红黑树中最大节点
     *
     * @return
     */
    public T maximum() {
        RBTreeNode<T> p = maximum(root);
        if (p != null) {
            return p.key;
        }

        return null;
    }

    /**
     * 查找节点(bstTreeNode)的后继节点。
     * <p>
     * 后继节点: 即查找二叉树中数据值大于该结点的最小节点
     *
     * @param rbTreeNode
     * @return
     */
    public RBTreeNode<T> successor(RBTreeNode<T> rbTreeNode) {
        // 如果rbTreeNode存在右孩子，则rbTreeNode的后继节点为以其右孩子为根的子树的最小结点
        if (rbTreeNode.right != null)
            return minimum(rbTreeNode.right);

        // 如果rbTreeNode没有右孩子。则rbTreeNode有以下两种可能：
        // (1)rbTreeNode是一个左孩子，则rbTreeNode的后继结点为它的父结点
        // (2)rbTreeNode是一个右孩子，则查找rbTreeNode的最低的父结点，并且该父结点要具有左孩子，找到的这个最低的父结点就是rbTreeNode的后继结点
        RBTreeNode<T> tmp = rbTreeNode.parent;
        while ((tmp != null) && (rbTreeNode == tmp.right)) {
            rbTreeNode = tmp;
            tmp = tmp.parent;
        }

        return tmp;
    }

    /**
     * 查找节点(bstTreeNode)的前驱节点。
     * <p>
     * 前驱节点：即查找二叉树中数据值小于该结点的最大节点
     *
     * @param rbTreeNode
     * @return
     */
    public RBTreeNode<T> predecessor(RBTreeNode<T> rbTreeNode) {
        // 如果rbTreeNode存在左孩子，则rbTreeNode的前驱结点为以其左孩子为根的子树的最大结点
        if (rbTreeNode.left != null) {
            return maximum(rbTreeNode.left);
        }

        // 如果rbTreeNode没有左孩子。则rbTreeNode有以下两种可能：
        // (1)rbTreeNode是一个右孩子，则rbTreeNode的前驱结点为它的父结点
        // (2)rbTreeNode是一个左孩子，则查找rbTreeNode的最低的父结点，并且该父结点要具有右孩子，找到的这个最低的父结点就是rbTreeNode的前驱结点
        RBTreeNode<T> tmp = rbTreeNode.parent;
        while ((tmp != null) && (rbTreeNode == tmp.left)) {
            rbTreeNode = tmp;
            tmp = tmp.parent;
        }

        return tmp;
    }

    /*
     * 对红黑树的节点(x)进行左旋转
     *
     * 左旋示意图(对节点x进行左旋)
     *
     *       px                          px
     *      /                            /
     *     x                            y
     *    / \       --(左旋)--         / \
     *   lx  y                       x  ry
     *      / \                     / \
     *    ly  ry			       lx ly
     *
     * @param x
     */
    private void leftRotate(RBTreeNode<T> x) {
        // 设置x的右孩子为y
        RBTreeNode<T> y = x.right;

        // 将y的左孩子设为x的右孩子
        // 如果y的左孩子非空，将x设为y的左孩子的父亲
        x.right = y.left;
        if (y.left != null) {
            y.left.parent = x;
        }

        // 将x的父亲设为y的父亲
        y.parent = x.parent;

        if (x.parent == null) {
            // 如果x的父亲是空节点，则将y设为根节点
            this.root = y;
        } else {
            if (x.parent.left == x) {
                // 如果x是它父节点的左孩子，则将y设为x的父节点的左孩子
                x.parent.left = y;
            } else {
                // 如果x是它父节点的左孩子，则将y设为x的父节点的左孩子
                x.parent.right = y;
            }
        }

        // 将x设为y的左孩子
        y.left = x;
        // 将x的父节点设为y
        x.parent = y;
    }

    /*
     * 对红黑树的节点(y)进行右旋转
     *
     * 右旋示意图(对节点y进行左旋)
     *
     *      py                          py
     *      /                           /
     *     y                           x
     *    / \       --(右旋)--        /  \
     *   x  ry                      lx   y
     *  / \                             / \
     * lx rx     			           rx ry
     *
     * @param y
     */
    private void rightRotate(RBTreeNode<T> y) {
        // 设置x是当前节点的左孩子
        RBTreeNode<T> x = y.left;

        // 将x的右孩子设为y的左孩子
        // 如果x的右孩子不为空的话，将y设为x的右孩子的父亲
        y.left = x.right;
        if (x.right != null) {
            x.right.parent = y;
        }

        // 将y的父亲设为x的父亲
        x.parent = y.parent;

        if (y.parent == null) {
            // 如果y的父亲是空节点，则将x设为根节点
            this.root = x;
        } else {
            if (y == y.parent.right) {
                // 如果y是它父节点的右孩子，则将x设为y的父节点的右孩子
                y.parent.right = x;
            } else {
                // (y是它父节点的左孩子)将x设为x的父节点的左孩子
                y.parent.left = x;
            }
        }

        // 将y设为x的右孩子
        x.right = y;

        // 将y的父节点设为x
        y.parent = x;
    }

    /**
     * 红黑树插入修正函数
     * <p>
     * 在向红黑树中插入节点之后(失去平衡)，再调用该函数
     * 目的是将它重新塑造成一颗红黑树
     *
     * @param node
     */
    private void insertFixUp(RBTreeNode<T> node) {
        RBTreeNode<T> parent, gparent;

        // 若父节点存在，并且父节点的颜色是红色
        while (((parent = parentOf(node)) != null) && isRed(parent)) {
            gparent = parentOf(parent);

            // 若父节点是祖父节点的左孩子
            if (parent == gparent.left) {
                // (1)叔叔节点是红色
                RBTreeNode<T> uncle = gparent.right;
                if (isRed(uncle)) {
                    setBlack(uncle);
                    setBlack(parent);
                    setRed(gparent);
                    node = gparent;
                    continue;
                }

                // (2)叔叔是黑色，且当前节点是右孩子
                if (parent.right == node) {
                    RBTreeNode<T> tmp;
                    leftRotate(parent);
                    tmp = parent;
                    parent = node;
                    node = tmp;
                }

                // (3)叔叔是黑色，且当前节点是左孩子。
                setBlack(parent);
                setRed(gparent);
                rightRotate(gparent);
            } else {
                // 若node的父节点是node的祖父节点的右孩子
                // (1)叔叔节点是红色
                RBTreeNode<T> uncle = gparent.left;
                if (isRed(uncle)) {
                    setBlack(uncle);
                    setBlack(parent);
                    setRed(gparent);
                    node = gparent;
                    continue;
                }

                // (2)叔叔是黑色，且当前节点是左孩子
                if (parent.left == node) {
                    RBTreeNode<T> tmp;
                    rightRotate(parent);
                    tmp = parent;
                    parent = node;
                    node = tmp;
                }

                // (3)叔叔是黑色，且当前节点是右孩子。
                setBlack(parent);
                setRed(gparent);
                leftRotate(gparent);
            }
        }

        // 将根节点设为黑色
        setBlack(this.root);
    }

    /**
     * 插入节点到红黑树中
     * <p>
     * 步骤：
     * (1)将红黑树当作一颗二叉查找树，将节点添加到二叉查找树中
     * (2)设置节点的颜色为红色
     * (3)重新修正为一颗二叉查找树
     *
     * @param node
     */
    private void insert(RBTreeNode<T> node) {
        int tmp;
        RBTreeNode<T> y = null;
        RBTreeNode<T> x = this.root;

        // (1)将红黑树当作一颗二叉查找树，将节点添加到二叉查找树中
        while (x != null) {
            y = x;
            tmp = node.key.compareTo(x.key);
            if (tmp < 0) {
                x = x.left;
            } else {
                x = x.right;
            }
        }

        node.parent = y;
        if (y != null) {
            tmp = node.key.compareTo(y.key);
            if (tmp < 0) {
                y.left = node;
            } else {
                y.right = node;
            }
        } else {
            this.root = node;
        }

        // (2)设置节点的颜色为红色
        node.color = RBTreeNode.RED;

        // (3)重新修正为一颗二叉查找树
        insertFixUp(node);
    }

    /**
     * 插入节点到红黑树中
     *
     * @param key
     */
    public void insert(T key) {
        RBTreeNode<T> node = new RBTreeNode<>(key, RBTreeNode.BLACK, null, null, null);
        if (node != null) {
            insert(node);
        }
    }

    /**
     * 红黑树删除修正函数
     * <p>
     * 在从红黑树中删除插入节点之后(红黑树失去平衡),再调用该函数
     * 目的是将它重新塑造成一颗红黑树
     *
     * @param node   待修正的节点
     * @param parent
     */
    private void removeFixUp(RBTreeNode<T> node, RBTreeNode<T> parent) {
        RBTreeNode<T> other;

        while ((node == null || isBlack(node)) && node != this.root) {
            if (parent.left == node) {
                other = parent.right;
                if (isRed(other)) {
                    // (1)x的兄弟w是红色的
                    setBlack(other);
                    setRed(parent);
                    leftRotate(parent);
                    other = parent.right;
                }

                if ((other.left == null || isBlack(other.left)) &&
                        (other.right == null || isBlack(other.right))) {
                    // (2)x的兄弟w是黑色，且w的俩个孩子也都是黑色的
                    setRed(other);
                    node = parent;
                    parent = parentOf(node);
                } else {
                    if (other.right == null || isBlack(other.right)) {
                        // (3)x的兄弟w是黑色的，并且w的左孩子是红色，右孩子为黑色。
                        setBlack(other.left);
                        setRed(other);
                        rightRotate(other);
                        other = parent.right;
                    }

                    // (4)x的兄弟w是黑色的；并且w的右孩子是红色的，左孩子任意颜色。
                    setColor(other, colorOf(parent));
                    setBlack(parent);
                    setBlack(other.right);
                    leftRotate(parent);
                    node = this.root;
                    break;
                }
            } else {
                other = parent.left;
                if (isRed(other)) {
                    // (1)x的兄弟w是红色的
                    setBlack(other);
                    setRed(parent);
                    rightRotate(parent);
                    other = parent.left;
                }

                if ((other.left == null || isBlack(other.left)) &&
                        (other.right == null || isBlack(other.right))) {
                    // (2)x的兄弟w是黑色，且w的俩个孩子也都是黑色的
                    setRed(other);
                    node = parent;
                    parent = parentOf(node);
                } else {
                    if (other.left == null || isBlack(other.left)) {
                        // (3)x的兄弟w是黑色的，并且w的左孩子是红色，右孩子为黑色。
                        setBlack(other.right);
                        setRed(other);
                        leftRotate(other);
                        other = parent.left;
                    }

                    // (4)x的兄弟w是黑色的；并且w的右孩子是红色的，左孩子任意颜色。
                    setColor(other, colorOf(parent));
                    setBlack(parent);
                    setBlack(other.left);
                    rightRotate(parent);
                    node = this.root;
                    break;
                }
            }
        }

        if (node != null) {
            setBlack(node);
        }
    }

    /**
     * 删除节点，并返回被删除的节点
     *
     * 删除节点步骤：
     * (1)将红黑树当作一颗二叉查找树，将节点删除
     * (2)通过旋转和重新着色来修正，使之重新成为一棵红黑树
     *
     * @param node
     */
    private void remove(RBTreeNode<T> node) {
        RBTreeNode<T> child, parent;
        boolean color;

        // 被删除节点的左右孩子都不为空的情况
        if (node.left != null && node.right != null) {
            // 被删节点的后继节点(称为取代节点)
            // 用它来取代被删节点的位置，然后再将被删节点去掉
            RBTreeNode<T> replace = node;

            // 获取后继节点
            replace = successor(node);

            // node节点不是根节点(只有根节点不存在父节点)
            if (parentOf(node) != null) {
                if (parentOf(node).left == node) {
                    parentOf(node).left = replace;
                } else {
                    parentOf(node).right = replace;
                }
            } else {
                // node节点是根节点，更新根节点
                this.root = replace;
            }

            // child是取代节点的右孩子,也是需要调整的节点
            // 取代节点肯定不存在左孩子,因为它是一个后继节点
            child = replace.right;
            parent = parentOf(replace);
            // 保存取代节点的颜色
            color = colorOf(replace);

            // 被删除节点是后继节点的父节点
            if (parent == node) {
                parent = replace;
            } else {
                // child不为空
                if (child != null) {
                    setParent(child, parent);
                }

                parent.left = child;
                replace.right = node.right;
                setParent(node.right, replace);
            }

            replace.parent = node.parent;
            replace.color = node.color;
            replace.left = node.left;
            node.left.parent = replace;

            if (color == RBTreeNode.BLACK) {
                removeFixUp(child, parent);
            }

            node = null;
            return;
        }

        if (node.left != null) {
            child = node.left;
        } else {
            child = node.right;
        }

        parent = node.parent;
        // 保存取代节点的颜色
        color = node.color;

        if (child != null) {
            child.parent = parent;
        }

        // node节点不是根节点
        if (parent != null) {
            if (parent.left == node) {
                parent.left = child;
            } else {
                parent.right = child;
            }
        } else {
            this.root = child;
        }

        if (color == RBTreeNode.BLACK) {
            removeFixUp(child, parent);
        }
        node = null;
    }

    /**
     * 删除节点，并返回被删除的节点
     *
     * @param key
     */
    public void remove(T key) {
        RBTreeNode<T> node;

        if ((node = search(root, key)) != null) {
            remove(node);
        }
    }

    /**
     * 销毁红黑树
     *
     * @param tree
     */
    private void destroy(RBTreeNode<T> tree) {
        if (tree == null) {
            return;
        }

        if (tree.left != null) {
            destroy(tree.left);
        }

        if (tree.right != null) {
            destroy(tree.right);
        }

        tree = null;
    }

    public void clear() {
        destroy(root);
        root = null;
    }

    /**
     * 打印红黑树
     *
     * @param tree
     * @param key
     * @param direction 0，表示该节点是根节点
     *                  -1，表示该节点是它的父结点的左孩子
     *                  1，表示该节点是它的父结点的右孩子
     */
    private void print(RBTreeNode<T> tree, T key, int direction) {
        if (tree != null) {
            if (direction == 0) {
                // tree是根节点
                System.out.printf("%2d(B) is root\n", tree.key);
            } else {
                // tree是分支节点
                System.out.printf("%2d(%s) is %2d's %6s child\n", tree.key, isRed(tree) ? "R" : "B", key, direction == 1 ? "right" : "left");
            }

            print(tree.left, tree.key, -1);
            print(tree.right, tree.key, 1);
        }
    }

    public void print() {
        if (root != null)
            print(root, root.key, 0);
    }


    public static void main(String[] args) {
        int[] nums = {10, 15, 13, 4, 7, 3, 18, 12, 11, 5, 9, 14, 8, 16, 1, 20, 2, 6, 19, 17};
        boolean debugInsert = false;    // 插入动作的检测开关(false，关闭；true，打开)
        boolean debugDelete = false;    // 删除动作的检测开关(false，关闭；true，打开)

        RBTree<Integer> tree = new RBTree<>();

        System.out.printf("原始数据: ");
        for (int i = 0; i < nums.length; i++)
            System.out.printf("%d ", nums[i]);
        System.out.printf("\n");

        for (int i = 0; i < nums.length; i++) {
            tree.insert(nums[i]);
            // 设置mDebugInsert=true, 测试添加函数
            if (debugInsert) {
                System.out.printf("== 添加节点: %d\n", nums[i]);
                System.out.printf("== 树的详细信息: \n");
                tree.print();
                System.out.printf("\n");
            }
        }

        System.out.printf("前序遍历: ");
        tree.preOrder();

        System.out.println();
        System.out.printf("中序遍历: ");
        tree.inOrder();

        System.out.println();
        System.out.printf("后序遍历: ");
        tree.postOrder();
        System.out.println();

        System.out.printf("最小值: %s\n", tree.minimum());
        System.out.printf("最大值: %s\n", tree.maximum());
        System.out.printf("树的详细信息: \n");
        tree.print();
        System.out.printf("\n");

        // 设置mDebugDelete=true, 测试删除函数
        if (debugDelete) {
            for (int i = 0; i < nums.length; i++) {
                tree.remove(nums[i]);

                System.out.printf("== 删除节点: %d\n", nums[i]);
                System.out.printf("== 树的详细信息: \n");
                tree.print();
                System.out.printf("\n");
            }
        }

        tree.remove(13);

        // 销毁二叉树
        tree.clear();
    }
}
