package com.hy.base;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/9/14.
 */
public class BaseResult implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7929060787308730351L;
	
	private String msg;
    private int code;
    private Object data;

    public BaseResult() {
    }

    public BaseResult(ReturnCode returnCode) {
        this.code = returnCode.getCode();
        this.msg = returnCode.getMsg();
    }
    
    public BaseResult(ReturnCode returnCode, Object data) {
        this.code = returnCode.getCode();
        this.msg = returnCode.getMsg();
        this.data=data;
    }

    public BaseResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public BaseResult(int code, Object data) {
        this.code = code;
        this.data = data;
    }
    public BaseResult(int code, String msg,Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
