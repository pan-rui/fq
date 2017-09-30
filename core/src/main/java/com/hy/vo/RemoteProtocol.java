package com.hy.vo;

import com.hy.core.ParamsMap;
import org.springframework.http.HttpMethod;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 116.7.226.222:100
 *
 * @Description: ${Description}
 * @Author: 潘锐 (2017-07-17 16:37)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
public enum RemoteProtocol implements Protocol {
    CERT("http://jisubank4.market.alicloudapi.com/bankcardverify4/verify?bankcard=6228480402564881235&idcard=410184198501181235&mobile=13333333333&realname=xxfd", FORM, HttpMethod.GET,ParamsMap.newMap("Authorization","APPCODE 2c32413199c24e43a9dd72ecd3e271d8"))
    ,CHECKWORK("http://116.7.226.222:10001/weChat/getCheckWork",FORM, HttpMethod.POST,ParamsMap.newMap("openId","").addParams("ddBB","").addParams("tenantId","").addParams("month","").addParams("projectCode",""))
    , DOWNIMG("http://116.7.226.222:10001/weChat/downImg", FORM, HttpMethod.POST,new LinkedHashMap(), ParamsMap.newMap("openId", "").addParams("ddBB", "").addParams("tenantId", "").addParams("projectCode", "").addParams("serverId", "").addParams("isFront", "").addParams("accToken", ""));
    private String url;
    private String contentType;
    private HttpMethod method;
    private Map<String, Object> postParams;
    private Map<String, Object> heards;

    public String getUrl() {
        return url;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public Map<String, Object> getPostParams() {
        return postParams;
    }

    RemoteProtocol(String url, HttpMethod method) {
        this.url = url;
        this.method = method;
    }

    RemoteProtocol(String url, String contentType, HttpMethod method) {
        this.url = url;
        this.contentType = contentType;
        this.method = method;
    }

    RemoteProtocol(String url, String contentType, HttpMethod method, Map<String, Object> heads) {
        this.url = url;
        this.contentType = contentType;
        this.method = method;
        this.heards = heads;
    }

    RemoteProtocol(String url, String contentType, HttpMethod method, Map<String, Object> heards, Map<String, Object> parmas) {
        this.url = url;
        this.contentType = contentType;
        this.method = method;
        this.heards = heards;
        this.postParams = parmas;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public Map<String, Object> getHeads() {
        return this.heards;
    }
}
