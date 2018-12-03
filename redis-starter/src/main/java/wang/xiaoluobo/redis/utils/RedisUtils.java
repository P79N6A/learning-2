package wang.xiaoluobo.redis.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * redis utils
 * @author wangyd
 * @date 2018/7/17 上午10:11
 */
@Component
public class RedisUtils {

    private static RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        RedisUtils.redisTemplate = redisTemplate;
    }


    public static void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public static void set(String key, Object value, long expireTime) {
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }

    public static void delect(String... keys) {
        if(keys == null || keys.length == 0){
            return;
        }

        if(keys.length == 1) {
            redisTemplate.delete(keys[0]);
        }else {
            redisTemplate.delete(Arrays.asList(keys));
        }
    }

    public static Object get(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value;
    }

    public static <T> T get(String key, Class<T> clazz) {
        try {
            T value = (T) redisTemplate.opsForValue().get(key);
            return value;
        } catch (Exception e) {

        }
        return null;
    }

    public static void setList(String key, Object value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    public static void setList(String key, Object value, long expireTime) {
        redisTemplate.opsForList().rightPush(key, value);
        redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }

    public static List<Object> getList(String key) {
        ListOperations<String, Object> operations = redisTemplate.opsForList();
        long length = operations.size(key);
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            list.add(operations.index(key, i));
        }
        return list;
    }

    public static <T> List<T> getList(String key, Class<T> clazz) {
        try {
            ListOperations<String, Object> operations = redisTemplate.opsForList();
            long length = operations.size(key);
            List<T> list = new ArrayList<T>();
            for (int i = 0; i < length; i++) {
                list.add((T) operations.index(key, i));
            }
            return list;
        } catch (Exception e) {

        }
        return null;
    }

    public static Long removeList(String key, long count, Object value){
        return redisTemplate.opsForList().remove(key, count, value);
    }

    public static void setHash(String key, Object hashKey, Object hashValue) {
        redisTemplate.opsForHash().put(key, hashKey, hashValue);
    }

    public static void setHash(String key, Object hashKey, Object hashValue, long expireTime) {
        redisTemplate.opsForHash().put(key, hashKey, hashValue);
        redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }

    public static void removeHash(String key, Object... hashKeys){
        redisTemplate.opsForHash().delete(key, hashKeys);
    }

    public static Object getHash(String key, Object hashKey) {
        Object hashValue = redisTemplate.opsForHash().get(key, hashKey);
        return hashValue;
    }

    public static <T> T getHash(String key, Object hashKey, Class<T> clazz) {
        try {
            T hashValue = (T) redisTemplate.opsForHash().get(key, hashKey);
            return hashValue;
        } catch (Exception e) {

        }
        return null;
    }

    public static void setSet(String key, Object value) {
        redisTemplate.opsForSet().add(key, value);
    }

    public static void setSet(String key, Object value, long expireTime) {
        redisTemplate.opsForSet().add(key, value);
        redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }

    public static void removeSet(String key, Object... values){
        redisTemplate.opsForSet().remove(key, values);
    }

    public static Object getSet(String key) {
        Object value = redisTemplate.opsForSet().pop(key);
        return value;
    }

    public static <T> T getSet(String key, Class<T> clazz) {
        try {
            T value = (T) redisTemplate.opsForSet().pop(key);
            return value;
        } catch (Exception e) {

        }
        return null;
    }

    public static void setZSet(String key, Object member, double score) {
        redisTemplate.opsForZSet().add(key, member, score);
    }

    public static void setZSet(String key, Object member, double score, long expireTime) {
        redisTemplate.opsForZSet().add(key, member, score);
        redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }

    public static void removeZSet(String key, Object... values){
        redisTemplate.opsForZSet().remove(key, values);
    }

    public static double getZSet(String key, Object member) {
        ZSetOperations<String, Object> operations = redisTemplate.opsForZSet();
        double value = operations.score(key, member);
        operations.score(key, member);
        return value;
    }
}
