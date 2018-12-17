package wang.xiaoluobo.flink.data;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Message_Topic_WsInfo = "iss_mobile_dynamic"
 * timestamp,content,clientId,type
 * 1539677071483,region:"Beijing" ip:"10.53.254.11" ,1000000500,DYNAMIC
 */
@Getter
@Setter
public class DynamicObject implements Serializable {

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
