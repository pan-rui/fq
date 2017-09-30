package com.hy.core;

import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;

import java.sql.Connection;
import java.util.Properties;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-11-29 11:02)
 * @version: \$Rev: 2895 $
 * @UpdateAuthor: \$Author: panrui $
 * @UpdateDateTime: \$Date: 2017-06-07 17:42:18 +0800 (周三, 07 6月 2017) $
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class UpdateIntercept implements Interceptor {

    private String dialect;
    private String sqlId;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        RoutingStatementHandler handler = (RoutingStatementHandler) invocation.getTarget();
        BaseStatementHandler delegate = (BaseStatementHandler) Constants.ReflectUtil.getFieldValue(handler, "delegate");
        MappedStatement mappedStatement = (MappedStatement) Constants.ReflectUtil.getFieldValue(delegate, "mappedStatement");
        BoundSql boundSql = delegate.getBoundSql();
        String sId = mappedStatement.getId();
        if (sId.substring(sId.lastIndexOf(".") + 1).matches(sqlId)) {
            String sql = boundSql.getSql();
            int suffix = sql.lastIndexOf(",");
            System.out.println(sql);
            if (suffix > 0)
                Constants.ReflectUtil.setFieldValue(boundSql, "sql", sql.substring(0, suffix) + " where " + sql.substring(suffix + 1));
//            ReflectUtil.setFieldValue(boundSql, "sql", sql.substring(0,prefix+4)+sql.substring(suffix+1)+" where "+sql.substring(prefix+4,suffix));
//            BoundSql updateBoundSql = new BoundSql(mappedStatement.getConfiguration(), sql.substring(0,prefix+4)+sql.substring(suffix+1)+" where "+sql.substring(prefix+4,suffix), boundSql.getParameterMappings(), ((Map)boundSql.getParameterObject()).get("params"));
//            ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), updateBoundSql);
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {
        this.dialect = properties.getProperty("dialect");
        this.sqlId = properties.getProperty("sqlId");
    }
}
