package com.hy.dao;

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
public class ProductDao {
    private SqlSessionTemplate sqlSessionTemplate;
    private String className;

    @Autowired
    public ProductDao(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
        this.className = this.getClass().getName();
    }

    public Connection getConnection() {
        return sqlSessionTemplate.getConnection();
    }

    public SqlSessionTemplate getSqlSessionTemplate() {
        return sqlSessionTemplate;
    }

    public List<Map<String,Object>> incrSales(int size,String attJson,Object productId,Object storeId) {
        return sqlSessionTemplate.selectList(className + ".incrSales", ParamsMap.newMap("size", size).addParams("attJson", attJson).addParams("productId",productId).addParams("storeId",storeId));
    }


}
