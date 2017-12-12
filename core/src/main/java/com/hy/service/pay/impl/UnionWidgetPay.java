package com.hy.service.pay.impl;

import com.hy.base.BaseResult;
import com.hy.base.IBase;
import com.hy.base.ReturnCode;
import com.hy.core.ParamsMap;
import com.hy.service.pay.IPaymentService;
import com.hy.service.pay.Pay;
import com.hy.unionpay.sdk.AcpService;
import com.hy.unionpay.sdk.SDKConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.function.DoublePredicate;

/**
 * Created by Administrator on 2015/10/20.
 */
@Service
public class UnionWidgetPay implements IPaymentService {
    private Logger logger = LogManager.getLogger(UnionWidgetPay.class);
    @Value("#{config['MERID2']}")
    private String merId;
    private final String accessType = "0";
//    private String notifyURL;
//    private String aliPublicKey;
    private String accType = "01";
    private String channelType = "08";

    @PostConstruct
    public void initAlipayClient() throws IOException {
/*        ResourceBundle bundle = new PropertyResourceBundle(getClass().getResourceAsStream("/payConfig.properties"));
        aliPublicKey = bundle.getString("aliPublicKey");
//        signType=bundle.getString("signType");
        notifyURL = bundle.getString("aliPay_notify_url");*/
        SDKConfig.getConfig().loadPropertiesFromSrc();// 从classpath加载acp_sdk.properties文件
        Pay.putType("unionWidgetPay", this);
    }

    /*    public static synchronized AlipayClient getInstance() {
            if(alipayClient==null){
                initAlipayClient();
            }
            return alipayClient;
        }*/
    @Override
    public BaseResult recharge(Map<String, Object> param) {
        ParamsMap paramsMap = ParamsMap.newMap("version", SDKConfig.getConfig().getVersion()).addParams("encoding", IBase.DEF_CHATSET).addParams("signMethod", SDKConfig.getConfig().getSignMethod())
                .addParams("txnType", "01").addParams("txnSubType", "01").addParams("bizType", "000201").addParams("channelType", channelType).addParams("merId", merId).addParams("accessType", accessType)
                .addParams("orderId", (String) param.get("orderNo")).addParams("txnTime", IBase.sdf2.format(new Date())).addParams("accType", accType).addParams("currencyCode", "156").addParams("txnAmt", String.valueOf(new BigDecimal(String.valueOf(param.get("amount"))).multiply(new BigDecimal(100)).intValue()))
                .addParams("backUrl", SDKConfig.getConfig().getBackUrl()).addParams(param.get("customKey"), param.get("customVal"));
        Map<String, String> reqData = AcpService.sign(paramsMap, IBase.DEF_CHATSET);
        String requestBackUrl = SDKConfig.getConfig().getAppRequestUrl();
        Map<String, String> rspData = AcpService.post(reqData, requestBackUrl, IBase.DEF_CHATSET);
        if (!rspData.isEmpty()) {
            if (AcpService.validate(rspData, IBase.DEF_CHATSET)) {
                String respCode = rspData.get("respCode");
                if (("00").equals(respCode)) {
                    //交易已受理(不代表交易已成功），等待接收后台通知更新订单状态,也可以主动发起 查询交易确定交易状态。
                    //如果是配置了敏感信息加密，如果需要获取卡号的铭文，可以按以下方法解密卡号
                    //String accNo1 = resmap.get("accNo");
                    //String accNo2 = AcpService.decryptData(accNo1, "UTF-8");  //解密卡号使用的证书是商户签名私钥证书acpsdk.signCert.path
                    //LogUtil.writeLog("解密后的卡号："+accNo2);
                    return new BaseResult(ReturnCode.OK,rspData);
                }
/*                else if(("03").equals(respCode)||
                        ("04").equals(respCode)||
                        ("05").equals(respCode)){
                    //后续需发起交易状态查询交易确定交易状态
                }else{
                    //其他应答码为失败请排查原因
                }*/
            } else {
                //TODO 检查验证签名失败的原因
                logger.warn("====================银联代收验证签名失败....");
            }
        } else {
            //未返回正确的http状态
            logger.error("====================银联控件支付出现网络故障...."+rspData);
        }
        return new BaseResult(8989, rspData.get("respMsg"));
    }

    @Override
    public BaseResult withdraw(Map<String, Object> param) {
//        Object obj = pay.getBodyInfo(param, TradeType.withdraw);

        return new BaseResult(ReturnCode.FAIL);
    }

    @Override
    public BaseResult confirmPay(Map<String, Object> param) {
//        return pay.checkResult(param);
        return null;
    }

    @Override
    public BaseResult checkResult(Map<String, String> param) {
        boolean flag = AcpService.validate(param, IBase.DEF_CHATSET);
        return flag ? new BaseResult(ReturnCode.OK) : new BaseResult(ReturnCode.FAIL);
    }

    @Override
    public BaseResult refund(Map<String, Object> param) {
        ParamsMap<String,String> paramsMap = ParamsMap.newMap("version", SDKConfig.getConfig().getVersion()).addParams("encoding", IBase.DEF_CHATSET).addParams("signMethod", SDKConfig.getConfig().getSignMethod())
                .addParams("txnType", "31").addParams("txnSubType", "00").addParams("bizType", "000501").addParams("channelType", channelType).addParams("merId", merId).addParams("accessType", accessType)
                .addParams("orderId", (String) param.get("orderNo")).addParams("txnTime", IBase.sdf2.format(new Date())).addParams("currencyCode", "156").addParams("txnAmt", String.valueOf(param.get("amount")))
                .addParams("origQryId", (String)param.get("payNo")).addParams("backUrl", SDKConfig.getConfig().getBackUrl());
        Map<String, String> reqData = AcpService.sign(paramsMap, IBase.DEF_CHATSET);
        String url = SDKConfig.getConfig().getBackRequestUrl();								 //交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl
        Map<String, String> rspData = AcpService.post(reqData, url,IBase.DEF_CHATSET);  //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
        /**对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->**/
        //应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
        if(!rspData.isEmpty()){
            if(AcpService.validate(rspData, IBase.DEF_CHATSET)){
                String respCode = rspData.get("respCode") ;
                if(("00").equals(respCode)){
                    //交易已受理(不代表交易已成功），等待接收后台通知更新订单状态,也可以主动发起 查询交易确定交易状态。
                    new BaseResult(ReturnCode.OK);
                }
/*                else if(("03").equals(respCode)||
                        ("04").equals(respCode)||
                        ("05").equals(respCode)){
                    //后续需发起交易状态查询交易确定交易状态
                    //TODO
                }else{
                    //其他应答码为失败请排查原因
                    //TODO
                }*/
            }else{
                //TODO 检查验证签名失败的原因
            }
        }else{
            //未返回正确的http状态
        }
        return new BaseResult(ReturnCode.FAIL);
    }

}
