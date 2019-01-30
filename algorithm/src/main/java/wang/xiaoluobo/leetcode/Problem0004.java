package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/median-of-two-sorted-arrays/
 *
 * @author wangyd
 * @date 2019/1/30
 */
public class Problem0004 {

    public static void main(String[] args) {
        Problem0004 problem0004 = new Problem0004();
        System.out.println(problem0004.findMedianSortedArrays(new int[]{1}, new int[]{}));
        System.out.println(problem0004.findMedianSortedArrays(new int[]{1, 3}, new int[]{}));
        System.out.println(problem0004.findMedianSortedArrays(new int[]{1, 3}, new int[]{2}));
        System.out.println(problem0004.findMedianSortedArrays(new int[]{1, 2}, new int[]{3, 4}));
    }

    /**
     * 归并排序
     *
     * @param nums1
     * @param nums2
     * @return
     */
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        if (nums1 == null || nums1.length == 0) {
            if (nums2.length % 2 == 0) {
                return (double) (nums2[nums2.length / 2] + nums2[nums2.length / 2 - 1]) / 2;
            } else {
                return nums2[nums2.length / 2];
            }
        }

        if (nums2 == null || nums2.length == 0) {
            if (nums1.length % 2 == 0) {
                return (double) (nums1[nums1.length / 2] + nums1[nums1.length / 2 - 1]) / 2;
            } else {
                return nums1[nums1.length / 2];
            }
        }

        int[] arr = new int[nums1.length + nums2.length];
        int i = 0, j = 0, index = 0;
        int n1 = 0, n2 = 0;
        while (i < nums1.length || j < nums2.length) {
            if (i < nums1.length) {
                n1 = nums1[i];
            }

            if (j < nums2.length) {
                n2 = nums2[j];
            }

            if (i < nums1.length && j < nums2.length) {
                if (n1 < n2) {
                    if (i < nums1.length) {
                        i++;
                    }
                    arr[index] = n1;
                } else {
                    if (j < nums2.length) {
                        j++;
                    }
                    arr[index] = n2;
                }
            } else if (i < nums1.length && j >= nums2.length) {
                arr[index] = n1;
                i++;
            } else if (i >= nums1.length && j < nums2.length) {
                arr[index] = n2;
                j++;
            }
            index++;
        }

        if (arr.length % 2 == 0) {
            return (double) (arr[arr.length / 2] + arr[arr.length / 2 - 1]) / 2;
        } else {
            return arr[arr.length / 2];
        }
    }
}
