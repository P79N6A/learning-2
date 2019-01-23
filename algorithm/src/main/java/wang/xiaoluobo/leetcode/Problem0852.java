package wang.xiaoluobo.leetcode;

/**
 * https://leetcode-cn.com/problems/peak-index-in-a-mountain-array/
 *
 * @author wangyd
 * @date 2019/1/23
 */
public class Problem0852 {

    public static void main(String[] args) {
        Problem0852 problem0852 = new Problem0852();
        System.out.println(problem0852.peakIndexInMountainArray(new int[]{0, 1, 0}));
        System.out.println(problem0852.peakIndexInMountainArray(new int[]{0, 2, 1, 0}));
        System.out.println(problem0852.peakIndexInMountainArray(new int[]{0, 1, 2, 3, 4, 5, 4, 3, 2, 1, 0}));
    }

    /**
     * 实际上就是查找数组最大值
     * 由于是山脉数组，最大值应当接近中间
     *
     * @param A
     * @return
     */
    public int peakIndexInMountainArray(int[] A) {
        int index = 0, i = A.length / 2, j = A.length / 2;
        int max = 0;
        while (i > 0 && j < A.length) {
            if(max < Math.max(A[i], A[j])){
                if(A[i] > A[j]){
                    index = i;
                    max = A[i];
                }else {
                    index = j;
                    max = A[j];
                }
            }

            i--;
            j++;
        }
        return index;
    }
}
