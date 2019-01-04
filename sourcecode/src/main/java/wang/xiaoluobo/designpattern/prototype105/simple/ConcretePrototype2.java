package wang.xiaoluobo.designpattern.prototype105.simple;

public class ConcretePrototype2 extends Prototype {
    public ConcretePrototype2(String id) {
        super(id);
    }

    public Prototype clone() {
        Prototype prototype = new ConcretePrototype2(this.getId());
        return prototype;
    }
}
