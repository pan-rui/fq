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

    public Map<String, Object> queryUserInfoMul(Object userId,String openId) {
        return sqlSessionTemplate.selectOne(className + ".queryUserInfoMul", ParamsMap.newMap("userId", userId).addParams("openId",openId));
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

    /**
     * 我的账单,还款列表
     * @param userId
     * @return
     */
    @Cacheable(key = "'repayList_'+#userId",condition = "#result != null")
    public List<Map<String, Object>> queryBillMul(Long userId) {
        List<Map<String, Object>> resultList = sqlSessionTemplate.selectList(className + ".queryBillMul", ParamsMap.newMap("userId", userId));
        return resultList;
    }

    /**
     * 我的业绩
     * @param workId
     * @param type
     * @return
     */
    @Cacheable(key = "'performanceList$'+#workId+'_'+#type",condition = "#result != null")
    public List<Map<String, Object>> queryPerformanceMul(String workId,int type) {
        List<Map<String, Object>> resultList = sqlSessionTemplate.selectList(className + ".queryPerformanceMul", ParamsMap.newMap("workId", workId).addParams("type",type));
        return resultList;
    }

    public List<Map<String, Object>> repayMind(int interval) {
        List<Map<String, Object>> resultList = sqlSessionTemplate.selectList(className + ".repayMind", ParamsMap.newMap("interval", interval));
        return resultList;
    }

    public List<Map<String, Object>> queryCouponMindMul(int interval) {
        List<Map<String, Object>> resultList = sqlSessionTemplate.selectList(className + ".queryCouponMindMul", ParamsMap.newMap("interval", interval));
        return resultList;
    }

    public List<Map<String, Object>> queryHelpPageMul(final Page page) {
        List<Map<String,Object>> resultList=sqlSessionTemplate.selectList(className + ".queryHelpPageMul", ParamsMap.newMap("page", page));
        page.setResults(resultList);
        return resultList;
    }

    public List<Map<String, Object>> queryRepayRecordTab(Object userId,String date) {
        List<Map<String, Object>> resultList = sqlSessionTemplate.selectList(className + ".queryRepayRecordTab", ParamsMap.newMap("userId", userId).addParams("date",date));
        return resultList;
    }

    public List<Map<String, Object>> queryCouponPageMul(final Page page) {
        List<Map<String,Object>> resultList=sqlSessionTemplate.selectList(className + ".queryCouponPageMul", ParamsMap.newMap("page", page));
        page.setResults(resultList);
        return resultList;
    }

    public List<Map<String, Object>> queryValidCoupon(Object userId) {
        List<Map<String, Object>> resultList = sqlSessionTemplate.selectList(className + ".queryValidCoupon", ParamsMap.newMap("userId", userId));
        return resultList;
    }

    public List<Map<String, Object>> queryUserInsurancdPageMul(final Page page) {
        List<Map<String,Object>> resultList=sqlSessionTemplate.selectList(className + ".queryUserInsurancdPageMul", ParamsMap.newMap("page", page));
        page.setResults(resultList);
        return resultList;
    }

    public List<Map<String, Object>> queryProductDiscussPageMul(final Page page) {
        List<Map<String,Object>> resultList=sqlSessionTemplate.selectList(className + ".queryProductDiscussPageMul", ParamsMap.newMap("page", page));
        page.setResults(resultList);
        return resultList;
    }
}
