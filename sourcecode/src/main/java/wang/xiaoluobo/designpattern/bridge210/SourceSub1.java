package wang.xiaoluobo.designpattern.bridge210;

public class SourceSub1 implements Sourceable {

    @Override
    public void method() {
        System.out.println("this is the first sub!");
    }
}
