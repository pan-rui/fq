package com.hy.action;

import com.alibaba.fastjson.JSON;
import com.hy.annotation.EncryptProcess;
import com.hy.base.BaseResult;
import com.hy.base.ReturnCode;
import com.hy.core.Constants;
import com.hy.core.Page;
import com.hy.core.ParamsMap;
import com.hy.core.Table;
import com.hy.dao.BaseDao;
import com.hy.dao.UserDao;
import com.hy.service.CommonService;
import com.hy.service.OrderService;
import com.hy.service.pay.Pay;
import com.hy.util.JuheUtil;
import com.hy.vo.ParamsVo;
import com.hy.vo.RemoteProtocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("order")
public class OrderAction extends BaseAction {
    @Autowired
    private BaseDao baseDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CommonService commonService;
    @Autowired
    @Value("#{config['SHIPMENTS_KEY']}")
    private String SHIPMENTS_KEY;
    private static final String orderCancelRemind = "ORDER_CANCEL_REMIND";
    private static final Logger logger = LogManager.getLogger(OrderAction.class);
    private String tableName = Table.FQ + Table.ORDER;

    @PostMapping("/add")
    public BaseResult addOrder(HttpServletRequest request, @RequestHeader(Constants.USER_ID) String uId, @RequestHeader(Constants.USER_PHONE) String phone, @EncryptProcess ParamsVo paramsVo) {
        return orderService.addOrder(uId,getIpAddr(request),paramsVo);
    }

    @GetMapping("id")
    public BaseResult getById(@RequestHeader(Constants.USER_ID) String userId, @RequestHeader(Constants.USER_PHONE) String phone, @RequestParam("pId") Long pId) {
        return new BaseResult(ReturnCode.OK, baseDao.queryByIdInTab(tableName, pId));
    }

    @PostMapping("/page")
    public BaseResult getByPage(@RequestHeader(Constants.USER_ID) String userId, @RequestHeader(Constants.USER_PHONE) String phone, @EncryptProcess Page page) {
        baseDao.queryPageInTab(tableName, page);
        return new BaseResult(ReturnCode.OK, page);
    }

    @PostMapping("pay")
    public BaseResult payOrder(@RequestHeader(Constants.USER_ID) String uId, @RequestHeader(Constants.USER_PHONE) String phone, @EncryptProcess ParamsVo paramsVo) {
        String payType = String.valueOf(paramsVo.getParams().remove(Table.Order.PAY_TYPE.name()));
        String bankCardNo = (String) paramsVo.getParams().remove("BANK_CARD_NO");
        List<Map<String, Object>> orderList = baseDao.queryByProsInTab(Table.FQ + Table.ORDER, paramsVo.getParams());
        Assert.notEmpty(orderList, "无此订单。");
        Map<String, Object> params = orderList.get(0);
        params.put("bankCardNo", bankCardNo);
        BaseResult baseResult = orderService.pay(payType, params);
/*        if (baseResult.getCode() == 0) {

*//*            String appMeta=Constants.hgetCache(CacheKey.APP_META,JPushUtil.USER_APP+userId);
            if(!StringUtils.isEmpty(appMeta)) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("orderId", (Number)paramsVo.getId());
                JPushUtil.pushByRegId(JPushUtil.USER_APP, "您刚才支付了一笔订单,金额为:" + orderMap.get("payMoney"), "如有疑问请联系客服.点击查看详情:", jsonObject, appMeta.split(Table.SEPARATE_SPLIT)[0]);        //TODO:AppMeta
            }*//*
            return baseResult;
        } else*/
        return baseResult;
    }

