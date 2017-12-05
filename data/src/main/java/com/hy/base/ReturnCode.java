package com.hy.base;

import java.io.Serializable;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2015-09-27 14:21)
 * @version: \$Rev: 3621 $
 * @UpdateAuthor: \$Author: zhangj $
 * @UpdateDateTime: \$Date: 2017-07-17 19:24:34 +0800 (周一, 17 7月 2017) $
 */
public enum ReturnCode implements Serializable {
    OK(0, "操作成功"),
    FAIL(1, "操作失败"),
    TOKEN_VERIFY_ERROR(2, "token无效"),
    REQUEST_PARAMS_VERIFY_ERROR(3, "请求参数错误"),
    HEADER_PARAMS_VERIFY_ERROR(4, "请求头参数错误"),
    SYSTEM_ERROR(5, "服务器错误"),
    REPEAT_SUBMIT_ERROR(6,"重复提交"),
    REQUEST_PARAMS_MISSING_ERROR(7, "请求参数缺失错误"),
    
    LOGIN_PHONE_ERROR(1001, "账号不存在"),
    LOGIN_PWD_ERROR(1002, "密码错误"),
    USER_PHONE_EXIST(1003, "用户号码已存在"),
    USER_NOT_EXISTS(1004, "用户不存在"),
    NO_AUTH(1016, "无此操作权限"),
    OLD_PWD_ERROR(1005, "原密码错误"),
    ONLY_LIMIT_CLIENT(1007, "只能在绑定的手机操作"),
    NO_DATA(2001, "没有相关数据"),

    NO_POST_AUTH(5000, "无此岗位权限"),
	NO_HOUSEHOLD_CHART(5001, "没有户型图"),
    Server_Exec_Error(8001,"服务端执行失败"),
    Server_Exec_Timeout(8002, "服务端执行超时"),


    NOT_SUPPORTED_PAY_TYPE(9011, "不支持的支付方式!"),
    ORDER_LIMIT_SIZE(9001,"您目前最多只能购买3件商品。");

    private String msg;
    private int code;

    ReturnCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "{\"code\":" + this.code + ",\"msg\":\"" + getMsg() + "\"}";
    }

}