package com.hy.dao;

import com.hy.core.Page;
import com.hy.core.ParamsMap;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

@Repository
@CacheConfig(cacheNames = "tmp", cacheResolver = "baseImpl")
public class UserDao {
    private SqlSessionTemplate sqlSessionTemplate;
    private String className;

    @Autowired
    public UserDao(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
        this.className = this.getClass().getName();
    }

    public Connection getConnection() {
        return sqlSessionTemplate.getConnection();
    }

    public SqlSessionTemplate getSqlSessionTemplate() {
        return sqlSessionTemplate;
    }

    public List<Map<String,Object>> queryLoginTab(String phone,String clientSn) {
        return sqlSessionTemplate.selectList(className + ".queryLoginTab", ParamsMap.newMap("phone", phone).addParams("clientSn", clientSn));
    }

    public List<Map<String,Object>> queryRepaysTab(Object userId,String date) {
        return sqlSessionTemplate.selectList(className + ".queryRepaysTab", ParamsMap.newMap("userId", userId).addParams("date", date));
    }

    public List<Map<String,Object>> queryUserRepaysTab(Object userId,Object productId) {
        return sqlSessionTemplate.selectList(className + ".queryUserRepaysTab", ParamsMap.newMap("userId", userId).addParams("productId",productId));
    }

    public List<Map<String, Object>> queryUserCertTab(Object userId) {
        return sqlSessionTemplate.selectList(className + ".queryUserCertTab", ParamsMap.newMap("userId", userId));
    }
}
