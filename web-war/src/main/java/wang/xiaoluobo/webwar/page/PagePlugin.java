package wang.xiaoluobo.webwar.page;

import org.apache.ibatis.builder.xml.dynamic.ForEachSqlNode;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import javax.xml.bind.PropertyException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * 分页处理拦截器
 * @author: WangYandong
 * @date: 2013-8-4 上午9:27:39
 * @version: v1.0
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})})
public class PagePlugin implements Interceptor {

    private static String dialect = "";
    private static String pageSqlId = "";

    public Object intercept(Invocation ivk) throws Throwable {
        if (ivk.getTarget() instanceof RoutingStatementHandler) {
            RoutingStatementHandler statementHandler = (RoutingStatementHandler) ivk.getTarget();
            BaseStatementHandler delegate = (BaseStatementHandler) ReflectHelper.getValueByFieldName(statementHandler, "delegate");
            MappedStatement mappedStatement = (MappedStatement) ReflectHelper.getValueByFieldName(delegate, "mappedStatement");

            if (mappedStatement.getId().matches(pageSqlId)) {
                BoundSql boundSql = delegate.getBoundSql();
                Object parameterObject = boundSql.getParameterObject();

                if (parameterObject == null) {
                    throw new NullPointerException("parameterObject尚未实例化！");
                } else {
                    Connection connection = (Connection) ivk.getArgs()[0];
                    String sql = boundSql.getSql();
                    String countSql = " SELECT COUNT(*) FROM ( " + sql + " ) tmp_count ";
                    PreparedStatement countStmt = connection.prepareStatement(countSql);
                    BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(), countSql, boundSql.getParameterMappings(), parameterObject);
                    setParameters(countStmt, mappedStatement, countBS, parameterObject);
                    ResultSet rs = countStmt.executeQuery();

                    int count = 0;
                    if (rs.next()) {
                        count = rs.getInt(1);
                    }
                    rs.close();
                    countStmt.close();

                    Page page = null;
                    if (parameterObject instanceof Page) {
                        page = (Page) parameterObject;
                        page.setEntityOrField(true);
                        page.setTotalCounts(count);
                    } else {
                        Field pageField = ReflectHelper.getFieldByFieldName(parameterObject, "page");
                        if (pageField != null) {
                            page = (Page) ReflectHelper.getValueByFieldName(parameterObject, "page");
                            if (page == null)
                                page = new Page();
                            page.setEntityOrField(false);
                            page.setTotalCounts(count);
                            ReflectHelper.setValueByFieldName(parameterObject, "page", page);
                        } else {
                            throw new NoSuchFieldException(parameterObject.getClass().getName() + "不存在 page属性！");
                        }
                    }

                    String pageSql = generatePageSql(sql, page);
                    ReflectHelper.setValueByFieldName(boundSql, "sql", pageSql);
                }
            }
        }
        return ivk.proceed();
    }

    /**
     * @Title: setParameters
     * @Description: 为SQL赋值
     * @param ps
     * @param mappedStatement
     * @param boundSql
     * @param parameterObject
     * @throws SQLException
     * @return: void
     * @throws:
     * @author: WangYandong
     * @date: 2013-8-4 下午12:22:53
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql, Object parameterObject) throws SQLException {
        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();

        if (parameterMappings != null) {
            Configuration configuration = mappedStatement.getConfiguration();
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);

            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);

                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    PropertyTokenizer prop = new PropertyTokenizer(propertyName);

                    if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX) && boundSql.hasAdditionalParameter(prop.getName())) {
                        value = boundSql.getAdditionalParameter(prop.getName());
                        if (value != null) {
                            value = configuration.newMetaObject(value).getValue(propertyName.substring(prop.getName().length()));
                        }
                    } else {
                        value = metaObject == null ? null : metaObject.getValue(propertyName);
                    }

                    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    if (typeHandler == null) {
                        throw new ExecutorException("There was no TypeHandler found for parameter " + propertyName + " of statement " + mappedStatement.getId());
                    }

                    typeHandler.setParameter(ps, i + 1, value, parameterMapping.getJdbcType());
                }
            }
        }
    }

    /**
     * @Title: generatePageSql
     * @Description: 根据SQL方言生成特定的分页SQL
     * @param sql
     * @param page
     * @return
     * @return: String
     * @throws:
     * @author: WangYandong
     * @date: 2013-8-4 下午12:23:23
     */
    private String generatePageSql(String sql, Page page) {
        if (page != null && dialect != null && dialect != "") {
            StringBuffer pageSql = new StringBuffer();

            if ("mysql".equalsIgnoreCase(dialect)) {
                pageSql.append(sql);
                pageSql.append(" LIMIT " + page.getPageIndex() + "," + page.getPageSize());
            } else if ("oracle".equalsIgnoreCase(dialect)) {
                pageSql.append(" SELECT * FROM (SELECT tmp_tb.*, ROWNUM row_id FROM ( ");
                pageSql.append(sql);
                pageSql.append(" ) tmp_tb WHERE ROWNUM <= ");
                pageSql.append(page.getPageIndex() + page.getPageSize());
                pageSql.append(" ) WHERE row_id > ");
                pageSql.append(page.getPageIndex());
            }

            return pageSql.toString();
        } else {
            return sql;
        }
    }

    public Object plugin(Object arg0) {
        return Plugin.wrap(arg0, this);
    }

    /**
     * @Title: setProperties
     * @Description: 设置数据库方言
     * @param p
     * @author: WangYandong
     * @date: 2013-8-4 下午12:24:45
     * @see Interceptor#setProperties(Properties)
     */
    public void setProperties(Properties p) {
        dialect = p.getProperty("dialect");

        if (dialect == null || dialect == "") {
            try {
                throw new PropertyException("dialect property is not found!");
            } catch (PropertyException e) {
                e.printStackTrace();
            }
        }

        pageSqlId = p.getProperty("pageSqlId");
        if (pageSqlId == null || pageSqlId == "") {
            try {
                throw new PropertyException("pageSqlId property is not found!");
            } catch (PropertyException e) {
                e.printStackTrace();
            }
        }
    }
}
