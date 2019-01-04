package wang.xiaoluobo.designpattern.prototype.simple;

public class ConcreteProtype1 extends Prototype {
    public ConcreteProtype1(String id) {
        super(id);
    }

    public Prototype clone() {
        Prototype prototype = new ConcreteProtype1(this.getId());
        return prototype;
    }
}
