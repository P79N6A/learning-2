package wang.xiaoluobo.flink.data;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Message_Topic_WsInfo = "iss_mobile_static"
 * timestamp,content,clientId,type
 * 1539677071483,mobile:"15910705020" imei:"9809890987" device:"Phone" local:"Test" os:"ANDROID" osVersion:"1.9.1.3" appVersion:"0.9.9.2" ,
 * 1000000505,STATIC
 */
@Getter
@Setter
public class DeviceObject implements Serializable {

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
