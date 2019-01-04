package wang.xiaoluobo.designpattern.prototype.simple;

public class Client {
    public static void main(String[] args) {
        ConcreteProtype1 protype1 = new ConcreteProtype1("Protype1");
        ConcreteProtype1 protypeCopy1 = (ConcreteProtype1) protype1.clone();
        System.out.println(protypeCopy1.getId());
        System.out.println(protype1.toString());
        System.out.println(protypeCopy1.toString());

        ConcreteProtype2 protype2 = new ConcreteProtype2("Protype2");
        ConcreteProtype2 protypeCopy2 = (ConcreteProtype2) protype2.clone();
        System.out.println(protypeCopy2.getId());
        System.out.println(protype2.toString());
        System.out.println(protypeCopy2.toString());

        ConcreteProtype1 protype3 = new ConcreteProtype1("Protype3");
        ConcreteProtype1 protypeCopy3 = protype3;
        System.out.println(protypeCopy3.getId());
        System.out.println(protype3.toString());
        System.out.println(protypeCopy3.toString());
    }
}
