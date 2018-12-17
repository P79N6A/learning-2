package wang.xiaoluobo.flink;

public class Constant {

    public static final String COMMA = ",";

    public static final String COLON = ":";

    // seconds
    public static final int CheckPoint = 55000;

    // seconds
    public static final int Delay = 55;

    /**
     * 数据在hdfs中的存储路径
     */
    public static final String HDFS_BASE_DIR = "/bigdata/statistics";

    public static final String TOPIC_ISS_WS_CLOG = "iss_ws_clog";

    public static final String TOPIC_ISS_MOBILE_STATIC  = "iss_mobile_static_info";

    public static final String TOPIC_ISS_MOBILE_DYNAMIC  = "iss_mobile_dynamic_info";

    public static final String TOPIC_ISS_MOBILE_GPS  = "iss_mobile_gps_info";


    public static final String STATIC  = "deviceinfo";
    public static final String GPS  = "gpsinfo";
    public static final String DYNAMIC  = "dynamic";
}
