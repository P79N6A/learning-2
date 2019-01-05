package wang.xiaoluobo.designpattern.templatemethod314;

public class TemplateMethodTest {

    public static void main(String[] args) {
        ScanBicycle scanBicycle = new ScanBicycle();
        scanBicycle.isNeedUnlock(false);
        scanBicycle.use();

        CodeBicycle codeBicycle = new CodeBicycle();
        codeBicycle.isNeedUnlock(true);
        codeBicycle.use();
    }
}
