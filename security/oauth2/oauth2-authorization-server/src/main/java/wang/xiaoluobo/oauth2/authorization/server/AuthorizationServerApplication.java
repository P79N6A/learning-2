package wang.xiaoluobo.oauth2.authorization.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

/**
 * @author wangyd
 * @date 2018/8/23
 */
@EnableResourceServer
@SpringBootApplication
public class AuthorizationServerApplication {

    /**
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(AuthorizationServerApplication.class, args);
    }
}
