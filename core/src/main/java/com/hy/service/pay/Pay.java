package com.hy.service.pay;

import com.hy.base.BaseResult;
import com.hy.base.ReturnCode;

import java.util.HashMap;
import java.util.Map;

public class Pay {
    private static Map<String, IPaymentService> payMap = new HashMap<>();
    private static Map<String, String> payType = new HashMap<>();
    static {
        payType.put("0", "aliPay");
        payType.put("1","weChat");
        payType.put("2", "unionPay");
        payType.put("3","balancePay");
        payType.put("4","unionWidgetPay");
    }

    public static void putType(String key, IPaymentService paymentService) {
        payMap.put(key, paymentService);
    }

    public static BaseResult recharge(final Map<String, Object> param) {
        IPaymentService paymentService = payMap.get(payType.get(param.remove("payType")));
        if (paymentService == null) return new BaseResult(ReturnCode.NOT_SUPPORTED_PAY_TYPE);
        return paymentService.recharge(param);
    }

    public static BaseResult withdraw(final Map<String, Object> param) {
        IPaymentService paymentService = payMap.get(payType.get(param.remove("payType")));
        if (paymentService == null) return new BaseResult(ReturnCode.NOT_SUPPORTED_PAY_TYPE);
        return paymentService.withdraw(param);
    }

    public static BaseResult confirmPay(final Map<String, Object> param) {
        IPaymentService paymentService = payMap.get(payType.get(param.remove("payType")));
        if (paymentService == null) return new BaseResult(ReturnCode.NOT_SUPPORTED_PAY_TYPE);
        return paymentService.confirmPay(param);
    }

    public static BaseResult checkResult(final Map param) {
        IPaymentService paymentService = payMap.get(param.remove("payType"));
        if (paymentService == null) return new BaseResult(ReturnCode.NOT_SUPPORTED_PAY_TYPE);
        return paymentService.checkResult(param);
    }

    public static BaseResult refund(final Map<String, Object> param) {
        IPaymentService paymentService = payMap.get(payType.get(param.remove("payType")));
        if (paymentService == null) return new BaseResult(ReturnCode.NOT_SUPPORTED_PAY_TYPE);
        return paymentService.refund(param);
    }

/*    public enum PayType{
        aliPay("支付宝支付"),
        wxPay("微信支付");
        private String remark;
        public String getRemark() {
            return remark;
        }
        PayType(String remark){
            this.remark=remark;
        }
    }*/

/*    public static IPaymentService getPayment(PayType payType){
        return (IPaymentService)Constants.applicationContext.getBean(payType.getRemark());
    }*/

    public static Map<String, String> getPayType() {
        return payType;
    }

}
