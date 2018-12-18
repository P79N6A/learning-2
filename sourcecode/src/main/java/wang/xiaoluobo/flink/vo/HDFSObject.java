package wang.xiaoluobo.flink.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Message_Topic_WsInfo = "iss_ws_clog"
 * timestamp,clientId,type,content
 */
@Getter
@Setter
public class HDFSObject implements Serializable {

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
