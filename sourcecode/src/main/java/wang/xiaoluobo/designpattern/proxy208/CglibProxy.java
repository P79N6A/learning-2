package wang.xiaoluobo.designpattern.proxy208;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * cglib
 */
public class CglibProxy implements MethodInterceptor {

    private Object car;

    public Object getInstance(Object object) {
        this.car = object;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.car.getClass());
        enhancer.setCallback(this);
        return enhancer.create();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println("before");
        proxy.invokeSuper(obj, args);
        System.out.println("after");
        return null;
    }
}
