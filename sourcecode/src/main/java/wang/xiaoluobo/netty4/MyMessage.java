package wang.xiaoluobo.netty4;

import java.io.Serializable;

/**
 * @author WangYandong
 * @email wangyd1005sy@163.com
 * @date 2017/1/6 12:54
 */
public class MyMessage implements Serializable {

    private static final long serialVersionUID = 7059198947577043740L;

    private String message;
    private String targetId;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }
}
