package wang.xiaoluobo.designpattern.prototype105.deep;

import java.io.*;
import java.util.Date;

/**
 * 猕猴类，大圣本尊由猕猴类来表示
 */
public class Monkey implements Cloneable, Serializable {
    /**
     * 身高
     */
    private int height;
    /**
     * 体重
     */
    private int weight;
    /**
     * 出生日期
     */
    private Date birthDay;
    /**
     * 金箍棒
     */
    private GoldRingedStaff staff;

    /**
     * 构造函数，指定创建事件和给定金箍棒
     */
    public Monkey() {
        this.birthDay = new Date();
        this.staff = new GoldRingedStaff();
    }

    /**
     * 克隆方法，直接调用接口的克隆方法
     */
    public Object clone() {
        Monkey temp = null;
        try {
            temp = (Monkey) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        } finally {
            return temp;
        }
    }

    public Object deepClone() throws IOException, ClassNotFoundException {
        //将对象写入流中
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(this);

        //将对象从流中读取回来
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        return objectInputStream.readObject();
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    public GoldRingedStaff getStaff() {
        return staff;
    }

    public void setStaff(GoldRingedStaff staff) {
        this.staff = staff;
    }
}