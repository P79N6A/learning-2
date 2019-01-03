package wang.xiaoluobo.designpattern.factorymethod101.test;

import wang.xiaoluobo.designpattern.factorymethod101.Sender;
import wang.xiaoluobo.designpattern.factorymethod101.impl.MailSender;
import wang.xiaoluobo.designpattern.factorymethod101.impl.SMSSender;

/**
 * 简单工厂模式
 */
public class SendFactory1 {
    public Sender produce(String type) {
        if ("mail".equals(type)) {
            return new MailSender();
        } else if ("sms".equals(type)) {
            return new SMSSender();
        }

        return null;
    }

    public static void main(String[] args) {
        SendFactory1 factory = new SendFactory1();
        Sender sender = factory.produce("sms");
        sender.send();

        Sender sender1 = factory.produce("mail");
        sender1.send();
    }
}
