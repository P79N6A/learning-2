package wang.xiaoluobo.mybatis;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.internal.DefaultCommentGenerator;
import org.mybatis.generator.internal.rules.Rules;
import org.mybatis.generator.internal.util.StringUtility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

/**
 * http://www.mybatis.org/generator/apidocs/index.html
 *
 * @author WangYandong
 * @email wangyd1005sy@163.com
 * @date 2014年9月6日 下午5:08:50
 */
public class MyCommentGenerator extends DefaultCommentGenerator {

    private Properties properties;
    private boolean suppressAllComments;

    public MyCommentGenerator() {
        this.properties = new Properties();
        this.suppressAllComments = false;
    }

    @Override
    public void addConfigurationProperties(Properties properties) {
        super.addConfigurationProperties(properties);
        this.properties.putAll(properties);
        this.suppressAllComments = StringUtility.isTrue(properties.getProperty("suppressAllComments"));
    }

    /**
     * 生成Java属性注释
     */
    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable,
                                IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            return;
        }

        field.addJavaDocLine("/**");
        StringBuilder sb = new StringBuilder();
        sb.append(" * ");
        String remarks = introspectedColumn.getRemarks();
        if (remarks != null && !"".equals(remarks)) {
            sb.append(remarks + ",");
        }
        sb.append(" Table.Column is ");
        sb.append(introspectedTable.getFullyQualifiedTable());
        sb.append('.');
        sb.append(introspectedColumn.getActualColumnName());
        field.addJavaDocLine(sb.toString());
        field.addJavaDocLine(" */");
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
//		super.addFieldComment(field, introspectedTable);
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
//		super.addClassComment(innerClass, introspectedTable);
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable,
                                boolean markAsDoNotDelete) {
//		super.addClassComment(innerClass, introspectedTable, markAsDoNotDelete);
    }

    @Override
    public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {
//		super.addEnumComment(innerEnum, introspectedTable);
    }

    /**
     * 生成Getter方法注释
     *
     * @param method
     * @param introspectedTable
     * @param introspectedColumn
     */
    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable,
                                 IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            return;
        }

        method.addJavaDocLine("/**");
        StringBuilder sb = new StringBuilder();
        sb.append(" * ");
        String remarks = introspectedColumn.getRemarks();
        if (remarks != null && !"".equals(remarks)) {
            sb.append("获取" + remarks);
        }
        method.addJavaDocLine(sb.toString());
        FullyQualifiedJavaType fullyQualifiedJavaType = method.getReturnType();
        if (fullyQualifiedJavaType != null) {
            String fullyQualifiedName = fullyQualifiedJavaType.getFullyQualifiedName();
            method.addJavaDocLine(" * @return " + fullyQualifiedName);
        }
        method.addJavaDocLine(" */");
    }

    /**
     * 生成Setter方法注释
     *
     * @param method
     * @param introspectedTable
     * @param introspectedColumn
     */
    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable,
                                 IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            return;
        }

        method.addJavaDocLine("/**");
        StringBuilder sb = new StringBuilder();
        sb.append(" * ");
        String remarks = introspectedColumn.getRemarks();
        if (remarks != null && !"".equals(remarks)) {
            sb.append("设置" + remarks);
        }
        method.addJavaDocLine(sb.toString());
        List<Parameter> parameterList = method.getParameters();
        if (parameterList != null && parameterList.size() > 0) {
            for (Parameter param : parameterList) {
                method.addJavaDocLine(" * @param " + param.getName());
            }
        }
        FullyQualifiedJavaType fullyQualifiedJavaType = method.getReturnType();
        if (fullyQualifiedJavaType != null) {
            String fullyQualifiedName = fullyQualifiedJavaType.getFullyQualifiedName();
            method.addJavaDocLine(" * @return " + fullyQualifiedName);
        }
        method.addJavaDocLine(" */");
    }

    /**
     * 生成java方法注释
     *
     * @param method
     * @param introspectedTable
     */
    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }

        // Mapper接口中的方法不添加注释
        String methodName = method.getName();
        java.lang.reflect.Method[] methods = Rules.class.getDeclaredMethods();
        String[] mapperMethodNames = {"selectByExample", "updateByExample", "updateByPrimaryKey"};    // Rules中的方法与生成的Mapper中的方法不一致
        for (int i = 0; i < mapperMethodNames.length; i++) {
            if (mapperMethodNames[i].equals(methodName)) {
                return;
            }
        }
        for (int i = 0; i < methods.length; i++) { // Mapper接口不添加注释
            if (!methods[i].getName().startsWith("generate")) {
                continue;
            }
            String tmpName = methods[i].getName().substring(8, 9).toLowerCase() + methods[i].getName().substring(9);
            if (tmpName.equals(methodName)) {
                return;
            }
        }

        method.addJavaDocLine("/**");
        method.addJavaDocLine(" * ");
        List<Parameter> parameterList = method.getParameters();
        if (parameterList != null && parameterList.size() > 0) {
            for (Parameter param : parameterList) {
                method.addJavaDocLine(" * @param " + param.getName());
            }
        }
        FullyQualifiedJavaType fullyQualifiedJavaType = method.getReturnType();
        if (fullyQualifiedJavaType != null) {
            String fullyQualifiedName = fullyQualifiedJavaType.getFullyQualifiedName();
            method.addJavaDocLine(" * @return " + fullyQualifiedName);
        }
        method.addJavaDocLine(" */");
    }

    @Override
    public void addJavaFileComment(CompilationUnit compilationUnit) {
        if (suppressAllComments) {
            return;
        }

        compilationUnit.addFileCommentLine("/** ");
        compilationUnit.addFileCommentLine(" * " + MergeConstants.NEW_ELEMENT_TAG);
        compilationUnit.addFileCommentLine(" * This file is automatically generated by MyBatis Generator, do not modify!");
//		compilationUnit.addFileCommentLine(" * This file is automatically generated by MyBatis Generator at " + this.getDateString() + ", do not modify!");
        compilationUnit.addFileCommentLine(" * If this file generated again, it will be overwriting!");
        compilationUnit.addFileCommentLine(" */");
    }

    @Override
    public void addComment(XmlElement xmlElement) {
//		super.addComment(xmlElement);
    }

    @Override
    public void addRootComment(XmlElement rootElement) {
        super.addRootComment(rootElement);
    }

    /**
     * 获取时间戳
     *
     * @return
     */
    protected String getDateString() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
