package wang.xiaoluobo.flink;

import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.fs.bucketing.BucketingSink;
import org.apache.flink.streaming.connectors.fs.bucketing.DateTimeBucketer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.xiaoluobo.flink.schema.HDFSSchema;
import wang.xiaoluobo.flink.vo.HDFSObject;

import java.util.Properties;

/**
 * @author wangyd
 * @date 2018/12/17
 */
public class FlinkApplication {

    private static String kafka;
    private static String topic;
    private static int interval;
    private static final Logger logger = LoggerFactory.getLogger(FlinkApplication.class);

    public static void main(String[] args) throws Exception {
        if (args.length != 6) {
            throw new Exception("Wrong number of args.");
        }
        ParameterTool parameters = ParameterTool.fromArgs(args);

        kafka = parameters.get("kafka");
        interval = parameters.getInt("interval");
        topic = parameters.get("topic");
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(Constants.CheckPoint);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        fromKafka2HDFS(env);

        env.execute("bigdata-statistics-service" + topic);
        logger.info("Application started.");
    }

    private static void fromKafka2HDFS(StreamExecutionEnvironment env) throws Exception {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", kafka);
        props.setProperty("group.id", Constants.TOPIC_NAME + "_group_id");

        FlinkKafkaConsumer011<HDFSObject> source = new FlinkKafkaConsumer011<>(Constants.TOPIC_NAME, new HDFSSchema(), props);
        source.assignTimestampsAndWatermarks(new AscendingTimestampExtractor<HDFSObject>() {
            @Override
            public long extractAscendingTimestamp(HDFSObject element) {
                if (element != null) {
                    logger.info(element.toString());
                    return element.getTimestamp();
                }
                return 0L;
            }
        });

        DataStream<String> dataStream = env
                .addSource(source)
                .keyBy("clientId")
                .window(TumblingEventTimeWindows.of(Time.seconds(interval)))
                .allowedLateness(Time.seconds(Constants.Delay))
                .apply(new WindowFunction<HDFSObject, String, Tuple, TimeWindow>() {
                    @Override
                    public void apply(Tuple tuple, TimeWindow window, Iterable<HDFSObject> input, Collector<String> out) throws Exception {
                        for (HDFSObject hdfsObject : input) {
                            logger.info("fromKafka2HDFS is {}", hdfsObject.toString());
                            out.collect(hdfsObject.toString());
                        }
                    }
                });

        BucketingSink bucketingSink = new BucketingSink<String>(Constants.HDFS_BASE_DIR)
                .setBucketer(new DateTimeBucketer("yyyy-MM-dd"))
                // bucket size: 500MB
                .setBatchSize(500 * 1024 * 1024L)
                // 设置文件前缀，默认为 part
                .setPartPrefix("bigdata-")
                // 一小时
                .setBatchRolloverInterval(60 * 60 * 1000);

        dataStream.addSink(bucketingSink);
    }
}
