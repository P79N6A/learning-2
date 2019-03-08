package wang.xiaoluobo.web.log;

import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * spring aop 不使用注解方式
 * 拦截指定方法
 */
@Aspect
@Component
public class LogAdvice {

    @Pointcut("execution(public * wang.xiaoluobo.web.*.*.delete(..))")
    public void log() {

    }

    @Before("log()")
    public void before() {
        System.out.println("method start");
    }

    @After("log()")
    public void after() {
        System.out.println("method after");
    }

    @AfterReturning("execution(public * wang.xiaoluobo.web.*.*.delete(..))")
    public void AfterReturning() {
        System.out.println("method AfterReturning");
    }

    @AfterThrowing("execution(public * wang.xiaoluobo.web.*.*.delete(..))")
    public void AfterThrowing() {
        System.out.println("method AfterThrowing");
    }
}
