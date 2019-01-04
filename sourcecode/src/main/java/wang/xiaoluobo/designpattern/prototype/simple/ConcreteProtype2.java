package wang.xiaoluobo.designpattern.prototype.simple;

public class ConcreteProtype2 extends Prototype {
    public ConcreteProtype2(String id) {
        super(id);
    }

    public Prototype clone() {
        Prototype prototype = new ConcreteProtype2(this.getId());
        return prototype;
    }
}
