package wang.xiaoluobo.mathematicalformula;

import java.text.DecimalFormat;

/**
 * 平均值、方差与标准差
 * 一维数据的分析，最常见的就是计算平均值(Mean)、方差(Variance)和标准差(Standard Deviation)
 * <p>
 * 平均值x = (x1 + x2 + ... + xn) / n
 * <p>
 * 方差s^2 = [(x1-x)^2 + ... +(xn-x)^2] / n，x为平均数，x1...xn为数据项
 * <p>
 * 标准差(标准差等于方差的值的开平方)好处：
 * 1)表示离散程度的数字与样本数据点的数量级一致，更适合对数据样本形成感性认知。
 * 2)表示离散程度的数字单位与样本数据的单位一致，更方便做后续的分析运算。
 * 3)在样本数据大致符合正态分布的情况下，标准差具有方便估算的特性：66.7%的数据点落在平均值前后1个标准差的范围内、
 * 95%的数据点落在平均值前后2个标准差的范围内，而99%的数据点将会落在平均值前后3个标准差的范围内。
 *
 * @author wangyd
 * @date 2018/11/12
 */
public class VarianceAndStandardDeviation {

    /**
     * 方差s^2 = [(x1-x)^2 + ... +(xn-x)^2] / n，x为平均数，x1...xn为数据项
     *
     * @param data
     * @return 方差
     */
    public static double variance(double[] data) {
        double sum = 0;
        // 求和
        for (int i = 0; i < data.length; i++) {
            sum += data[i];
        }

        // 求平均数
        double average = sum / data.length;
        double variance = 0;

        // 求方差
        for (int i = 0; i < data.length; i++) {
            variance += (data[i] - average) * (data[i] - average);
        }
        return variance / data.length;
    }

    /**
     * 标准差等于方差的值的开平方
     *
     * @param data
     * @return 标准差
     */
    public static double standardDeviation(double[] data) {
        double variance = variance(data);
        return Math.sqrt(variance / data.length);
    }

    public static void main(String[] args) {
        double[] nums = new double[]{9.0D, 43.0D, 1.0D, 15.0D, 5.0D, 32.0D, 34.0D, 1.0D, 26.0D, 28.0D, 555.0D, 10001.0D, 2003.0D, 7.0D, 7.0D, 6.0D, 11.0D, 50.0D, 8.0D, 12.0D, 41.0D};

        DecimalFormat df = new DecimalFormat("#,##0.00");

        // 计算方差
        double variance = variance(nums);
        System.out.println("方差 = " + df.format(variance));

        // 计算标准差
        double standardDeviation = standardDeviation(nums);
        System.out.println("标准差 = " + df.format(standardDeviation));
    }
}
