package wang.xiaoluobo.mybatis;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.IntrospectedTableMyBatis3Impl;

import java.util.ArrayList;
import java.util.List;

/**
 * Mybatis Oracle Java动态分页
 *
 * @author WangYandong
 * @email wangyd1005sy@163.com
 * @date 2016年12月11日 上午10:23:16
 */
public class MybatisOraclePaginationPlugin extends PluginAdapter {

    private Document document;

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        addPageParameter(topLevelClass, introspectedTable, "pageIndex");
        addPageParameter(topLevelClass, introspectedTable, "pageSize");
        return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
    }

    private void addPageParameter(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String name) {
        CommentGenerator commentGenerator = context.getCommentGenerator();
        Field field = new Field();
        field.setVisibility(JavaVisibility.PROTECTED);
        field.setType(new FullyQualifiedJavaType(Integer.class.getName()));
        field.setName(name);
        field.setInitializationString("-1");
        commentGenerator.addFieldComment(field, introspectedTable);
        topLevelClass.addField(field);

        char c = name.charAt(0);
        String camel = Character.toUpperCase(c) + name.substring(1);
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("set" + camel);
        method.addParameter(new Parameter(FullyQualifiedJavaType.getIntInstance(), name));
        method.addBodyLine("this." + name + "=" + name + ";");
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);

        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setName("get" + camel);
        method.addBodyLine("return " + name + ";");
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
    }

    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        XmlElement pageIndex = new XmlElement("include"); //$NON-NLS-1$
        pageIndex.addAttribute(new Attribute("refid", "pagePrefix"));
        element.getElements().add(0, pageIndex);

        XmlElement isNotNullElement = new XmlElement("include"); //$NON-NLS-1$
        isNotNullElement.addAttribute(new Attribute("refid", "pageSuffix"));
        element.getElements().add(isNotNullElement);
        return super.sqlMapSelectByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        this.document = document;

        XmlElement parentElement = document.getRootElement();

        XmlElement pagePrefixElement = new XmlElement("sql");
        pagePrefixElement.addAttribute(new Attribute("id", "pagePrefix"));
        XmlElement pageIndex = new XmlElement("if");
        pageIndex.addAttribute(new Attribute("test", "pageIndex != -1 and pageSize != -1"));
        pageIndex.addElement(new TextElement(" SELECT * FROM (SELECT tmp_tb.*, ROWNUM row_id FROM ( "));
        pagePrefixElement.addElement(pageIndex);
        parentElement.addElement(pagePrefixElement);

        XmlElement paginationSuffixElement = new XmlElement("sql");
        paginationSuffixElement.addAttribute(new Attribute("id", "pageSuffix"));
        XmlElement pageEnd = new XmlElement("if");
        pageEnd.addAttribute(new Attribute("test", "pageIndex != -1 and pageSize != -1"));
        pageEnd.addElement(new TextElement("<![CDATA[ ) tmp_tb WHERE ROWNUM <= #{pageIndex} + #{pageSize} ) WHERE row_id > ${pageIndex} ]]>"));
        paginationSuffixElement.addElement(pageEnd);
        parentElement.addElement(paginationSuffixElement);

        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * @param introspectedTable
     * @return
     * @see IntrospectedTableMyBatis3Impl#getGeneratedXmlFiles()
     */
    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(IntrospectedTable introspectedTable) {
        List<GeneratedXmlFile> generatedXmlFileList = new ArrayList<GeneratedXmlFile>();
        GeneratedXmlFile gxf = new GeneratedXmlFile(this.document, introspectedTable.getMyBatis3XmlMapperFileName(), introspectedTable.getMyBatis3XmlMapperPackage(), this.context.getSqlMapGeneratorConfiguration().getTargetProject(), false, this.context.getXmlFormatter());
        if (this.context.getPlugins().sqlMapGenerated(gxf, introspectedTable)) {
            generatedXmlFileList.add(gxf);
        }
        return generatedXmlFileList;
    }
}
