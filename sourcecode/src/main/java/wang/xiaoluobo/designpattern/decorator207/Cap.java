package wang.xiaoluobo.designpattern.decorator207;

/**
 * 鸭舌帽
 */
public class Cap extends HatDecorator {

    Person person;

    public Cap(Person person) {
        this.person = person;
    }

    @Override
    public String getDescription() {
        return person.getDescription() + "a casquette ";
    }

    @Override
    public double cost() {
        return 75 + person.cost();
    }
}

