### shiro version 1.4.0
#### redis单点升级为哨兵，shiro遇到的坑

    升级前shiro-redis版本为2.4.2.1-RELEASE
    升级后shiro-redis版本为3.2.1
    用户反映每隔半小时就会掉线，默认session设置为3个月过期(方便用户使用)
    
#### 以下是修改前的代码
```java
@Bean
public SessionManager sessionManager() {
    MySessionManager mySessionManager = new MySessionManager();
    mySessionManager.setSessionDAO(redisSessionDAO());
    return mySessionManager;
}

public RedisManager redisManager() {
    RedisManager redisManager = new RedisManager();
    redisManager.setHost(host);
    redisManager.setPort(port);
    redisManager.setExpire(60*60*24*30*3);
    return redisManager;
}

@Bean(name = "shiroRedisManager")
public RedisCacheManager shiroRedisManager() {
    RedisCacheManager redisCacheManager = new RedisCacheManager();
    redisCacheManager.setKeyPrefix("shiro_redis_session:prefix_key");
    redisCacheManager.setRedisManager(redisManager());
    return redisCacheManager;
}

@Bean
public RedisSessionDAO redisSessionDAO() {
    RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
    redisSessionDAO.setRedisManager(redisManager());
    return redisSessionDAO;
}
```

#### 以下是修改后的代码
```java
@Bean
public SessionManager sessionManager() {
    MySessionManager mySessionManager = new MySessionManager();
    mySessionManager.setSessionDAO(redisSessionDAO());
    mySessionManager.setGlobalSessionTimeout(60*60*24*30*3 * 1000L);    // session超时时间
    return mySessionManager;
}

public RedisSentinelManager redisManager() {
    RedisSentinelManager redisManager = new RedisSentinelManager();
    redisManager.setHost(host);
    redisManager.setMasterName(masterName);
    if(StringUtils.isNotBlank(password)) {
        redisManager.setPassword(password);
    }
    return redisManager;
}

@Bean(name = "shiroRedisManager")
public RedisCacheManager shiroRedisManager() {
    RedisCacheManager redisCacheManager = new RedisCacheManager();
    redisCacheManager.setKeyPrefix("shiro_redis_session:prefix_key");
    redisCacheManager.setRedisManager(redisManager());
    redisCacheManager.setExpire(60*60*24*30*3);
    redisCacheManager.setPrincipalIdFieldName("id");
    return redisCacheManager;
}

@Bean
public RedisSessionDAO redisSessionDAO() {
    RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
    redisSessionDAO.setRedisManager(redisManager());
    redisSessionDAO.setExpire(60*60*24*30*3);   // RedisSessionDAO设置过期解决问题
    return redisSessionDAO;
}
```

### shiro源码
```java
/**
 * Please make sure expire is longer than sesion.getTimeout()
 */
private int expire = DEFAULT_EXPIRE;

/**
 * save session
 * @param session
 * @throws UnknownSessionException
 */
private void saveSession(Session session) throws UnknownSessionException {
    if (session == null || session.getId() == null) {
        logger.error("session or session id is null");
        throw new UnknownSessionException("session or session id is null");
    }
    byte[] key;
    byte[] value;
    try {
        key = keySerializer.serialize(getRedisSessionKey(session.getId()));
        value = valueSerializer.serialize(session);
    } catch (SerializationException e) {
        logger.error("serialize session error. session id=" + session.getId());
        throw new UnknownSessionException(e);
    }
    
    // 如果不设置超时时间，则将过期时间默认设置为session timeout时间
    if (expire == DEFAULT_EXPIRE) {
        this.redisManager.set(key, value, (int) (session.getTimeout() / MILLISECONDS_IN_A_SECOND));
        return;
    }
    
    
    if (expire != NO_EXPIRE && expire * MILLISECONDS_IN_A_SECOND < session.getTimeout()) {
        logger.warn("Redis session expire time: "
                + (expire * MILLISECONDS_IN_A_SECOND)
                + " is less than Session timeout: "
                + session.getTimeout()
                + " . It may cause some problems.");
    }
    this.redisManager.set(key, value, expire);
}
```
