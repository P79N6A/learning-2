package wang.xiaoluobo.web.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan("wang.xiaoluobo.web.service")
@EnableAspectJAutoProxy
public class AopConfig {

}
