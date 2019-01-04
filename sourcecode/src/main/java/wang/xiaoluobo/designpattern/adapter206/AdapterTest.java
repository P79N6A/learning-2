package wang.xiaoluobo.designpattern.adapter206;

public class AdapterTest {

    public static void main(String[] args) {
        Targetable target = new Adapter();
        target.method1();
        target.method2();
    }
}
