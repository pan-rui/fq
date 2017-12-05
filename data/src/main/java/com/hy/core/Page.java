package com.hy.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/9/1.
 */
public class Page<T> implements Serializable{
    private int pageNo = 1;//页码，默认是第一页
    private int pageSize=10;//TODO:每页显示的记录数，默认是30
    private int totalRecord;//总记录数
    private int totalPage;//总页数
    private List<T> results;//对应的当前页记录
    private T params ;//其他的参数我们把它分装成一个Map对象
    private Map<String,Object> matchs;
    private Map<String,String> orderMap;
    private String tableName;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(int totalRecord) {
        this.totalRecord = totalRecord;
        //在设置总页数的时候计算出对应的总页数，在下面的三目运算中加法拥有更高的优先级，所以最后可以不加括号。
        int totalPage = totalRecord%pageSize==0 ? totalRecord/pageSize : totalRecord/pageSize + 1;
        this.setTotalPage(totalPage);
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public Map<String,Object> getParams() {
        return params==null?new LinkedHashMap<String,Object>():params instanceof Map?(Map)params:Constants.poToMap(params);
//        return params;
    }

    public void setParams(T params) {
        this.params = params;
    }

    public Map<String,Object> getMatchs() {
//        return matchs==null?new LinkedHashMap<String,Object>():matchs;
        return matchs;
    }

    public void setMatchs(Map<String,Object> matchs) {
        this.matchs = matchs;
    }

    public Map<String, String> getOrderMap() {
        return orderMap;
    }

    public Page setOrderMap(Map<String, String> orderMap) {
        this.orderMap = orderMap;
        return this;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Page [pageNo=").append(pageNo).append(", pageSize=")
                .append(pageSize).append(", results=").append(results).append(
                ", totalPage=").append(totalPage).append(
                ", totalRecord=").append(totalRecord).append("]");
        return builder.toString();
    }

}
