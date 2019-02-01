package wang.xiaoluobo.mybatis;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;
import java.util.Objects;

/**
 * Example序列化
 *
 * @author WangYandong
 * @email wangyd1005sy@163.com
 * @date 2014年9月6日 下午4:00:03
 * @see org.mybatis.generator.plugins.SerializablePlugin
 */
public class ExampleSerializablePlugin extends PluginAdapter {

    private FullyQualifiedJavaType serializable;

    public ExampleSerializablePlugin() {
        serializable = new FullyQualifiedJavaType("java.io.Serializable");
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType(serializable);
        this.makeSerializable(topLevelClass, introspectedTable);
        List<InnerClass> innerClassList = topLevelClass.getInnerClasses();

        for (InnerClass innerClass : innerClassList) {
            if (Objects.equals(innerClass.getType().getFullyQualifiedName(), "GeneratedCriteria")) {
                this.makeSerializable(innerClass, introspectedTable);
            }
            if (Objects.equals(innerClass.getType().getFullyQualifiedName(), "Criterion")) {
                this.makeSerializable(innerClass, introspectedTable);
            }
        }

        return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
    }

    protected void makeSerializable(InnerClass innerClass, IntrospectedTable introspectedTable) {
        innerClass.addSuperInterface(serializable);
        Field field = new Field();
        field.setFinal(true);
        field.setInitializationString("1L");
        field.setName("serialVersionUID");
        field.setStatic(true);
        field.setType(new FullyQualifiedJavaType("long"));
        field.setVisibility(JavaVisibility.PRIVATE);
        context.getCommentGenerator().addFieldComment(field, introspectedTable);
        innerClass.addField(field);
    }

}
