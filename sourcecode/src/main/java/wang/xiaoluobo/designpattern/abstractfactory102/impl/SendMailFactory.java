package wang.xiaoluobo.designpattern.abstractfactory102.impl;

import wang.xiaoluobo.designpattern.abstractfactory102.Provider;
import wang.xiaoluobo.designpattern.factorymethod101.Sender;
import wang.xiaoluobo.designpattern.factorymethod101.impl.MailSender;

public class SendMailFactory implements Provider {
    @Override
    public Sender produce() {
        return new MailSender();
    }
}
