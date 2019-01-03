package wang.xiaoluobo.designpattern.abstractfactory102.test;

import wang.xiaoluobo.designpattern.abstractfactory102.Provider;
import wang.xiaoluobo.designpattern.abstractfactory102.impl.SendMailFactory;
import wang.xiaoluobo.designpattern.abstractfactory102.impl.SendSMSFactory;
import wang.xiaoluobo.designpattern.factorymethod101.Sender;

public class SendFactory {
    public static void main(String[] args) {
        Provider provider = new SendMailFactory();
        Sender sender = provider.produce();
        sender.send();

        Provider provider1 = new SendSMSFactory();
        Sender sender1 = provider1.produce();
        sender1.send();
    }
}
