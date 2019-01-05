package wang.xiaoluobo.designpattern.decorator207;

public abstract class Person {
    String description = "Unkonwn";

    public String getDescription() {
        return description;
    }

    public abstract double cost();
}
