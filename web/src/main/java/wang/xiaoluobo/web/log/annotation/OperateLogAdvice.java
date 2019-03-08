package wang.xiaoluobo.web.log.annotation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 记录用户操作日志
 *
 * @author: WangYandong
 * @date: 2013-8-6 下午10:09:42
 * @version: v1.0
 */
@Aspect
@Component
public class OperateLogAdvice {

    @Pointcut("@annotation(wang.xiaoluobo.web.log.annotation.OperateLog)")
    public void log() {

    }

    @Before("log()")
    public void before(JoinPoint joinPoint) {
        System.out.println("method start");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperateLog operateLog = method.getAnnotation(OperateLog.class);
        String defaultMessage = operateLog.value();
        String methodName = signature.getClass().getName() + "." + method.getName();
        System.out.println(methodName + "---" + defaultMessage);
    }

    @After("log()")
    public void after(JoinPoint joinPoint) {
        System.out.println("method after");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperateLog annotation = method.getAnnotation(OperateLog.class);
        if (annotation == null)
            return;
        String defaultMessage = annotation.value();
        String methodName = signature.getClass().getName() + "." + method.getName();
        System.out.println(methodName + "---" + defaultMessage);
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
