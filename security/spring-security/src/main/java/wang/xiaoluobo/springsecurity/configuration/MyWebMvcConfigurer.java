package wang.xiaoluobo.springsecurity.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author wangyd
 * @date 2018/8/31
 */
@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/home").setViewName("home");
//        registry.addViewController("/login").setViewName("login");
//        registry.addViewController("/test").setViewName("test");
//        registry.addViewController("/admin").setViewName("admin");
    }

}
