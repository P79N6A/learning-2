package wang.xiaoluobo.spi;

import wang.xiaoluobo.spi.service.SPIService;

import java.util.Iterator;
import java.util.ServiceLoader;

public class TestSPI {

    public static void main(String[] args) {
        ServiceLoader<SPIService> serviceLoader = ServiceLoader.load(SPIService.class);
        Iterator<SPIService> it = serviceLoader.iterator();
        while (it != null && it.hasNext()) {
            SPIService spiService = it.next();
            System.out.println("class:" + spiService.getClass().getName() + "---" + spiService.test("spi"));
        }
    }
}
