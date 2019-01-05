package wang.xiaoluobo.designpattern.proxy208;

import java.lang.reflect.Proxy;

public class ProxyTest {

    public static void main(String[] args) {
        // jdk proxy
        Car car = new CarImpl();
        CarInvocationHandler carInvocationHandler = new CarInvocationHandler(car);
        Car proxy = (Car) Proxy.newProxyInstance(ProxyTest.class.getClassLoader(),
                car.getClass().getInterfaces(), carInvocationHandler);
        proxy.run();


        // cglib proxy
        CglibProxy cglibProxy = new CglibProxy();
        CarNoInterface carNoInterface = (CarNoInterface) cglibProxy.getInstance(new CarNoInterface());
        carNoInterface.run();

        CglibProxy cglibProxy1 = new CglibProxy();
        Car c = (Car)cglibProxy1.getInstance(car);
        c.run();
    }
}
