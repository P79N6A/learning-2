package wang.xiaoluobo.designpattern.prototype105.deep;

import java.io.Serializable;

/**
 * 金箍棒对象
 */
public class GoldRingedStaff implements Serializable {
    /**
     * 高度
     */
    private float height = 100.00f;
    /**
     * 半径
     */
    private float radius = 10.00f;

    /**
     * 金箍棒变大方法
     */
    public void grow() {
        this.radius *= 2;
        this.height *= 2;
    }

    /**
     * 金箍棒缩小方法
     */
    public void shrink() {
        this.radius /= 2;
        this.height /= 2;
    }
}
