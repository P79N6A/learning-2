package wang.xiaoluobo.leetcode;

import com.alibaba.fastjson.JSON;

/**
 * https://leetcode-cn.com/problems/merge-sorted-array/
 *
 * @author wangyd
 * @date 2019/1/28
 */
public class Problem0088 {

    public static void main(String[] args) {
        Problem0088 problem0088 = new Problem0088();
        int[] arr = new int[]{1, 8, 9, 0, 0, 0};
        problem0088.merge(arr, 3, new int[]{2, 5, 6}, 3);
        System.out.println(JSON.toJSONString(arr));

        arr = new int[]{1, 2, 3, 0, 0, 0};
        problem0088.merge1(arr, 3, new int[]{2, 5, 6}, 3);
        System.out.println(JSON.toJSONString(arr));
    }

    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int[] arr = new int[m];
        System.arraycopy(nums1, 0, arr, 0, m);
        int i = 0, j = 0, index = 0;
        while (i < m && j < n) {
            if (arr[i] > nums2[j]) {
                nums1[index] = nums2[j];
                j++;
            } else {
                nums1[index] = arr[i];
                i++;
            }
            index++;
        }

        if (i < m) {
            System.arraycopy(arr, i, nums1, i + j, m - i);
        }

        if (j < n) {
            System.arraycopy(nums2, j, nums1, i + j, n - j);
        }
    }

    public void merge1(int[] nums1, int m, int[] nums2, int n) {
        int index = m + n - 1;
        m--;
        n--;
        while (index >= 0) {
            if (m >= 0 && n >= 0) {
                nums1[index--] = nums1[m] > nums2[n] ? nums1[m--] : nums2[n--];
            } else if (m >= 0 && n < 0) {
                nums1[index--] = nums1[m--];
            } else {
                nums1[index--] = nums2[n--];
            }
        }
    }
}
