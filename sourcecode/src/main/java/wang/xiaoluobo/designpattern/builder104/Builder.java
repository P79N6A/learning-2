package wang.xiaoluobo.designpattern.builder104;

public abstract class Builder {

    protected abstract Product build();

    protected abstract void buildName();

    protected abstract void buildType();

    protected abstract void buildDesc();
}
