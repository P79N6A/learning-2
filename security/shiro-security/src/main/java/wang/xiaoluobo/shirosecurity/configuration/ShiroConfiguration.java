package wang.xiaoluobo.shirosecurity.configuration;

import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisSentinelManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;
import wang.xiaoluobo.shirosecurity.service.security.context.MySessionManager;
import wang.xiaoluobo.shirosecurity.service.security.filters.*;
import wang.xiaoluobo.shirosecurity.service.security.matcher.MyShiroCredentialsMatcher;
import wang.xiaoluobo.shirosecurity.service.security.realm.MyShiroRealm;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author wangyd
 * @date 2018/9/15
 */
@Configuration
public class ShiroConfiguration {

    @Value("${spring.redis.sentinel.nodes}")
    private String host;

    @Value("${spring.redis.sentinel.master}")
    private String masterName;

    @Value("${spring.redis.password}")
    private String password;


    @Bean
    public FilterRegistrationBean delegatingFilterProxy() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName("shiroFilter");
        filterRegistrationBean.setFilter(proxy);
        return filterRegistrationBean;
    }

    @Bean
    public AdminAuthenticationFilter adminAuthenticationFilter() {
        AdminAuthenticationFilter adminAuthenticationFilter = new AdminAuthenticationFilter();
        return adminAuthenticationFilter;
    }

    @Bean
    public PermsFilter permsFilter() {
        PermsFilter permsFilter = new PermsFilter();
        return permsFilter;
    }

    public AppAuthenticationFilter appAuthenticationFilter() {
        AppAuthenticationFilter appAuthenticationFilter = new AppAuthenticationFilter();
        return appAuthenticationFilter;
    }

    @Bean
    public WxAppAuthenticationFilter wxAppAuthenticationFilter() {
        WxAppAuthenticationFilter wxAppAuthenticationFilter = new WxAppAuthenticationFilter();
        return wxAppAuthenticationFilter;
    }

    @Bean
    public LoginContextFilter loginContextFilter() {
        LoginContextFilter loginContextFilter = new LoginContextFilter();
        return loginContextFilter;
    }

    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        //自定义拦截器
        Map<String, Filter> filtersMap = new LinkedHashMap<String, Filter>();
        filtersMap.put("loginContext", loginContextFilter());
        filtersMap.put("adminAuthenticationFilter", adminAuthenticationFilter());
        filtersMap.put("appAuthenticationFilter", appAuthenticationFilter());
        filtersMap.put("wxAppAuthenticationFilter", wxAppAuthenticationFilter());
        filtersMap.put("perm", permsFilter());

        shiroFilterFactoryBean.setFilters(filtersMap);

        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();

        filterChainDefinitionMap.put("/logout", "logout");
        //微信小程序
        filterChainDefinitionMap.put("/wxapp/**", "wxAppAuthenticationFilter");
        //app端
        filterChainDefinitionMap.put("/app/courier/**", "appAuthenticationFilter");
        filterChainDefinitionMap.put("/app/manager/**", "appAuthenticationFilter");
        //管理页面
        filterChainDefinitionMap.put("/admin/**", "adminAuthenticationFilter");

        // 配置不会被拦截的链接
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/**", "anon");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    @Bean
    public MyShiroRealm myShiroRealm() {
        MyShiroRealm myShiroRealm = new MyShiroRealm();
        myShiroRealm.setCredentialsMatcher(this.credentialsMatcher());
        return myShiroRealm;
    }

    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(myShiroRealm());
        securityManager.setSessionManager(sessionManager());
        securityManager.setCacheManager(shiroRedisManager());
        return securityManager;
    }

    @Bean
    public SessionManager sessionManager() {
        MySessionManager sessionManager = new MySessionManager();
        sessionManager.setSessionDAO(redisSessionDAO());
        sessionManager.setGlobalSessionTimeout(60 * 60 * 24 * 30 * 3 * 1000L);
        return sessionManager;
    }

    public RedisSentinelManager redisManager() {
        RedisSentinelManager redisManager = new RedisSentinelManager();
        redisManager.setHost(host);
        redisManager.setMasterName(masterName);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(password)) {
            redisManager.setPassword(password);
        }

        return redisManager;
    }

    @Bean(name = "shiroRedisManager")
    public RedisCacheManager shiroRedisManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        // 设置shiro的cache前缀
        redisCacheManager.setKeyPrefix("shiro_redis_session:seurity_api");
        redisCacheManager.setRedisManager(redisManager());
        redisCacheManager.setExpire(60 * 60 * 24 * 30 * 3);
        redisCacheManager.setPrincipalIdFieldName("id");

        return redisCacheManager;
    }

    @Bean
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        redisSessionDAO.setExpire(60 * 60 * 24 * 30 * 3 + 1);
        return redisSessionDAO;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    public CredentialsMatcher credentialsMatcher() {
        return new MyShiroCredentialsMatcher();
    }

}
