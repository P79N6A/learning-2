package wang.xiaoluobo.designpattern.templatemethod314;

public class ScanBicycle extends AbstractBicycle {

    @Override
    protected void unlock() {
        System.out.println("========" + "扫码开锁" + "========");
    }

    @Override
    protected void ride() {
        System.out.println(getClass().getSimpleName() + "骑起来很拉风");
    }

    protected void isNeedUnlock(boolean isNeedUnlock) {
        this.isNeedUnlock = isNeedUnlock;
    }
}