package wang.xiaoluobo.designpattern.visitor321;

public class VisitorTest {

    public static void main(String[] args) {
        Visitor visitor = new MyVisitor();
        Subject sub = new MySubject();
        sub.accept(visitor);
    }
}
