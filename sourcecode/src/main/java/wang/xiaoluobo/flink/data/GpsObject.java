package wang.xiaoluobo.flink.data;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Message_Topic_WsInfo = "iss_mobile_gps"
 * timestamp,content,clientId,type
 * 1539677071483,longtitude:112.3454 latitude:65.3214 ,1000000507,GPS
 */
@Getter
@Setter
public class GpsObject implements Serializable {

    /**
     * 毫秒
     */
    private Long timestamp;

    /**
     * 客户端 ID
     */
    private String clientId;

    /**
     * 类型
     */
    private String type;

    /**
     * 内容体(暂时不解析)
     */
    private String content;

    @Override
    public String toString() {
        return new StringBuilder()
                .append(timestamp).append(",")
                .append(clientId).append(",")
                .append(type).append(",")
                .append(content).toString();
    }
}
