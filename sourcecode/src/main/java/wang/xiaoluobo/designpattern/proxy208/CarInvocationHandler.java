package wang.xiaoluobo.designpattern.proxy208;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * JDK动态代理代理类
 * 实现InvocationHandler
 */
public class CarInvocationHandler implements InvocationHandler {

    private Object car;

    public CarInvocationHandler(Object obj) {
        this.car = obj;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("before");
        Object res = method.invoke(car, args);
        System.out.println("after");
        return res;
    }
}