    @PostMapping("aliPayNotify")
    public BaseResult aliPayNotify(@RequestBody ParamsVo paramsVo) {
        BaseResult baseResult = Pay.checkResult(paramsVo.getParams().addParams("payType", "aliPay"));
        if (baseResult.getCode() == 0) {
            boolean flag = true;
            String trade_status = (String) paramsVo.getParams().get("trade_status");
            String out_trade_no = (String) paramsVo.getParams().remove("out_trade_no");
            if (trade_status.equals("TRADE_FINISHED")) {
                logger.info("交易完结........订单号为:" + out_trade_no);
            } else if (trade_status.equals("TRADE_SUCCESS")) {
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                //如果有做过处理，不执行商户的业务程序
                //注意：
                //付款完成后，支付宝系统发送该交易状态通知
/*                Order order = new Order();
                order.setId(Long.parseLong(out_trade_no));
                order.setPayNum(trade_no);
                order.setPayInfo(JSON.toJSONString(params));
                order.setPayMoney(Double.parseDouble(params.get("total_fee")));
                order.setState("1");
                order.setUtime(new Date());*/
//                orderService.updatePayOrder(ParamsMap.newMap("id", Long.parseLong(out_trade_no)).addParams("payNum", trade_no).addParams("payInfo", JSON.toJSONString(params)).addParams("payMoney", Double.parseDouble(params.get("total_fee"))).addParams("state", "1"));
//                String[] bodyArr = ((String) paramsVo.getParams().remove("body")).split(Table.SEPARATE_SPLIT);
                String payTime = (String) paramsVo.getParams().get("gmt_payment");
                String amount = (String) paramsVo.getParams().get("total_amount");
                String trade_no = (String) paramsVo.getParams().remove("trade_no");
                flag = commonService.paySuccessCallBack(paramsVo.getParams(), out_trade_no, trade_no, payTime, "body", amount, "0");
   /*             DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                def.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
                TransactionStatus transStatus = transactionManager.getTransaction(def);
                try {
                    transactionManager.commit(transStatus);
                    if (IPaymentService.TradeType.firstPay.name().equals(tradeType)) {
                        //更新订单信息及相关信息
                        //保存交易记录
                        ParamsMap paramsMap = ParamsMap.newMap(Table.Order.STATE.name(), "1").addParams(Table.Order.PAY_TIME.name(), payTime).addParams(Table.Order.PAY_NO.name(), trade_no);
                        baseDao.insertByProsInTab(Table.FQ + Table.TRADE_RECORD, ParamsMap.newMap(Table.TradeRecord.TRADE_TYPE.name(), tradeType).addParams(Table.TradeRecord.ACCT_TIME.name(), payTime).addParams(Table.USER_ID, bodyArr[1])
                                .addParams(Table.TradeRecord.PAY_TYPE.name(), "0").addParams(Table.TradeRecord.ORDER_NO.name(), out_trade_no).addParams(Table.TradeRecord.TRADE_NO.name(), trade_no).addParams(Table.TradeRecord.PAY_INFO.name(), JSON.toJSONString(paramsVo.getParams())));
                        baseDao.updateByProsInTab(Table.FQ + Table.ORDER, paramsMap.addParams(Table.ID, bodyArr[2]));
                        flag = orderService.genPlanRepay(baseDao.queryByIdInTab(Table.FQ + Table.ORDER, bodyArr[2]));
                    } else if (IPaymentService.TradeType.repay.name().equals(tradeType)) {
                        baseDao.insertByProsInTab(Table.FQ + Table.TRADE_RECORD, ParamsMap.newMap(Table.TradeRecord.TRADE_TYPE.name(), tradeType).addParams(Table.TradeRecord.ACCT_TIME.name(), payTime).addParams(Table.USER_ID, bodyArr[1])
                                .addParams(Table.TradeRecord.PAY_TYPE.name(), "0").addParams(Table.TradeRecord.BILL_DATE.name(), bodyArr[2]).addParams(Table.TradeRecord.ORDER_NO.name(), out_trade_no).addParams(Table.TradeRecord.TRADE_NO.name(), trade_no)
                                .addParams(Table.TradeRecord.PAY_INFO.name(), JSON.toJSONString(paramsVo.getParams())));
                        Map<String, Object> repayMap = baseDao.queryByIdInTab(Table.FQ + Table.PLAN_REPAYMENT, bodyArr[2]);
                        long diffSecond = System.currentTimeMillis() - ((Date) repayMap.get("planrepayDate")).getTime();
                        baseDao.updateByProsInTab(Table.FQ + Table.PLAN_REPAYMENT, ParamsMap.newMap(Table.PlanRepayment.STATUS.name(), diffSecond > 0 ? "1" : "2").addParams(Table.PlanRepayment.PAY_DATE, new Date()).addParams(Table.ID, bodyArr[2]));
                        String appMeta = Constants.hgetCache(CacheKey.APP_META, JPushUtil.USER_APP + bodyArr[1]);
                        if (repayMap.get("periodSum") == repayMap.get("repayNum")) {
                            Long orderId = (Long) repayMap.get("orderId");
                            baseDao.updateByProsInTab(Table.FQ + Table.ORDER, ParamsMap.newMap(Table.Order.STATE.name(), "10").addParams(Table.ID, orderId));           //更新订单状态
                            if (!StringUtils.isEmpty(appMeta)) {
                                new Thread(() -> {
                                    JsonObject jsonObject = new JsonObject();
                                    jsonObject.addProperty("orderId", orderId);
                                    JPushUtil.pushByRegId(JPushUtil.USER_APP, "您有一笔融宝分期订单已全部还款完成.", "点击查看详情!", jsonObject, appMeta.split(Table.SEPARATE_SPLIT)[0]);
                                }).start();
                            }
                        }
                        if (!StringUtils.isEmpty(appMeta)) {
                            Calendar calendar = Calendar.getInstance();
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("repayId", bodyArr[2]);
                            JPushUtil.pushByRegId(JPushUtil.USER_APP, "您" + calendar.get(Calendar.MONTH) + "月账单" + repayMap.get("planrepayMoney") + "元已还清!", "点击查看详情:", jsonObject, appMeta.split(Table.SEPARATE_SPLIT)[0]);   //TODO:AppMeta
                        }

                    } else if (IPaymentService.TradeType.freeRepay.name().equals(tradeType)) {
                        Calendar calendar = Calendar.getInstance();
                        String[] dateArr = bodyArr[2].split(Table.SEPARATE_CACHE);
                        baseDao.insertByProsInTab(Table.FQ + Table.TRADE_RECORD, ParamsMap.newMap(Table.TradeRecord.TRADE_TYPE.name(), "freeRepay").addParams(Table.TradeRecord.ACCT_TIME.name(), payTime).addParams(Table.USER_ID, bodyArr[1])
                                .addParams(Table.TradeRecord.TRADE_AMOUNT, amount).addParams(Table.TradeRecord.PAY_TYPE.name(), "0")
                                .addParams(Table.TradeRecord.BILL_DATE.name(), new Calendar.Builder().setDate(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]) - 1, calendar.get(Calendar.DAY_OF_MONTH)).build().getTime())
                                .addParams(Table.TradeRecord.ORDER_NO.name(), out_trade_no).addParams(Table.TradeRecord.TRADE_NO.name(), trade_no).addParams(Table.TradeRecord.PAY_INFO.name(), JSON.toJSONString(paramsVo.getParams())));
                        List<Map<String, Object>> repayList = userDao.queryRepaysTab(bodyArr[1], bodyArr[2]);
//                        Calendar calendar = Calendar.getInstance();
                        BigDecimal money = new BigDecimal(amount);
                        for (Map<String, Object> repayMap : repayList) {
                            if (money.compareTo(BigDecimal.ZERO) <= 0) break;
                            ParamsMap upMap = ParamsMap.newMap(Table.PlanRepayment.UTIME.name(), calendar.getTime()).addParams(Table.PlanRepayment.REPAY_TYPE.name(), "freeRepay");
                            String status = (String) repayMap.get("status");
                            if (status.equals("1") || status.equals("2"))
                                continue;
                            BigDecimal planrepayMoney = (BigDecimal) repayMap.get("planrepayMoney");
                            BigDecimal realRepayMoney = (BigDecimal) repayMap.get("realRepayMoney");
                            if (status.equals("0")) {       //未还
                                BigDecimal syRepay = planrepayMoney.subtract(realRepayMoney);
                                if (money.compareTo(syRepay) >= 0) {     //多于应还本金
                                    upMap.addParams(Table.PlanRepayment.REAL_REPAY_MONEY.name(), planrepayMoney);
                                    upMap.addParams(Table.PlanRepayment.STATUS.name(), "1");
                                    money = money.subtract(syRepay);
                                } else {
                                    upMap.addParams(Table.PlanRepayment.REAL_REPAY_MONEY.name(), realRepayMoney.add(money));
                                    money = BigDecimal.ZERO;
                                }
                            } else if (status.equals("3")) {       //逾期
                                BigDecimal realRepayInterest = (BigDecimal) repayMap.get("realRepayInterest");
                                BigDecimal overdue = (BigDecimal) repayMap.get("overdue");
                                BigDecimal syRepayOve = overdue.subtract(realRepayInterest);
                                BigDecimal syRepay = planrepayMoney.subtract(realRepayMoney);
                                if (money.compareTo(syRepayOve) >= 0) {     //多于应还利息
                                    upMap.addParams(Table.PlanRepayment.REAL_REPAY_INTEREST.name(), overdue);
                                    money = money.subtract(syRepayOve);
                                    if (money.compareTo(syRepay) >= 0) {     //多于应还本金
                                        upMap.addParams(Table.PlanRepayment.REAL_REPAY_MONEY.name(), planrepayMoney);
                                        upMap.addParams(Table.PlanRepayment.STATUS.name(), "1");
                                        money = money.subtract(syRepay);
                                    } else {
                                        upMap.addParams(Table.PlanRepayment.REAL_REPAY_MONEY.name(), realRepayMoney.add(money));
                                        money = BigDecimal.ZERO;
                                    }
                                } else {      //少于利息
                                    upMap.addParams(Table.PlanRepayment.REAL_REPAY_INTEREST.name(), realRepayInterest.add(money));
                                    money = BigDecimal.ZERO;
                                }
                            }
                            baseDao.updateByProsInTab(Table.FQ + Table.PLAN_REPAYMENT, upMap.addParams(Table.ID, repayMap.get("id")));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    flag = false;
                    transactionManager.rollback(transStatus);
//                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                }*/
                logger.info("支付成功........订单号为:" + out_trade_no);
//                payedNotify(Constants.getSystemStringValue("Pay_Notify_Phone"),"支付宝",Double.parseDouble(params.get("total_fee")),out_trade_no);
            }
            if (!flag) baseResult.setCode(1);
        } else logger.error("订单支付验证失败异常...........");
        return baseResult;
    }

