package wang.xiaoluobo.redis.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author wangyd
 * @date 2018/7/18 上午10:37
 */
@Configuration
@ConfigurationProperties(prefix="redis")
public class RedisProperties {

    private String host;
    private int port;
    private String password;
    private int timeout;
    private int database;
    private boolean ifUseSentinel;
    private int maxIdle;
    private int minIdle;
    private int maxActive;
    private int maxWait;
    private boolean testOnBorrow;
    private boolean testOnReturn;
    private String master;
    private String sentinel;
    /**
     * @see org.springframework.data.redis.serializer.RedisSerializer
     * @see org.springframework.data.redis.serializer.StringRedisSerializer
     * @see org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
     * etc.
     *
     * org.springframework.data.redis.serializer.StringRedisSerializer
     */
    private String keySerializer;

    private String valueSerializer;

    private String hashKeySerializer;

    private String hashValueSerializer;


    public RedisProperties() {

    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public boolean getIfUseSentinel() {
        return ifUseSentinel;
    }

    public void setIfUseSentinel(boolean ifUseSentinel) {
        this.ifUseSentinel = ifUseSentinel;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public int getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }

    public boolean getTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean getTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public String getSentinel() {
        return sentinel;
    }

    public void setSentinel(String sentinel) {
        this.sentinel = sentinel;
    }

    public String getKeySerializer() {
        return keySerializer;
    }

    public void setKeySerializer(String keySerializer) {
        this.keySerializer = keySerializer;
    }

    public String getValueSerializer() {
        return valueSerializer;
    }

    public void setValueSerializer(String valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    public String getHashKeySerializer() {
        return hashKeySerializer;
    }

    public void setHashKeySerializer(String hashKeySerializer) {
        this.hashKeySerializer = hashKeySerializer;
    }

    public String getHashValueSerializer() {
        return hashValueSerializer;
    }

    public void setHashValueSerializer(String hashValueSerializer) {
        this.hashValueSerializer = hashValueSerializer;
    }
}
