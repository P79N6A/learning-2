package wang.xiaoluobo.designpattern.factorymethod101.test;

import wang.xiaoluobo.designpattern.factorymethod101.Sender;
import wang.xiaoluobo.designpattern.factorymethod101.impl.MailSender;
import wang.xiaoluobo.designpattern.factorymethod101.impl.SMSSender;

/**
 * 静态工厂方法模式
 */
public class SendFactory3 {
    public static Sender produceMail(){
        return new MailSender();
    }

    public static Sender produceSms(){
        return new SMSSender();
    }

    public static void main(String[] args) {
        Sender sender = SendFactory3.produceSms();
        sender.send();

        Sender sender1 = SendFactory3.produceMail();
        sender1.send();
    }
}
