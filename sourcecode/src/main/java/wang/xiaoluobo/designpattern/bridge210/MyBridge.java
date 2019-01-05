package wang.xiaoluobo.designpattern.bridge210;

public class MyBridge extends Bridge {
    public void method(){
        getSource().method();
    }
}