    /**
     * 订单剩余付款时间
     *
     * @param uId
     * @param ctime
     * @return
     */
    @GetMapping("remainTime/{ctime:\\d+}")
    public BaseResult remainPayTime(@RequestHeader(Constants.USER_ID) Long uId, @PathVariable Long ctime) {
        String delay2 = Constants.getSystemStringValue(orderCancelRemind);
        if (StringUtils.isEmpty(delay2)) delay2 = "0";
        return new BaseResult(ReturnCode.OK, ctime + Long.parseLong(delay2) - System.currentTimeMillis());
    }

    /**
     * 发货
     *
     * @param uId
     * @param paramsVo
     * @return
     */
    @PostMapping("shipments")
    public WebAsyncTask<BaseResult> shipments(@RequestHeader(Constants.USER_ID) Long uId, @RequestBody ParamsVo paramsVo) {
        List<Map<String, Object>> datas = paramsVo.getDatas();
        return new WebAsyncTask<BaseResult>(3500L, () -> {
            if (CollectionUtils.isEmpty(datas)) return new BaseResult(21111, "运单号码为空");
            int[] count = new int[]{0};
            datas.forEach((map) -> {
                String number = String.valueOf(map.get("number"));
                String logistics = (String) map.get("logistics");
                String orderId = String.valueOf(map.get("orderId"));
                Map param = ParamsMap.newMap("company", logistics).addParams("number", number).addParams("key", SHIPMENTS_KEY).addParams("parameters", ParamsMap.newMap("callbackurl", "http://58.61.142.74:1000/consumer/single/shipCall?orderId=" + orderId)).addParams("autoCom", "1").addParams("resultv2", "1");
//                Map<String, Object> resultMap = JSON.parseObject(HttpUtil.execute(RemoteProtocol.SHIPMENTS, ParamsMap.newMap("schema", "json").addParams("param", JSON.toJSONString(param))), Map.class);
                Map<String, Object> resultMap = null;
                try {
                    resultMap = JSON.parseObject(JuheUtil.net(RemoteProtocol.SHIPMENTS.getUrl(), ParamsMap.newMap("schema", "json").addParams("param", JSON.toJSONString(param)), HttpMethod.POST.name()), Map.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!CollectionUtils.isEmpty(resultMap) && (Boolean) resultMap.get("result")) {
                    count[0] += baseDao.updateByProsInTab(Table.FQ + Table.ORDER, ParamsMap.newMap(Table.Order.STATE.name(), "2").addParams(Table.Order.LOGISTICS.name(), logistics).addParams(Table.Order.LOGISTICS_CODE.name(), number)
                            .addParams(Table.Order.SHIPMENTS_TIME.name(), new Date()).addParams(Table.ID, orderId));
                }
            });
            return count[0] == datas.size() ? new BaseResult(ReturnCode.OK) : new BaseResult(ReturnCode.FAIL, count[0]);
        });
    }
}
