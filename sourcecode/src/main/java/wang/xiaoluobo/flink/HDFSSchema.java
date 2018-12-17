package wang.xiaoluobo.flink;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 将来自Kafka的二进制数据转换为Java对象。
 * 允许程序员指定这个序列化的实现 
 */
public class HDFSSchema implements DeserializationSchema<HDFSObject> {

    @Override
    public HDFSObject deserialize(byte[] message) throws IOException {
        String s = new String(message, StandardCharsets.UTF_8);
        String[] ss = s.split(Constant.COMMA);
        if (ss.length != 4) {
            String err = "Deserialize failed: The input element fields should be <timestamp,taskId,cid,eventType>.";
            throw new IOException(err);
        }

        HDFSObject hdfsObject = null;
        try {
            hdfsObject = new HDFSObject();
            hdfsObject.setTimestamp(Long.parseLong(ss[0]));
            hdfsObject.setClientId(ss[1]);
            hdfsObject.setType(ss[2]);
            hdfsObject.setContent(ss[3]);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Deserialize failed:" + e.getMessage());
        }

        return hdfsObject;
    }

    @Override
    public boolean isEndOfStream(HDFSObject nextElement) {
        return false;
    }

    /**
     * 如果用户的代码实现了DeserializationSchema，那么就需要自己实现getProducedType(...) 方法。
     */
    @Override
    public TypeInformation<HDFSObject> getProducedType() {
        return TypeInformation.of(HDFSObject.class);
    }

}
