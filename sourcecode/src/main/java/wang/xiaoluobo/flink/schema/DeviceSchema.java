package wang.xiaoluobo.flink.schema;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import wang.xiaoluobo.flink.Constant;
import wang.xiaoluobo.flink.data.DeviceObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 将来自Kafka的二进制数据转换为Java对象。
 * 允许程序员指定这个序列化的实现 
 *
 */
public class DeviceSchema implements DeserializationSchema<DeviceObject> {

    @Override
    public DeviceObject deserialize(byte[] message) throws IOException {
        String s = new String(message, StandardCharsets.UTF_8);
        String[] ss = s.split(Constant.COMMA);
        if (ss.length != 4) {
            String err = "Deserialize failed: The input element fields should be <timestamp,content,clientId,type>.";
            throw new IOException(err);
        }

        DeviceObject deviceObj = null;
        try {
            deviceObj = new DeviceObject();
            deviceObj.setTimestamp(Long.parseLong(ss[0]));
            deviceObj.setContent(ss[1]);
            deviceObj.setClientId(ss[2]);
            deviceObj.setType(ss[3]);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Deserialize failed:" + e.getMessage());
        }

        return deviceObj;
    }

    @Override
    public boolean isEndOfStream(DeviceObject nextElement) {
        return false;
    }

    /**
     * 如果用户的代码实现了DeserializationSchema，那么就需要自己实现getProducedType(...) 方法。
     */
    @Override
    public TypeInformation<DeviceObject> getProducedType() {
        return TypeInformation.of(DeviceObject.class);
    }

}
