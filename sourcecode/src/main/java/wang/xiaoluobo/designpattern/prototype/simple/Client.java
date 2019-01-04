package wang.xiaoluobo.designpattern.prototype.simple;

public class Client {
    public static void main(String[] args) {
        ConcretePrototype1 prototype1 = new ConcretePrototype1("Protype1");
        ConcretePrototype1 prototypeCopy1 = (ConcretePrototype1) prototype1.clone();
        System.out.println(prototypeCopy1.getId());
        System.out.println(prototype1.toString());
        System.out.println(prototypeCopy1.toString());

        ConcretePrototype2 prototype2 = new ConcretePrototype2("Protype2");
        ConcretePrototype2 prototypeCopy2 = (ConcretePrototype2) prototype2.clone();
        System.out.println(prototypeCopy2.getId());
        System.out.println(prototype2.toString());
        System.out.println(prototypeCopy2.toString());

        ConcretePrototype1 prototype3 = new ConcretePrototype1("Protype3");
        ConcretePrototype1 prototypeCopy3 = prototype3;
        System.out.println(prototypeCopy3.getId());
        System.out.println(prototype3.toString());
        System.out.println(prototypeCopy3.toString());
    }
}
