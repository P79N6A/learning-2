package wang.xiaoluobo.designpattern.composite211;

import java.util.ArrayList;
import java.util.List;

public class Composite implements Component {

    /**
     * 用来存储组合对象中包含的子组件对象
     */
    private List<Component> childComponents = new ArrayList<>();

    /**
     * 组合对象的名称
     */
    private String name;

    /**
     * 构造方法，传入组合对象的名称
     *
     * @param name 组合对象的名称
     */
    public Composite(String name) {
        this.name = name;
    }

    /**
     * 聚集管理方法，增加一个子构建对象
     *
     * @param child 子构建对象
     */
    public void addChild(Component child) {
        this.childComponents.add(child);
    }

    /**
     * 聚集管理方法，删除一个子构建对象
     *
     * @param index 子构建对象的下标
     */
    public void removeChild(int index) {
        this.childComponents.remove(index);
    }

    /**
     * 聚集管理方法，返回所有子构建对象
     *
     * @return 子构建对象列表
     */
    public List<Component> getChild() {
        return this.childComponents;
    }

    /**
     * 输出对象的自身结构
     *
     * @param preStr 前缀，主要是按照层级拼装空格，实现向后缩进
     */
    @Override
    public void printStruct(String preStr) {
        //首先输出自身
        System.out.println(preStr + '+' + this.name);

        //如果还包含有子组件，那么就输出这些子组件对象
        if (this.childComponents != null) {
            //添加前缀空格，表示向后缩进
            preStr += "  ";

            //循环递归输出每个子对象
            for (Component component : childComponents) {
                component.printStruct(preStr);
            }
        }
    }
}
