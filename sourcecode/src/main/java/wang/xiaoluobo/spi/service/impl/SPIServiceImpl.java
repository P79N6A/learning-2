package wang.xiaoluobo.spi.service.impl;

import wang.xiaoluobo.spi.service.SPIService;

public class SPIServiceImpl implements SPIService {

    @Override
    public String test(String msg) {
        return "receive " + msg;
    }
}
