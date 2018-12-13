package wang.xiaoluobo.springsecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import wang.xiaoluobo.redis.configuration.RedisConfig;

/**
 * SpringSecurityApplication扫描不到{@link RedisConfig}，帮需要引入该包
 */
@Import(RedisConfig.class)
@SpringBootApplication
public class SpringSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityApplication.class, args);
    }
}
