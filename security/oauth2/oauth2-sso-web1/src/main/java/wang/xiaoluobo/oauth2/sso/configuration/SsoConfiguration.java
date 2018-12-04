package wang.xiaoluobo.oauth2.sso.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.RequestContextListener;

/**
 * @author wangyd
 * @date 2018/8/27
 */
@Configuration
public class SsoConfiguration {

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

}
