package wang.xiaoluobo.tree;

/**
 * 二叉树遍历，有深度遍历和广度遍历，深度遍历有前序、中序以及后序三种遍历方法，广度遍历即层次遍历
 * <p>
 * 二叉树四种主要的遍历思想:(前序、中序和后序指的是根节点的查找位置，左子树永远在右子树先查找)
 * 前序遍历：根节点 ---> 左子树 ---> 右子树
 * 中序遍历：左子树 ---> 根结点 ---> 右子树
 * 后序遍历：左子树 ---> 右子树 ---> 根节点
 * 层次遍历：只需按层次遍历即可
 * <p>
 * <p>
 * 二叉查找树(Binary Search Tree，又称二叉查找树，又称二叉搜索树)是一种能将链表插入的灵活性和有序数组查找的高效性结合起来的算法
 * <p>
 * 二叉查找树或者是一棵空树，或者是具有下列性质的二叉树：
 * 1)若左子树不空，则左子树上所有结点的值均小于或等于它的根结点的值
 * 2)若右子树不空，则右子树上所有结点的值均大于或等于它的根结点的值
 * 3)左、右子树也分别为二叉排序树
 *
 * @author wangyd
 * @date 2018/11/12
 */
public class BSTTree<T extends Comparable<T>> {

    // 根节点
    private BSTTreeNode<T> root;

    public BSTTree() {
        root = null;
    }

    /**
     * 前序遍历二叉树
     * <p>
     * 前序遍历：根结点 ---> 左子树 ---> 右子树
     *
     * @param tree
     */
    private void preOrder(BSTTreeNode<T> tree) {
        if (tree != null) {
            System.out.print(tree.key + " ");
            preOrder(tree.left);
            preOrder(tree.right);
        }
    }

    /**
     * 前序遍历二叉树
     */
    public void preOrder() {
        preOrder(root);
    }

    /**
     * 中序遍历二叉树
     * <p>
     * 中序遍历：左子树 ---> 根结点 ---> 右子树
     *
     * @param tree
     */
    private void inOrder(BSTTreeNode<T> tree) {
        if (tree != null) {
            inOrder(tree.left);
            System.out.print(tree.key + " ");
            inOrder(tree.right);
        }
    }

    /**
     * 中序遍历二叉树
     */
    public void inOrder() {
        inOrder(root);
    }

    /**
     * 后序遍历二叉树
     * <p>
     * 后序遍历：左子树 ---> 右子树 ---> 根节点
     *
     * @param tree
     */
    private void postOrder(BSTTreeNode<T> tree) {
        if (tree != null) {
            postOrder(tree.left);
            postOrder(tree.right);
            System.out.print(tree.key + " ");
        }
    }

    /**
     * 后序遍历二叉树
     */
    public void postOrder() {
        postOrder(root);
    }

    /**
     * 在指定二叉树根节点中查找key(递归实现)
     *
     * @param bstTreeNode
     * @param key
     * @return
     */
    private BSTTreeNode<T> search(BSTTreeNode<T> bstTreeNode, T key) {
        if (bstTreeNode == null) {
            return bstTreeNode;
        }

        int cmp = key.compareTo(bstTreeNode.key);
        if (cmp < 0) {
            return search(bstTreeNode.left, key);
        } else if (cmp > 0) {
            return search(bstTreeNode.right, key);
        }

        return bstTreeNode;
    }

    /**
     * 二叉树中查找key(递归实现)
     *
     * @param key
     * @return
     */
    public BSTTreeNode<T> search(T key) {
        return search(root, key);
    }

    /**
     * 在指定二叉树根节点中查找key(迭代实现)
     *
     * @param bstTreeNode
     * @param key
     * @return
     */
    private BSTTreeNode<T> iterativeSearch(BSTTreeNode<T> bstTreeNode, T key) {
        while (bstTreeNode != null) {
            int tmp = key.compareTo(bstTreeNode.key);

            if (tmp < 0) {
                bstTreeNode = bstTreeNode.left;
            } else if (tmp > 0) {
                bstTreeNode = bstTreeNode.right;
            } else {
                return bstTreeNode;
            }
        }

        return bstTreeNode;
    }

    /**
     * 在指定二叉树根节点中查找key(迭代实现)
     *
     * @param key
     * @return
     */
    public BSTTreeNode<T> iterativeSearch(T key) {
        return iterativeSearch(root, key);
    }

    /**
     * 查找指定二叉树根节点中最小节点
     *
     * @param tree
     * @return
     */
    private BSTTreeNode<T> minimum(BSTTreeNode<T> tree) {
        if (tree == null) {
            return null;
        }

        // 根据二叉查找树特性，当前节点左树为空的节点就是最小节点
        while (tree.left != null) {
            tree = tree.left;
        }
        return tree;
    }

    /**
     * 查找二叉树中最小节点
     *
     * @return
     */
    public T minimum() {
        BSTTreeNode<T> p = minimum(root);
        if (p != null) {
            return p.key;
        }

        return null;
    }

