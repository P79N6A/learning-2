package wang.xiaoluobo.designpattern.factorymethod101.impl;

import wang.xiaoluobo.designpattern.factorymethod101.Sender;

public class SMSSender implements Sender {
    @Override
    public void send() {
        System.out.println("sms sender");
    }
}
