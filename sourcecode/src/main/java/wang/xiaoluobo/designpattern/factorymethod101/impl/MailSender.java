package wang.xiaoluobo.designpattern.factorymethod101.impl;

import wang.xiaoluobo.designpattern.factorymethod101.Sender;

public class MailSender implements Sender {
    @Override
    public void send() {
        System.out.println("mail sender");
    }
}