    /**
     * 查找指定二叉树根节点中最大节点
     *
     * @param tree
     * @return
     */
    private BSTTreeNode<T> maximum(BSTTreeNode<T> tree) {
        if (tree == null) {
            return null;
        }

        while (tree.right != null) {
            tree = tree.right;
        }
        return tree;
    }

    /**
     * 查找二叉树中最大节点
     *
     * @return
     */
    public T maximum() {
        BSTTreeNode<T> p = maximum(root);
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
     * @param bstTreeNode
     * @return
     */
    public BSTTreeNode<T> successor(BSTTreeNode<T> bstTreeNode) {
        // 如果bstTreeNode存在右孩子，则bstTreeNode的后继节点为以其右孩子为根的子树的最小结点
        if (bstTreeNode.right != null) {
            return minimum(bstTreeNode.right);
        }

        // 如果bstTreeNode没有右孩子。则bstTreeNode有以下两种可能：
        // (1)bstTreeNode是一个左孩子，则bstTreeNode的后继结点为它的父结点
        // (2)bstTreeNode是一个右孩子，则查找bstTreeNode的最低的父结点，并且该父结点要具有左孩子，找到的这个最低的父结点就是bstTreeNode的后继结点
        BSTTreeNode<T> tmp = bstTreeNode.parent;
        while ((tmp != null) && (bstTreeNode == tmp.right)) {
            bstTreeNode = tmp;
            tmp = tmp.parent;
        }

        return tmp;
    }

    /**
     * 查找节点(bstTreeNode)的前驱节点。
     * <p>
     * 前驱节点：即查找二叉树中数据值小于该结点的最大节点
     *
     * @param bstTreeNode
     * @return
     */
    public BSTTreeNode<T> predecessor(BSTTreeNode<T> bstTreeNode) {
        // 如果bstTreeNode存在左孩子，则bstTreeNode的前驱结点为以其左孩子为根的子树的最大结点
        if (bstTreeNode.left != null) {
            return maximum(bstTreeNode.left);
        }

        // 如果bstTreeNode没有左孩子。则bstTreeNode有以下两种可能：
        // (1)bstTreeNode是一个右孩子，则bstTreeNode的前驱结点为它的父结点
        // (2)bstTreeNode是一个左孩子，则查找bstTreeNode的最低的父结点，并且该父结点要具有右孩子，找到的这个最低的父结点就是bstTreeNode的前驱结点
        BSTTreeNode<T> tmp = bstTreeNode.parent;
        while ((tmp != null) && (bstTreeNode == tmp.left)) {
            bstTreeNode = tmp;
            tmp = tmp.parent;
        }

        return tmp;
    }

    /**
     * 插入节点到二叉树中
     *
     * @param bst         二叉树tree
     * @param bstTreeNode 待插入的节点
     */
    private void insert(BSTTree<T> bst, BSTTreeNode<T> bstTreeNode) {
        int tmp;
        // 待插入节点的父节点
        BSTTreeNode<T> tmpBstTreeNode = null;

        // 临时根节点，每次循环后，根节点不断进行下钻
        BSTTreeNode<T> tmpRoot = bst.root;

        // 查找待插入节点的插入位置，对根节点进行下钻查找，直到找到其父节点为止，则其父节点左孩子或右孩子节点必有其一为空，空的即待插入节点的位置(根据查找二叉树特性确定)
        while (tmpRoot != null) {
            tmpBstTreeNode = tmpRoot;
            tmp = bstTreeNode.key.compareTo(tmpRoot.key);
            // 节点插入后，需要满足查找二叉树特性
            if (tmp < 0) {
                tmpRoot = tmpRoot.left;
            } else {
                tmpRoot = tmpRoot.right;
            }
        }

        bstTreeNode.parent = tmpBstTreeNode;
        // 当为空时，则插入节点为根节点
        if (tmpBstTreeNode == null) {
            bst.root = bstTreeNode;
        } else {
            // 根据查找二叉树特性，确定节点的位置，即左节点还是右节点
            tmp = bstTreeNode.key.compareTo(tmpBstTreeNode.key);
            if (tmp < 0) {
                tmpBstTreeNode.left = bstTreeNode;
            } else {
                tmpBstTreeNode.right = bstTreeNode;
            }
        }
    }

    /**
     * 新建结点(key)，并将其插入到二叉树中
     *
     * @param key 待插入节点的键值
     */
    public void insert(T key) {
        // 创建待插入的节点
        BSTTreeNode<T> bstTreeNode = new BSTTreeNode<>(key, null, null, null);
        if (bstTreeNode != null) {
            insert(this, bstTreeNode);
        }
    }

    /**
     * 删除节点(bstTreeNode)，并返回被删除的节点
     *
     * @param bst
     * @param bstTreeNode
     * @return
     */
    private BSTTreeNode<T> remove(BSTTree<T> bst, BSTTreeNode<T> bstTreeNode) {
        // 临时节点
        BSTTreeNode<T> tmpBstTreeNode = null;
        // 后继节点
        BSTTreeNode<T> successorBstTreeNode = null;

        // 当删除节点的左孩子节点或右孩子节点为空时，则该节点至少只有一个子节点
        // (1)无子节点直接删除即可
        // (2)有一个子节点，则只需要该子节点的父节点指向要删除节点的父节点即可
        if (bstTreeNode.left == null || bstTreeNode.right == null) {
            successorBstTreeNode = bstTreeNode;
        } else {
            // 查找后继节点，因为需要满足二叉查找树特性，例删除根节点时，则只能将后继节点设置为根节点，其他节点不满足查找二叉树特性
            successorBstTreeNode = successor(bstTreeNode);
        }

        if (successorBstTreeNode.left != null) {
            tmpBstTreeNode = successorBstTreeNode.left;
        } else {
            tmpBstTreeNode = successorBstTreeNode.right;
        }

        if (tmpBstTreeNode != null) {
            tmpBstTreeNode.parent = successorBstTreeNode.parent;
        }

        // 判断子节点的位置，即后继节点的原位置
        if (successorBstTreeNode.parent == null) {
            bst.root = tmpBstTreeNode;
        } else if (successorBstTreeNode == successorBstTreeNode.parent.left) {
            successorBstTreeNode.parent.left = tmpBstTreeNode;
        } else {
            successorBstTreeNode.parent.right = tmpBstTreeNode;
        }

        // 当后继节点与删除节点不是同一节点时，需要将后继节点的值赋给待删除节点的值，即完成节点的值删除，其他不需要变化，因为二叉树的结构并没有变化
        // 后继节点实际上成为最终删除的节点，需要将其置为空来释放内存
        if (successorBstTreeNode != bstTreeNode) {
            bstTreeNode.key = successorBstTreeNode.key;
        }

        return successorBstTreeNode;
    }

    /**
     * 删除节点(bstTreeNode)，并返回被删除的节点
     *
     * @param key
     */
    public void remove(T key) {
        BSTTreeNode<T> bstTreeNode, tmpBstTreeNode;
        if ((bstTreeNode = search(root, key)) != null) {
            if ((tmpBstTreeNode = remove(this, bstTreeNode)) != null) {
                tmpBstTreeNode = null;
            }
        }
    }

    /**
     * 销毁指定节点的二叉树
     *
     * @param bstTreeNode
     */
    private void destroy(BSTTreeNode<T> bstTreeNode) {
        if (bstTreeNode == null) {
            return;
        }

        if (bstTreeNode.left != null) {
            destroy(bstTreeNode.left);
        }

        if (bstTreeNode.right != null) {
            destroy(bstTreeNode.right);
        }

        bstTreeNode = null;
    }

    /**
     * 清空树所有数据
     */
    public void clear() {
        destroy(root);
        root = null;
    }

    /**
     * 打印二叉查找树
     *
     * @param tree
     * @param key
     * @param direction 0，表示该节点是根节点
     *                  -1，表示该节点是它的父结点的左孩子
     *                  1，表示该节点是它的父结点的右孩子
     */
    private void print(BSTTreeNode<T> tree, T key, int direction) {
        if (tree != null) {
            if (direction == 0) {   // tree是根节点
                System.out.printf("%2d(root)\n", tree.key);
            } else {    // tree是分支节点
                System.out.printf("%2d(%2d %2s)\n", tree.key, key, direction == 1 ? "R" : "L");
            }

            print(tree.left, tree.key, -1);
            print(tree.right, tree.key, 1);
        }
    }

    /**
     * 打印二叉查找树
     */
    public void print() {
        if (root != null) {
            print(root, root.key, 0);
        }
    }

    public static void main(String[] args) {
        int[] nums = {10, 15, 13, 4, 7, 3, 18, 12, 11, 5, 9, 14, 8, 16, 1, 20, 2, 6, 19, 17};
        BSTTree<Integer> tree = new BSTTree<>();

        System.out.print("原始序列: ");
        for (int i = 0; i < nums.length; i++) {
            System.out.print(nums[i] + " ");
            tree.insert(nums[i]);
        }

        System.out.println();
        System.out.print("前序遍历: ");
        tree.preOrder();

        System.out.println();
        System.out.print("中序遍历: ");
        tree.inOrder();

        System.out.println();
        System.out.print("后序遍历: ");
        tree.postOrder();
        System.out.println();

        System.out.println("最小值: " + tree.minimum());
        System.out.println("最大值: " + tree.maximum());
        System.out.println("树的详细信息: ");
        tree.print();

        System.out.println();
        System.out.print("删除根节点: " + 15);
        tree.remove(15);

        System.out.println();
        System.out.print("中序遍历: ");
        tree.inOrder();
        System.out.println();

        // 销毁二叉树
        tree.clear();
    }
}
