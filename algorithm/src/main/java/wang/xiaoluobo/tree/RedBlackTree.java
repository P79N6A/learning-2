package wang.xiaoluobo.tree;

/**
 * https://mp.weixin.qq.com/s/QbLG-En2HOPM3S1uoFUZug
 * https://www.cnblogs.com/skywang12345/p/3245399.html
 *
 * R-B Tree，全称是Red-Black Tree，又称为“红黑树”，它一种特殊的二叉查找树。红黑树的每个节点上都有存储位表示节点的颜色，可以是红(Red)或黑(Black)。
 * 红黑树的时间复杂度为: O(lgn)
 *
 * 红黑树的特性:
 * （1）每个节点或者是黑色，或者是红色。
 * （2）根节点是黑色。
 * （3）每个叶子节点（NIL）是黑色。 [注意：这里叶子节点，是指为空(NIL或NULL)的叶子节点！]
 * （4）如果一个节点是红色的，则它的子节点必须是黑色的。
 * （5）从一个节点到该节点的子孙节点的所有路径上包含相同数目的黑节点。
 *
 * 注意：
 * (01) 特性(3)中的叶子节点，是只为空(NIL或null)的节点。
 * (02) 特性(5)，确保没有一条路径会比其他路径长出俩倍。因而，红黑树是相对是接近平衡的二叉树。
 *
 * 红-黑树主要通过三种方式对平衡进行修正，改变节点颜色、左旋和右旋。
 *
 *
 * @author wangyd
 * @date 2018/11/2
 */
public class RedBlackTree {

}
