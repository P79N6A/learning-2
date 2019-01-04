package wang.xiaoluobo.designpattern.prototype105.deep;

import java.io.IOException;

public class TheGreatestSage {
    private Monkey monkey = new Monkey();

    public void change() {
        Monkey copyMonkey = (Monkey) monkey.clone();
        System.out.println("大圣本尊的生日是：" + monkey.getBirthDay());
        System.out.println("克隆大圣的生日是：" + copyMonkey.getBirthDay());
        System.out.println("大圣本尊同克隆大圣是否为同一个对象：" + (monkey == copyMonkey));
        System.out.println("大圣本尊持有的金箍棒 同 克隆大圣持有的金箍棒是否为同一个对象：" + (monkey.getStaff() == copyMonkey.getStaff()));
    }

    public void change1() {
        Monkey copyMonkey = null;
        try {
            copyMonkey = (Monkey) monkey.deepClone();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("大圣本尊的生日是：" + monkey.getBirthDay());
        System.out.println("克隆大圣的生日是：" + copyMonkey.getBirthDay());
        System.out.println("大圣本尊同克隆大圣是否为同一个对象：" + (monkey == copyMonkey));
        System.out.println("大圣本尊持有的金箍棒 同 克隆大圣持有的金箍棒是否为同一个对象：" + (monkey.getStaff() == copyMonkey.getStaff()));
    }

    public static void main(String[] args) {
        TheGreatestSage sage = new TheGreatestSage();
        sage.change();
        System.out.println();

        sage.change1();
    }
}
