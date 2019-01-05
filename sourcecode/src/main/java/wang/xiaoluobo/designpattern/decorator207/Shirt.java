package wang.xiaoluobo.designpattern.decorator207;

public class Shirt extends ClothingDecorator {

    // 用实例变量保存Person的引用
    Person person;

    public Shirt(Person person) {
        this.person = person;
    }

    @Override
    public String getDescription() {
        return person.getDescription() + "a shirt ";
    }

    @Override
    public double cost() {
        // 实现了cost()方法，并调用了person的cost()方法，目的是获得所有累加值
        return 100 + person.cost();
    }
}
