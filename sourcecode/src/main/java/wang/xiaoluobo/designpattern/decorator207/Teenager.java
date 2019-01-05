package wang.xiaoluobo.designpattern.decorator207;

public class Teenager extends Person {
    public Teenager() {
        description = "Shopping List:";
    }

    @Override
    public double cost() {
        // 什么都没买，不用钱
        return 0;
    }
}
