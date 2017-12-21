package com.hy.dao;

import com.hy.core.DataSourceHolder;
import com.hy.core.Page;
import com.hy.core.ParamsMap;
import com.hy.core.DataSource;
import com.hy.core.MapResultHandler;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * Description: 基本Dao 操作定义,,,第一个参数为表名,第二个参数为传入的条件(Map),顺序不可逆
 * Author: 潘锐 (2017-03-28 14:35)
 * version: \$Rev: 3902 $
 * UpdateAuthor: \$Author: zhangj $
 * UpdateDateTime: \$Date: 2017-08-02 14:43:05 +0800 (周三, 02 8月 2017) $
 */
@Repository
@CacheConfig(cacheNames = "tmp", cacheResolver = "baseImpl")
public class BaseDao {
    private SqlSessionTemplate sqlSessionTemplate;
    private String className;

    @Autowired
    public BaseDao(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
        this.className = this.getClass().getName();
    }

    public Connection getConnection() {
        return sqlSessionTemplate.getConnection();
    }

    public SqlSessionTemplate getSqlSessionTemplate() {
        return sqlSessionTemplate;
    }

    @DataSource
    public List<Map<String, Object>> queryListInTab(String tableName, Map<String, Object> params, Map<String, Object> orderMap) {
        Map<String, Object> paramsMap = ParamsMap.newMap("tableName", tableName).addParams("params", params).addParams("orderMap", orderMap);
        return sqlSessionTemplate.selectList(className + ".queryListInTab", paramsMap);
    }

    @DataSource
    public List<Map<String, Object>> queryListInT(String tableName, Map<String, Object> params, Map<String, Object> orderMap) {
        Map<String, Object> paramsMap = ParamsMap.newMap("tableName", tableName).addParams("params", params).addParams("orderMap", orderMap);
        return sqlSessionTemplate.selectList(className + ".queryListInT", paramsMap);
    }

    //limit 条数限制
    @DataSource
    public List<Map<String, Object>> queryListSufInTab(String tableName, Map<String, Object> params, Map<String, Object> orderMap,String sufSql) {
        Map<String, Object> paramsMap = ParamsMap.newMap("tableName", tableName).addParams("params", params).addParams("orderMap", orderMap).addParams("sufSql",sufSql);
        return sqlSessionTemplate.selectList(className + ".queryListSufInTab", paramsMap);
    }
    //    @Cacheable(keyGenerator = "myKeyGenerator")
    @DataSource
    public List<Map<String,Object>> queryByProsInTab(String tableName, Map<String, Object> params) {
        Map<String, Object> paramsMap = ParamsMap.newMap("tableName", tableName).addParams("params", params);
        return sqlSessionTemplate.selectList(className + ".queryByProsInTab", paramsMap);
    }

    @DataSource
    public List<Map<String,Object>> queryByProsMulTab(String tableName, Map<String, Object> params) {
        Map<String, Object> paramsMap = ParamsMap.newMap("tableName", tableName).addParams("params", params);
        return sqlSessionTemplate.selectList(className + ".queryByProsMulTab", paramsMap);
    }

    @DataSource
    public List<Map<String, Object>> queryByProsInT(String tableName, Map<String, Object> params) {
        Map<String, Object> paramsMap = ParamsMap.newMap("tableName", tableName).addParams("params", params);
        return sqlSessionTemplate.selectList(className + ".queryByProsInT", paramsMap);
    }

//    @Cacheable(key = "(#tableName).replaceFirst('\\.','-')+'$'+#id")
//@Cacheable(key = "T(java.lang.String).valueOf(#p0).replaceFirst('\\.','-')")
@DataSource
    @Cacheable(key = "#tableName+'$ID-'+#id",condition = "#result != null")
    public Map<String, Object> queryByIdInTab(String tableName, Object id) {
        Map<String, Object> paramsMap = ParamsMap.newMap("tableName", tableName).addParams("id", id);
        return sqlSessionTemplate.selectOne(className + ".queryByIdInTab", paramsMap);
    }

