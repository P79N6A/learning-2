package wang.xiaoluobo.designpattern.adapter206;

/**
 * 类适配器
 */
public class Adapter extends Source implements Targetable {

    @Override
    public void method2() {
        System.out.println("this is the targetable method!");
    }
}
