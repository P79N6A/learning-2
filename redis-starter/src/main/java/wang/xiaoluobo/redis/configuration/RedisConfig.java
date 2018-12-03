package wang.xiaoluobo.redis.configuration;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * redis configuration
 * @author wangyd
 * @date 2018/7/18 上午10:31
 */
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfig {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory jedisConnectionFactory = null;

        JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfigurationBuilder = JedisClientConfiguration.builder()
                .connectTimeout(Duration.of(redisProperties.getTimeout(), ChronoUnit.SECONDS))
                .readTimeout(Duration.of(redisProperties.getTimeout(), ChronoUnit.SECONDS));
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(redisProperties.getMaxIdle());
        genericObjectPoolConfig.setMaxTotal(redisProperties.getMaxActive());
        genericObjectPoolConfig.setMinIdle(redisProperties.getMinIdle());
        genericObjectPoolConfig.setMaxWaitMillis(redisProperties.getMaxWait());
        genericObjectPoolConfig.setTestOnBorrow(redisProperties.getTestOnBorrow());
        genericObjectPoolConfig.setTestOnReturn(redisProperties.getTestOnReturn());
        JedisClientConfiguration jedisClientConfiguration = jedisClientConfigurationBuilder.usePooling()
                .poolConfig(genericObjectPoolConfig).build();

        boolean ifUseSentinel = redisProperties.getIfUseSentinel();
        if (ifUseSentinel) {
            RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration();
            sentinelConfig.setMaster(redisProperties.getMaster());
            sentinelConfig.setPassword(RedisPassword.of(redisProperties.getPassword()));
            sentinelConfig.setDatabase(redisProperties.getDatabase());

            String[] nodes = redisProperties.getSentinel().split(",");
            for (int i = 0, length = nodes.length; i < length; i++) {
                String[] node = nodes[i].split(":");
                sentinelConfig.sentinel(node[0], Integer.valueOf(node[1]));
            }

            jedisConnectionFactory = new JedisConnectionFactory(sentinelConfig, jedisClientConfiguration);
            return jedisConnectionFactory;
        }else {
            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
            redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());
            redisStandaloneConfiguration.setHostName(redisProperties.getHost());
            redisStandaloneConfiguration.setPassword(RedisPassword.of(redisProperties.getPassword()));
            redisStandaloneConfiguration.setPort(redisProperties.getPort());

            jedisConnectionFactory = new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration);
            return jedisConnectionFactory;
        }
    }

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