    @DataSource
    @Cacheable(key = "#tableName",condition = "#result != null")
    public List<Map<String, Object>> queryAllInTab(String tableName) {
        Map<String, Object> paramsMap = ParamsMap.newMap("tableName", tableName);
        return sqlSessionTemplate.selectList(className + ".queryAllInTab", paramsMap);
    }

/*    @DataSource
    public List<Map<String, Object>> queryAllOnSortInTab(String tableName, Map<String, Object> orderMap) {
        Map<String, Object> paramsMap = ParamsMap.newMap("tableName", tableName).addParams("orderMap", orderMap);
        return sqlSessionTemplate.selectList(className + ".queryAllOnSortInTab", paramsMap);
    }*/

    @Caching(evict = {@CacheEvict(keyGenerator = "myKeyGenerator", cacheManager = "cacheManager"),@CacheEvict(key = "#tableName", cacheManager = "cacheManager")})
    @DataSource(DataSourceHolder.DBType.master)
    public int updateByProsInTab(String tableName, Map<String, Object> params) {
        Map<String, Object> paramsMap = ParamsMap.newMap("tableName", tableName).addParams("params", params);
        return sqlSessionTemplate.update(className + ".updateByProsInTab", paramsMap);
    }

    @DataSource(DataSourceHolder.DBType.master)
    public int updateByMapInTab(String tableName, Map<String, Object> params,Map<String,Object> condition) {
        Map<String, Object> paramsMap = ParamsMap.newMap("tableName", tableName).addParams("params", params).addParams("condition",condition);
        return sqlSessionTemplate.update(className + ".updateByMapInTab", paramsMap);
    }

    @DataSource(DataSourceHolder.DBType.master)
    public int updateByProsInTab(String tableName, Map<String, Object> params,Integer size) {
        Map<String, Object> paramsMap = ParamsMap.newMap("tableName", tableName).addParams("params", params).addParams("size",size);
        return sqlSessionTemplate.update(className + ".updateByProsInTab", paramsMap);
    }

    @Caching(evict = {@CacheEvict(keyGenerator = "myKeyGenerator", cacheManager = "cacheManager"),@CacheEvict(key = "#tableName", cacheManager = "cacheManager")})
    @DataSource(DataSourceHolder.DBType.master)
    public int deleteByProsInTab(String tableName, Map<String, Object> params) {
        Map<String, Object> paramsMap = ParamsMap.newMap("tableName", tableName).addParams("params", params);
        return sqlSessionTemplate.delete(className + ".deleteByProsInTab", paramsMap);
    }

    @CacheEvict(key = "#tableName", cacheManager = "cacheManager")
    @DataSource(DataSourceHolder.DBType.master)
    public int insertByProsInTab(String tableName, Map<String, Object> params) {
        Map<String, Object> paramsMap = ParamsMap.newMap("tableName", tableName).addParams("params", params);
        return sqlSessionTemplate.insert(className + ".insertByProsInTab", paramsMap);
    }

