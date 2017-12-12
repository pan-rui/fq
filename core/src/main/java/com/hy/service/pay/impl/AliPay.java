package com.hy.service.pay.impl;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConstants;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.hy.base.BaseResult;
import com.hy.base.IBase;
import com.hy.base.ReturnCode;
import com.hy.service.pay.Pay;
import com.hy.service.pay.IPaymentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Created by Administrator on 2015/10/20.
 */
@Service
public class AliPay implements IPaymentService {
    private Logger logger = LogManager.getLogger(AliPay.class);
    private  AlipayClient alipayClient;
    private String notifyURL;
    private String aliPublicKey;
    private String signType;

    @PostConstruct
    public void initAlipayClient() throws IOException {
        ResourceBundle bundle = new PropertyResourceBundle(getClass().getResourceAsStream("/payConfig.properties"));
        aliPublicKey=bundle.getString("aliPublicKey");
        signType=bundle.getString("signType");
        alipayClient = new DefaultAlipayClient(bundle.getString("gateway"),bundle.getString("appId"),bundle.getString("appPrivateKey"), AlipayConstants.FORMAT_JSON,AlipayConstants.CHARSET_UTF8,aliPublicKey,AlipayConstants.SIGN_TYPE_RSA2);
        notifyURL = bundle.getString("aliPay_notify_url");
        Pay.putType("aliPay",this);
    }
/*    public static synchronized AlipayClient getInstance() {
        if(alipayClient==null){
            initAlipayClient();
        }
        return alipayClient;
    }*/
    @Override
    public BaseResult recharge(Map<String, Object> param) {
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
//SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody((String) param.get("customVal"));
        model.setSubject((String) param.get("subject"));
        model.setOutTradeNo((String)param.get("orderNo"));
        model.setTimeoutExpress("30m");
        model.setTotalAmount(String.valueOf(param.get("amount")));
//        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setBizModel(model);
        request.setNotifyUrl(notifyURL);
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            return new BaseResult(ReturnCode.OK,response);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return new BaseResult(ReturnCode.FAIL);
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
    public BaseResult checkResult( Map<String, String> param) {
        boolean flag = false;
        try {
            flag = AlipaySignature.rsaCheckV1(param, aliPublicKey, AlipayConstants.CHARSET_UTF8, signType);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return flag?new BaseResult(ReturnCode.OK):new BaseResult(ReturnCode.FAIL);
    }

    @Override
    public BaseResult refund(Map<String, Object> param) {
        return new BaseResult(ReturnCode.FAIL);
    }

}
