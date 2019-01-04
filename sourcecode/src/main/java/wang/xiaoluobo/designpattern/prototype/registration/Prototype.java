package wang.xiaoluobo.designpattern.prototype.registration;

/**
 * 注册原型模式
 */
public interface Prototype {
    Prototype clone();

    String getName();

    void setName(String name);
}
