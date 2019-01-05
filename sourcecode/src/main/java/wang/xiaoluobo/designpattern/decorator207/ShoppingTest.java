package wang.xiaoluobo.designpattern.decorator207;

public class ShoppingTest {
    public static void main(String[] args) {
        Person person = new Teenager();
        person = new Shirt(person);
        person = new Shirt(person);
        person = new Cap(person);
        person = new Cap(person);

        System.out.println(person.getDescription() + "￥" + person.cost());
    }
}
