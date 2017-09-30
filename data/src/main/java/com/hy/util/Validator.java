package com.hy.util;

import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * Created by panrui on 2015/10/2.
 */
public enum Validator implements Serializable {
    PASSWORD("\\S{8,32}",501, "密码长度不能小于8位"),
    SN_CARD("(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)",502, "您输入的身份证格式不正确"),
    PHONE("^1[345789]+?\\d{9}$",503,"您输入的手机号码格式不正确"),
    NAME("\\w{5,32}",504,"用户名格式不正确.");
    private String regexp;
    private int code;
    private String msg;

    Validator(String regexp,int code,String msg){
        this.regexp=regexp;
        this.code=code;
        this.msg=msg;
    }
    public int getCode(){
        return this.code;
    }

    public boolean v(String value) {
        return !StringUtils.isEmpty(value)&&value.matches(regexp);
    }

    @Override
    public String toString() {
        return "{\"code\":"+this.code+"\",\"msg\":\""+this.msg+"\"}";
    }
}
