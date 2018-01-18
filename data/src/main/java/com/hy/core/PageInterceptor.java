package com.hy.core;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/8/31.
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class PageInterceptor implements Interceptor {
    private Logger logger = LogManager.getLogger(PageInterceptor.class);
    private Pattern tablePattern = Pattern.compile("(?<=\\.)(\\w+)", Pattern.CASE_INSENSITIVE);
    private Pattern tablePattern2 = Pattern.compile("(?<=\\.)(\\w+)(\\s+([a-zA-Z]+)(?=,)?)", Pattern.CASE_INSENSITIVE);
    private String tPatternStr = "(\\w+\\.)?(?<!\\[)\\*";
    private Pattern tPattern = Pattern.compile(tPatternStr, Pattern.CASE_INSENSITIVE);
    /*    @Autowired
    private RedisCacheManager cacheManager;    //TODO:读取系统配置*/
    private String dialect; //数据库类型
    //    private String cacheSql; //缓存SQL
    private String pageSql;
    private String upSql;
    private String encryptSql;
    private String mulitEncryptSql;
//    private @Value("#{config['daoPackage']}") String daoPackage;

  /*  @Autowired
    public PageInterceptor(SqlSessionFactory sqlSessionFactory) {
        super(sqlSessionFactory);
    }*/

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        RoutingStatementHandler handler = (RoutingStatementHandler) invocation.getTarget();
        BaseStatementHandler delegate = (BaseStatementHandler) Constants.ReflectUtil.getFieldValue(handler, "delegate");
        MappedStatement mappedStatement = (MappedStatement) Constants.ReflectUtil.getFieldValue(delegate, "mappedStatement");
        BoundSql boundSql = delegate.getBoundSql();
        String sId = mappedStatement.getId();
        String sqlId = sId.substring(sId.lastIndexOf(".") + 1);
        logger.info(boundSql.getSql());
        logger.info(DataSourceHolder.getLocalDataSource());
        if (sqlId.matches(upSql)) {
            Object obj = boundSql.getParameterObject();
            Integer size = (Integer) ((Map) obj).get("size");
            if (size == null || size == 0)
                updateProcess(boundSql);
            else {
                updateProcess(boundSql, size);
            }
//            return invocation.proceed();
        } else if (sqlId.matches(encryptSql)) {
            encryptProcess(boundSql);
/*            if (sqlId.matches(pageSql)) {
                pageProcess(invocation, delegate, boundSql);
            }*/
//            return invocation.proceed();
        } else if (sqlId.matches(mulitEncryptSql)) {
            encryptPro(boundSql);
//            return invocation.proceed();
        }
        if (sqlId.matches(pageSql)) {
            pageProcess(invocation, delegate, boundSql);
        }
        return invocation.proceed();
    }

    public void pageProcess(final Invocation invocation, final BaseStatementHandler delegate, final BoundSql boundSql) {
        Object obj = boundSql.getParameterObject();
        boolean isPage = obj instanceof Map && ((Map) obj).containsKey("page");
        if (isPage) {
            Page<?> page = (Page<?>) ((Map) obj).get("page");
//            fillParams(page, boundSql);
            MappedStatement mappedStatement = (MappedStatement) Constants.ReflectUtil.getFieldValue(delegate, "mappedStatement");
            Connection connection = (Connection) invocation.getArgs()[0];
            //获取当前要执行的sql语句
            String sql = boundSql.getSql();
            this.setTotalRecord((Map) obj, mappedStatement, connection, sql);
            String pageSql = this.getPageSql(page, sql);
            Constants.ReflectUtil.setFieldValue(boundSql, "sql", pageSql);
//            Constants.ReflectUtil.setFieldValue(boundSql, "parameterObject", page.getParams());
        }
    }

    public void encryptProcess(final BoundSql boundSql) {
        String sql = boundSql.getSql();
        Matcher matcher = tablePattern.matcher(sql);
        int index = sql.indexOf("from", 9);
        if (index < 0) index = sql.indexOf("FROM", 9);
        if (matcher.find(index)) {
            String tableName = matcher.group(1);
            Matcher matcher1 = tPattern.matcher(sql);
            if (matcher1.find(6)) {
                String replaceStr = Constants.getCacheStringValue("columns", tableName);
                String prefix = matcher1.group(1);
                if (prefix == null)
                    sql = sql.replaceFirst(tPatternStr, replaceStr);
                else
                    sql = sql.replaceFirst(tPatternStr, processStr(replaceStr, prefix));
            }
        }
        Constants.ReflectUtil.setFieldValue(boundSql, "sql", sql);
    }

    public void encryptPro(final BoundSql boundSql) {
        String sql = boundSql.getSql();
        String pSql = new String(sql);
        Matcher matcher1 = tPattern.matcher(sql);
        int index = sql.indexOf("from", 15);
        if (index < 0) index = sql.indexOf("FROM", 15);
        a:
        while (matcher1.find()) {
            String prefix = matcher1.group(1);
            Matcher matcher = tablePattern2.matcher(sql.substring(index));
            b:
            while (matcher.find()) {
                String tableName = matcher.group(1);
                String tableAlias = matcher.group(3);
                String replaceStr = Constants.getCacheStringValue("columns", tableName);
                if (prefix == null) {
                    pSql = pSql.replaceFirst(tPatternStr, replaceStr);
                    break a;
                } else if (prefix.substring(0, prefix.length() - 1).equals(tableAlias)) {
                    pSql = pSql.replaceFirst(tPatternStr, processS(replaceStr, prefix, "_" + tableAlias));
                    break b;
                }
            }
        }
//        logger.info(pSql);
        Constants.ReflectUtil.setFieldValue(boundSql, "sql", pSql);
    }

    public void updateProcess(final BoundSql boundSql) {
        String sql = boundSql.getSql();
        int suffix = sql.lastIndexOf(",");
        if (suffix > 0)
            Constants.ReflectUtil.setFieldValue(boundSql, "sql", sql.substring(0, suffix) + " where " + sql.substring(suffix + 1));
    }

    public void updateProcess(final BoundSql boundSql, int size) {
        String sql = boundSql.getSql();
        String inte = ",";
        String[] sqlArr = sql.split(inte);
        Assert.isTrue(size < sqlArr.length, "SQL UPDATE cond size error:" + sql);
        StringBuffer sqlSB = new StringBuffer();
        int tok = sqlArr.length - size - 1;
        for (int i = 0; i < sqlArr.length; i++) {
            if (i < tok)
                sqlSB.append(sqlArr[i]).append(inte);
            else if (i == tok) {
                sqlSB.append(sqlArr[i]).append(" where ");
            } else if (i == sqlArr.length - 1) {
                sqlSB.append(sqlArr[i]);
            } else {
                sqlSB.append(sqlArr[i]).append(" and ");
            }
        }
        Constants.ReflectUtil.setFieldValue(boundSql, "sql", sqlSB.toString());
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {
        this.dialect = properties.getProperty("dialect");
        this.pageSql = properties.getProperty("pageSql");
        this.upSql = properties.getProperty("upSql");
        this.encryptSql = properties.getProperty("encryptSql");
        this.mulitEncryptSql = properties.getProperty("mulitEncryptSql");
    }

    private void setTotalRecord(Map<String, Object> paramsObj, MappedStatement mappedStatement, Connection connection, String eSql) {
        BoundSql boundSql = mappedStatement.getBoundSql(paramsObj);
//        String sql = boundSql.getSql();
        String countSql = "select count(1) from (" + eSql + ") as total";
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
//        BoundSql countBoundSql = new BoundSql(mappedStatement.getConfiguration(), countSql, parameterMappings, paramsObj.get("page"));
        BoundSql countBoundSql = new BoundSql(mappedStatement.getConfiguration(), countSql, parameterMappings, paramsObj);
        parameterMappings.forEach((mapping) -> {
            String prop = mapping.getProperty();
            if (boundSql.hasAdditionalParameter(prop))
                countBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
        });
        ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, paramsObj, countBoundSql);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement(countSql);
            parameterHandler.setParameters(pstmt);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int totalRecord = rs.getInt(1);
                Page<?> page = (Page<?>) ((Map) paramsObj).get("page");
                int pageSize = page.getPageSize();
                if (pageSize == 0) {
                    Integer size = Constants.getCacheValue("system", "pageSize", Integer.class);
                    pageSize = size == null ? 30 : size;
                }
                if (page.getPageSize() == 0) page.setPageSize(pageSize);
                page.setTotalRecord(totalRecord);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 根据page对象获取对应的分页查询Sql语句，这里只做了两种数据库类型，Mysql和Oracle
     * 其它的数据库都 没有进行分页
     *
     * @param page 分页对象
     * @param sql  原sql语句
     * @return
     */
    private String getPageSql(Page<?> page, String sql) {
        StringBuffer sqlBuffer = new StringBuffer(sql);
        if ("mysql".equalsIgnoreCase(dialect)) {
            return getMysqlPageSql(page, sqlBuffer);
        } else if ("oracle".equalsIgnoreCase(dialect)) {
            return getOraclePageSql(page, sqlBuffer);
        }
        return sqlBuffer.toString();
    }

    /**
     * 获取Mysql数据库的分页查询语句
     *
     * @param page      分页对象
     * @param sqlBuffer 包含原sql语句的StringBuffer对象
     * @return Mysql数据库分页语句
     */
    private String getMysqlPageSql(Page<?> page, StringBuffer sqlBuffer) {
        //计算第一条记录的位置，Mysql中记录的位置是从0开始的。
        int offset = (page.getPageNo() - 1) * page.getPageSize();
        sqlBuffer.append(" limit ").append(offset).append(",").append(page.getPageSize());
        return sqlBuffer.toString();
    }

    /**
     * 获取Oracle数据库的分页查询语句
     *
     * @param page      分页对象
     * @param sqlBuffer 包含原sql语句的StringBuffer对象
     * @return Oracle数据库的分页查询语句
     */
    private String getOraclePageSql(Page<?> page, StringBuffer sqlBuffer) {
        //计算第一条记录的位置，Oracle分页是通过rownum进行的，而rownum是从1开始的
        int offset = (page.getPageNo() - 1) * page.getPageSize() + 1;
        sqlBuffer.insert(0, "select u.*, rownum r from (").append(") u where rownum < ").append(offset + page.getPageSize());
        sqlBuffer.insert(0, "select * from (").append(") where r >= ").append(offset);
        //上面的Sql语句拼接之后大概是这个样子：
        //select * from (select u.*, rownum r from (select * from t_user) u where rownum < 31) where r >= 16
        return sqlBuffer.toString();
    }

    private void fillParams(Page page, BoundSql boundSql) {
        Object obj = page.getParams();
        StringBuffer sqlSB = new StringBuffer(boundSql.getSql() + " where 1=1 ");
        Consumer<Field> fillSql = field -> {
            field.setAccessible(true);
            try {
                Object val = field.get(obj);
                if (!StringUtils.isEmpty(val))
                    sqlSB.append(" and " + field.getName() + "=" + String.valueOf(val));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        };
        Arrays.<Field>asList(obj.getClass().getDeclaredFields()).forEach(fillSql);
        String sql = sqlSB.toString();
//        if(sql.endsWith("and"))
//            sql = sql.substring(0,sql.length() - 3);
        Constants.ReflectUtil.setFieldValue(boundSql, "sql", sql);
    }

    public String processStr(String str, String prefix) {
        return prefix + str.replace(Table.SEPARATE_SPLIT, Table.SEPARATE_SPLIT + prefix);
    }

    public String processS(String str, String prefix, String suffix) {
//        return prefix + str.replace(Table.SEPARATE_SPLIT, suffix + Table.SEPARATE_SPLIT + prefix) + suffix;
//        return prefix + str.replaceAll("(\\w+),", prefix.replace(".","_")+"$1," + prefix);
        return str.replaceAll("([A-Z_]+)\\s(\\w+)", prefix + "$1 " + prefix.replace(".", "_") + "$2");
    }

}
