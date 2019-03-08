package wang.xiaoluobo.webwar.log.annotation;

import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

/**
 * 记录用户操作日志
 *
 * @author: WangYandong
 * @date: 2013-8-6 下午10:09:42
 * @version: v1.0
 */
public class OperateLogAdvice implements MethodBeforeAdvice, AfterReturningAdvice {

    @Override
    public void before(Method method, Object[] arg1, Object target) throws Throwable {
        System.out.println("方法执行前调用日志");
        // 判断方法是否注解了UserOperateLog
        OperateLog annotation = method.getAnnotation(OperateLog.class);
        if (annotation == null)
            return;
        String defaultMessage = annotation.value();
        String methodName = target.getClass().getName() + "." + method.getName();
        System.out.println(methodName + defaultMessage);
    }

    @Override
    public void afterReturning(Object returnObj, Method method, Object[] arg, Object target) throws Throwable {
        System.out.println("方法执行后调用日志");
        OperateLog annotation = method.getAnnotation(OperateLog.class);
        if (annotation == null)
            return;
        String defaultMessage = annotation.value();
        String methodName = target.getClass().getName() + "." + method.getName();
        System.out.println(methodName + defaultMessage);
    }
}
