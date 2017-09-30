package com.hy.vo;

import org.springframework.http.HttpMethod;

import java.util.Map;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-07-24 16:12)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
public interface Protocol {
    public static final String TEXT = "text/plain";
    public static final String JSON = "application/json;charset=utf-8";
    public static final String BYTE = "application/octet-stream";
    public static final String FORM = "application/x-www-form-urlencoded";
    public static final String MULTI = "multipart/form-data";
    String getUrl();
    HttpMethod getMethod();
    String getContentType();
    Map<String, Object> getPostParams();
    Map<String, Object> getHeads();

}