    @CacheEvict(key = "#tableName", cacheManager = "cacheManager")
    @DataSource(DataSourceHolder.DBType.master)
    public int insertUpdateByProsInTab(String tableName, Map<String, Object> params) {
        Map<String, Object> paramsMap = ParamsMap.newMap("tableName", tableName).addParams("params", params);
        return sqlSessionTemplate.insert(className + ".insertUpdateByProsInTab", paramsMap);
    }
    @CacheEvict(key = "#tableName", cacheManager = "cacheManager")
    @DataSource(DataSourceHolder.DBType.master)
    public int insertBatchByProsInTab(String tableName, List<Map<String, Object>> dataList) {
        Map<String, Object> paramsMap = ParamsMap.newMap("tableName", tableName).addParams("dataList", dataList);
        return sqlSessionTemplate.insert(className + ".insertBatchByProsInTab", paramsMap);
    }

@CacheEvict(key = "#tableName", cacheManager = "cacheManager")
@DataSource(DataSourceHolder.DBType.master)
public int insertIgnoreBatchByProsInTab(String tableName, List<Map<String, Object>> dataList) {
    Map<String, Object> paramsMap = ParamsMap.newMap("tableName", tableName).addParams("dataList", dataList);
    return sqlSessionTemplate.insert(className + ".insertIgnoreBatchByProsInTab", paramsMap);
}
    @CacheEvict(key = "#tableName", cacheManager = "cacheManager")
    @DataSource(DataSourceHolder.DBType.master)
    public int insertIgnoreByProsInTab(String tableName, Map<String, Object> params) {
        Map<String, Object> paramsMap = ParamsMap.newMap("tableName", tableName).addParams("params", params);
        return sqlSessionTemplate.insert(className + ".insertByProsInTab", paramsMap);
    }
    @CacheEvict(key = "#tableName", cacheManager = "cacheManager")
    @DataSource(DataSourceHolder.DBType.master)
    public int insertUpdateBatchByProsInTab(String tableName, List<Map<String, Object>> dataList) {
        Map<String, Object> paramsMap = ParamsMap.newMap("tableName", tableName).addParams("dataList", dataList);
        return sqlSessionTemplate.insert(className + ".insertUpdateBatchByProsInTab", paramsMap);
    }

/*    @DataSource
    public List<Map<String, Object>> queryPageLikeInTab(String tableName, final Page page) {
        Map<String, Object> paramsMap = ParamsMap.newMap("tableName", tableName).addParams("page", page);
        List<Map<String, Object>> resultList = sqlSessionTemplate.selectList(className + ".queryPageLikeInTab", paramsMap);
        page.setResults(resultList);
        return resultList;
    }*/

    @DataSource
    public List<Map<String, Object>> queryPageInTab(String tableName, final Page page) {
        Map<String, Object> paramsMap = ParamsMap.newMap("tableName", tableName).addParams("page", page);
        List<Map<String, Object>> resultList = sqlSessionTemplate.selectList(className + ".queryPageInTab", paramsMap);
        page.setResults(resultList);
        return resultList;
    }

    @DataSource
    public List<Map<String, Object>> queryPageMulTab(String tableName, final Page page) {
        Map<String, Object> paramsMap = ParamsMap.newMap("tableName", tableName).addParams("page", page);
        List<Map<String, Object>> resultList = sqlSessionTemplate.selectList(className + ".queryPageMulTab", paramsMap);
        page.setResults(resultList);
        return resultList;
    }

    @DataSource
    public List<Map<String, Object>> queryPageLikeTab(String tableName, final Page page) {
        Map<String, Object> paramsMap = ParamsMap.newMap("tableName", tableName).addParams("page", page);
        List<Map<String, Object>> resultList = sqlSessionTemplate.selectList(className + ".queryPageLikeTab", paramsMap);
        page.setResults(resultList);
        return resultList;
    }
    @DataSource
    public List<Map<String, Object>> queryBySql(String dynSql) {
        MapResultHandler mapResultHandler = new MapResultHandler();
        sqlSessionTemplate.select(className + ".queryBySql", ParamsMap.newMap("dynSql", dynSql), mapResultHandler);
        return mapResultHandler.getMappedResults();
    }

    @DataSource
    public List<Map<String, Object>> queryByS(String dynSql) {
        return sqlSessionTemplate.selectList(className + ".queryByS", ParamsMap.newMap("dynSql", dynSql));
    }

    @DataSource
    public Map<String, Object> queryBySOne(String dynSql) {
        return sqlSessionTemplate.selectOne(className + ".queryByS", ParamsMap.newMap("dynSql", dynSql));
    }

    @DataSource(DataSourceHolder.DBType.master)
    public int ddlBySql(String dynSql) {
        return sqlSessionTemplate.insert(className + ".ddlBySql", ParamsMap.newMap("dynSql", dynSql));
    }

@DataSource
public Map<String,Object> queryJsonSize(String tableName,String fieldKey,String jsonPath,Map<String,Object> params) {
    return sqlSessionTemplate.selectOne(className + ".queryJsonSize", ParamsMap.newMap("tableName",tableName).addParams("fieldKey",fieldKey).addParams("jsonPath",jsonPath).addParams("params",params));
}
}
