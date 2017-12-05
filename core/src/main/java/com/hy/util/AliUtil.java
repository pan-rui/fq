package com.hy.util;
import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.hy.core.ParamsMap;
import com.hy.vo.RemoteProtocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class AliUtil {
    //产品名称:云通信短信API产品,开发者无需替换
    static final String sms_product = "Dysmsapi";
    //产品域名,开发者无需替换
    static final String sms_domain = "dysmsapi.aliyuncs.com";
    // TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
    static final String sms_accessKeyId = "LTAIrHvaUqkjhkJE";
//    static final String sms_accessKeyId = "LTAI8pLhnh0Dhs2H";
    //    static final String accessKeyId = "yourAccessKeyId";
//    static final String sms_accessKeySecret = "BdlXgP3nmvQX7t3tsCogYNmCfKpx2F";
    static final String sms_accessKeySecret = "ux2EMnN3jx6Whb0tZWxoycNqIQMLY5";
    static final String signName = "融宝";
    static final String cert_APPCODE = "";
    private static final Logger logger = LogManager.getLogger(AliUtil.class);

    public static SendSmsResponse sendSms(Map<String,Object> smsTemplate, String phone, String outParam) throws ClientException {
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", sms_accessKeyId, sms_accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", sms_product, sms_domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);
        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(phone);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(signName);
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode((String) smsTemplate.get("code"));
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.setTemplateParam((String) smsTemplate.get("variables"));
        //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");
        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId(outParam);
        //hint 此处可能会抛出异常，注意catch
        SendSmsResponse sendSmsResponse=null;
        try {
            sendSmsResponse = acsClient.getAcsResponse(request);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("==================阿里云短信接口返回错误......");
        }
        return sendSmsResponse;
    }

/*    public static void main(String[] args) {
        String host = "http://jisubank4.market.alicloudapi.com";
        String path = "/bankcardverify4/verify";
        String method = "GET";
        String appcode = "你自己的AppCode";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("bankcard", "6228480402564881235");
        querys.put("idcard", "410184198501181235");
        querys.put("mobile", "13333333333");
        querys.put("realname", "张先生");


        try {
            *//**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             *//*
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            System.out.println(response.toString());
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public static Map<String, Object> cert(Map<String, Object> params) {
        return JSON.parseObject(HttpUtil.execute(RemoteProtocol.CERT, params), Map.class);
    }
}
