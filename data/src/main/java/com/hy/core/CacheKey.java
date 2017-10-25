package com.hy.core;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-07-24 15:24)
 * @version: \$Rev: 3830 $
 * @UpdateAuthor: \$Author: panrui $
 * @UpdateDateTime: \$Date: 2017-07-28 10:05:44 +0800 (周五, 28 7月 2017) $
 */
public class CacheKey {
    public static final String PHONE = "PHONE";
    public static final String USERID = "USERID";
    // 用户短信验证码
    public static final String U_SMS_Prefix = "U_SMS_";
//    public static final String S_SMS_Prefix = "S_SMS_";
    //用户登录主信息
    public static final String U_= "U_";
//    public static final String S_= "S_";
    //用户登录令牌
    public static final String U_TOKEN_Prefix= "U_TOKEN_";
//    public static final String S_TOKEN_Prefix= "S_TOKEN_";
    //用户登录客户端序列号
    public static final String U_SN_Prefix = "U_SN_";
    //    public static final String S_SN_Prefix = "U_SN_";
    //用户认证状态
    public static final String U_CERT_STATUS_Prefix = "U_CERT_STATUS_";

    public static final String U_BANK_Prefix = "U_BANK_";

    public static final String APP_META_Prefix="APP_META_";

}
