package wang.xiaoluobo.designpattern.factorymethod101.test;

import wang.xiaoluobo.designpattern.factorymethod101.Sender;
import wang.xiaoluobo.designpattern.factorymethod101.impl.MailSender;
import wang.xiaoluobo.designpattern.factorymethod101.impl.SMSSender;

/**
 * 多个工厂方法模式
 */
public class SendFactory2 {
    public Sender produceMail(){
        return new MailSender();
    }

    public Sender produceSms(){
        return new SMSSender();
    }

    public static void main(String[] args) {
        SendFactory2 factory = new SendFactory2();
        Sender sender = factory.produceSms();
        sender.send();

        Sender sender1 = factory.produceMail();
        sender1.send();
    }
}
