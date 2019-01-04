package wang.xiaoluobo.designpattern.prototype105.registration;

/**
 * 注册原型模式
 */
public interface Prototype {
    Prototype clone();

    String getName();

    void setName(String name);
}
