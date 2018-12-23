package wang.xiaoluobo.spi.service.impl;

import wang.xiaoluobo.spi.service.SPIService;

public class SPISecondServiceImpl implements SPIService {
    @Override
    public String test(String msg) {
        return "second " + msg;
    }
}
