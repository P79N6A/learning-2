package wang.xiaoluobo.designpattern.visitor321;

public interface Subject {

    void accept(Visitor visitor);

    String getSubject();
}
