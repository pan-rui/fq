package com.hy.dao;

import com.hy.core.Page;
import com.hy.core.ParamsMap;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

@Repository
@CacheConfig(cacheNames = "tmp", cacheResolver = "baseImpl")
public class CommonDao {
    private SqlSessionTemplate sqlSessionTemplate;
    private String className;

    @Autowired
    public CommonDao(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
        this.className = this.getClass().getName();
    }

    public Connection getConnection() {
        return sqlSessionTemplate.getConnection();
    }

    public SqlSessionTemplate getSqlSessionTemplate() {
        return sqlSessionTemplate;
    }

    public Map<String, Object> queryUserInfo(Long userId) {
        return sqlSessionTemplate.selectOne(className + ".queryUserInfoMul", ParamsMap.newMap("userId", userId));
    }

    public List<Map<String, Object>> queryCJUserPageMul(final Page page) {
        List<Map<String,Object>> resultList=sqlSessionTemplate.selectList(className + ".queryCJUserPageMul", ParamsMap.newMap("page", page));
        page.setResults(resultList);
        return resultList;
    }
    public List<Map<String, Object>> querySBUserPageMul(final Page page) {
        List<Map<String,Object>> resultList=sqlSessionTemplate.selectList(className + ".querySBUserPageMul", ParamsMap.newMap("page", page));
        page.setResults(resultList);
        return resultList;
    }
    //
    public List<Map<String, Object>> queryDYHUserPageMul(final Page page) {
        List<Map<String,Object>> resultList=sqlSessionTemplate.selectList(className + ".queryDYHUserPageMul", ParamsMap.newMap("page", page));
        page.setResults(resultList);
        return resultList;
    }
    public List<Map<String, Object>> queryDYWHUserPageMul(final Page page) {
        List<Map<String,Object>> resultList=sqlSessionTemplate.selectList(className + ".queryDYWHUserPageMul", ParamsMap.newMap("page", page));
        page.setResults(resultList);
        return resultList;
    }

    public List<Map<String, Object>> queryXZUserPageMul(final Page page) {
        List<Map<String,Object>> resultList=sqlSessionTemplate.selectList(className + ".queryXZUserPageMul", ParamsMap.newMap("page", page));
        page.setResults(resultList);
        return resultList;
    }
    public List<Map<String, Object>> queryDSHUserPageMul(final Page page) {
        List<Map<String,Object>> resultList=sqlSessionTemplate.selectList(className + ".queryDSHUserPageMul", ParamsMap.newMap("page", page));
        page.setResults(resultList);
        return resultList;
    }
}