package wang.xiaoluobo.designpattern.flyweight212;

import java.util.ArrayList;
import java.util.List;

public class FlyWeightTest {

    private static List<FlyWeight> orders = new ArrayList<>();

    private static FlyWeightFactory factory;

    public static void main(String[] args) {
        factory = FlyWeightFactory.getInstance();
        takeOrders("三国演义");
        takeOrders("水浒传");
        takeOrders("封神榜");
        takeOrders("三");
        takeOrders("红楼梦");
        takeOrders("三国演义");
        takeOrders("封神榜");
        takeOrders("水浒传");
        takeOrders("西游记");
        takeOrders("西游记");
        takeOrders("西游记");
        takeOrders("西游记");
        takeOrders("西游记");

        for (FlyWeight order : orders) {
            order.sell();
        }

        // 打印生成的订单java对象数量
        System.out.println("\n客户一共买了 " + orders.size() + " 本书! ");

        // 打印生成的订单java对象数量
        System.out.println("共生成了 " + factory.getTotalObjects()
                + " 个 FlyWeight java对象! ");
    }

    private static void takeOrders(String bookName) {
        orders.add(factory.getOrder(bookName));
    }
}
