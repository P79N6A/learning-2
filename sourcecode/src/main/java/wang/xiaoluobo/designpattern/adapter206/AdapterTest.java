package wang.xiaoluobo.designpattern.adapter206;

public class AdapterTest {

    public static void main(String[] args) {
        // 类适配器
        Targetable target = new Adapter();
        target.method1();
        target.method2();
        System.out.println();

        // 对象适配器
        Source source = new Source();
        Targetable target1 = new Wrapper(source);
        target1.method1();
        target1.method2();
        System.out.println();

        // 接口适配器
        Sourceable source1 = new SourceSub1();
        Sourceable source2 = new SourceSub2();

        source1.method1();
        source1.method2();
        source2.method1();
        source2.method2();
    }
}
