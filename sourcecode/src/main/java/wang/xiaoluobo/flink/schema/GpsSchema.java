package wang.xiaoluobo.flink.schema;

import com.ishansong.bigdata.statistics.Constant;
import com.ishansong.bigdata.statistics.data.GpsObject;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 将来自Kafka的二进制数据转换为Java对象。
 * 允许程序员指定这个序列化的实现 
 * @author lee
 *
 */
public class GpsSchema implements DeserializationSchema<GpsObject> {

    @Override
    public GpsObject deserialize(byte[] message) throws IOException {
        String s = new String(message, StandardCharsets.UTF_8);
        String[] ss = s.split(Constant.COMMA);
        if (ss.length != 4) {
            String err = "Deserialize failed: The input element fields should be <timestamp,content,clientId,type>.";
            throw new IOException(err);
        }

        GpsObject gpsObj = null;
        try {
            gpsObj = new GpsObject();
            gpsObj.setTimestamp(Long.parseLong(ss[0]));
            gpsObj.setContent(ss[1]);
            gpsObj.setClientId(ss[2]);
            gpsObj.setType(ss[3]);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Deserialize failed:" + e.getMessage());
        }

        return gpsObj;
    }

    @Override
    public boolean isEndOfStream(GpsObject nextElement) {
        return false;
    }

    /**
     * 如果用户的代码实现了DeserializationSchema，那么就需要自己实现getProducedType(...) 方法。
     */
    @Override
    public TypeInformation<GpsObject> getProducedType() {
        return TypeInformation.of(GpsObject.class);
    }

}
