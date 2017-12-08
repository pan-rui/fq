package com.hy.service;

import com.alibaba.fastjson.JSON;
import com.hy.base.BaseImpl;
import com.hy.base.BaseResult;
import com.hy.base.IBase;
import com.hy.base.ReturnCode;
import com.hy.core.ColumnProcess;
import com.hy.core.Constants;
import com.hy.core.ParamsMap;
import com.hy.core.Table;
import com.hy.dao.BaseDao;
import com.hy.dao.ProductDao;
import com.hy.service.pay.Pay;
import com.hy.task.OrderCancelJob;
import com.hy.task.OrderPayJob;
import com.hy.vo.ParamsVo;
import org.apache.commons.lang.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jcajce.provider.symmetric.ARC4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.html.HTMLTableElement;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrderService {
    @Autowired
    private BaseImpl baseImpl;
    @Autowired
    private BaseDao baseDao;
    @Autowired
    private ProductDao productDao;
    @Value("#{config['ORDER_TASK_POOL']}")
    private int orderTaskPool;
    private String tableName = Table.FQ + Table.ORDER;
    private static final String orderPayRemind = "ORDER_PAY_REMIND";
    private static final String orderCancelRemind = "ORDER_CANCEL_REMIND";
    private static final Logger logger = LogManager.getLogger(OrderService.class);
    private static final AtomicInteger atomicInteger = new AtomicInteger(0);
    private static final ScheduledExecutorService scheduPool = Executors.newScheduledThreadPool(3, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "ScheduThread-" + atomicInteger.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        }
    });

    public BaseResult pay(String payType, Map<String, Object> params) {
        //验证
        //支付接口
        String custom = "firstPay" + Table.SEPARATE_SPLIT + params.get("userId") + Table.SEPARATE_SPLIT + params.get("id");
        params.put("subject", params.get("productName"));
        params.put("amount", params.get("payMoney"));
        params.put("payType", payType);
        if ("2".equals(payType)) {
            String accNo = (String) params.get("bankCardNo");
            if (StringUtils.isEmpty(accNo)) return new BaseResult(17893, "卡号不能为空");
            params.put("accNo", accNo);
            params.put("customKey","reqReserved");
            params.put("customVal", custom);
        } else if ("0".equals(payType)) {
            params.put("customKey", "body");
            params.put("customVal", custom);
        } else if ("4".equals(payType)) {
            params.put("customKey","reqReserved");
            params.put("customVal", custom);
        }
        BaseResult baseResult = Pay.recharge(params);
        return baseResult;
    }

    public BaseResult repay(String payType, Map<String, Object> params) {
        //支付
        String custom = "repay" + Table.SEPARATE_SPLIT + params.get("userId") + Table.SEPARATE_SPLIT + params.get("id");
        params.put("orderNo", params.remove("repayNo"));
        params.put("subject", "用户(" + params.get("userId") + ")还款");
        BigDecimal amount = ((BigDecimal) params.get("planRepayMoney")).add((BigDecimal) params.get("overdue")).subtract((BigDecimal) params.get("realRepayMoney")).subtract((BigDecimal) params.get("realRepayInterest"));
        params.put("amount", amount.toString());
        params.put("payType", payType);
        if ("2".equals(payType)) {
            String accNo = (String) params.get("bankCardNo");
            if (StringUtils.isEmpty(accNo)) return new BaseResult(17893, "卡号不能为空");
            params.put("accNo", accNo);
            params.put("customKey","reqReserved");
            params.put("customVal", custom);
        } else if ("0".equals(payType)) {
            params.put("customKey","body");
            params.put("customVal", custom);
        } else if ("4".equals(payType)) {
            params.put("customKey","reqReserved");
            params.put("customVal", custom);
        }
        BaseResult baseResult = Pay.recharge(params);
        return baseResult;
    }

    public BaseResult freeRepay(String payType, Map<String, Object> params) {
        //支付
        String dateStr = (String) params.get("date");
        String custom = "freeRepay" + Table.SEPARATE_SPLIT + params.get("userId") + Table.SEPARATE_SPLIT + dateStr;
        Calendar calendar = Calendar.getInstance();
        params.put("orderNo", dateStr.replace(Table.SEPARATE_CACHE,"") + "Z" + calendar.getTimeInMillis());
        String amount = (String) params.get("amount");
        params.put("subject", "融宝儿分期购物对" + dateStr.replace("-", "年") + "月的账单还款(" + amount + ")");
        params.put("payType", payType);
        if ("2".equals(payType)) {
            String accNo = (String) params.get("bankCardNo");
            if (StringUtils.isEmpty(accNo)) return new BaseResult(17893, "卡号不能为空");
            params.put("accNo", accNo);
            params.put("customKey","reqReserved");
            params.put("customVal", custom);
        } else if ("0".equals(payType)) {
            params.put("customKey","body");
            params.put("customVal", custom);
        } else if ("4".equals(payType)) {
            params.put("customKey","reqReserved");
            params.put("customVal", custom);
        }
        BaseResult baseResult = Pay.recharge(params);
        return baseResult;
    }

    public BaseResult refund(String payType, Map<String, Object> params) {
        params.put("payType", payType);
        return Pay.refund(params);
    }

    public static void submitTask(Runnable task, long delay) {
        scheduPool.schedule(task, delay, TimeUnit.MILLISECONDS);
    }

    @Transactional(rollbackFor = Exception.class)
    public BaseResult addOrder(Object uId, String reqIp, ParamsVo paramsVo) {
        ParamsMap<String, Object> order = paramsVo.getParams();
        int userId = Integer.parseInt(String.valueOf(order.get(Table.USER_ID)));
        List<Map<String, Object>> countList = baseDao.queryByS("select sum(SUM) cou from fq.ORDER where USER_ID=" + userId + " and STATE not in ('8','9','10')");
        if (!CollectionUtils.isEmpty(countList) && !CollectionUtils.isEmpty(countList.get(0)) && Integer.parseInt(String.valueOf(countList.get(0).get("cou"))) >= 3)
            return new BaseResult(ReturnCode.ORDER_LIMIT_SIZE);
        String orderNo = IBase.sdf.format(new Date());
        order.addParams(Table.Order.ORDER_NO.name(), orderNo);
        order.addParams(Table.Order.USER_ID.name(), userId);
        order.addParams(Table.Order.STATE.name(), "0");
        order.addParams(Table.Order.REQ_IP.name(), reqIp);
        order.addParams(Table.Order.UP_ID.name(), uId);
        final List<Map> itemList = JSON.parseArray((String) order.get(Table.Order.ITEMS.name()), Map.class);
        BigDecimal orderMoney = BigDecimal.ZERO;
        BigDecimal money = BigDecimal.ZERO;
        BigDecimal firstMoney = BigDecimal.ZERO;
//        final List<Map<String, Object>> userInsuranceList = new ArrayList<>();
        String coupons = (String) order.get(Table.Order.COUPONS.name());
        Object score=order.get("SCORE");
        Integer scoreNum =StringUtils.isEmpty(score)?0:Integer.parseInt(String.valueOf(score));
        List<Map<String, Object>> couponList = null;
        int sum = 0;
        for (final Map<String, Object> itMap : itemList) {
            List<String> tags = (List<String>) itMap.get("tags");
            boolean isActivity=false;
            if (!CollectionUtils.isEmpty(tags)) {
                for (String tag : tags) {
                    IMarket market = IMarket.marketMap.get(tag);
                    if(Objects.nonNull(market)){
                        market.processActivity(itMap);
                        isActivity=true;
                        break;
                    }
                }
            }
            Map<String, Object> periodMap = (Map<String, Object>) itMap.get("period");
            BigDecimal productPrice = new BigDecimal(String.valueOf(itMap.get("price")));
            sum += Integer.parseInt(String.valueOf(itMap.get("size")));
            money = money.add(productPrice);
            BigDecimal insureMoney = BigDecimal.ZERO;
            Object insureId = periodMap.get("insure");
            if (!StringUtils.isEmpty(insureId)) {
                Map<String, Object> insureMap = baseDao.queryByIdInTab(Table.FQ + Table.PRODUCT_INSURANCE, insureId);
                if(!CollectionUtils.isEmpty(insureMap))
                insureMoney = BigDecimal.ZERO.add((BigDecimal) insureMap.get("insurMoney"));
            }
            BigDecimal rateVal = new BigDecimal(String.valueOf(periodMap.get("rate")));
            if (!StringUtils.isEmpty(coupons)) {
                couponList = baseDao.queryByS("SELECT c.ID c_ID,cd.ID cd_ID,cd.COUPON_AMOUNT,cd.COND,cd.COUPON_TYPE,cd.COUPON_NAME,c.STATUS,c.REMARK FROM COUPON c JOIN COUPON_DICT cd ON c.COUPON_ID=cd.ID WHERE c.STATUS='2' AND c.USER_ID=1106 AND cd.EXPIRE_DATE>current_timestamp() AND c.ID in (" + coupons + ") AND cd.IS_ENABLE=1");
                for (Iterator<Map<String, Object>> it = couponList.iterator(); it.hasNext(); ) {
                    Map<String, Object> couMap = it.next();
                    String couponType = (String) couMap.get("COUPON_TYPE");
                    if ("c".equals(couponType)) continue;        //现金券,放在后面计算

                    Map<String, Object> conditionMap = JSON.parseObject((String) couMap.get("COND"), Map.class);
                    Map<String, Object> limitMap = (Map<String, Object>) conditionMap.get("limit");
                    Map<String, Object> discountsMap = (Map<String, Object>) conditionMap.get("discounts");
                    boolean flag = false;
                    String insure = String.valueOf(limitMap.get("insure"));     //产品险ID
                    if (!StringUtils.isEmpty(insure)) {
                        flag = flag && coupons.contains(insure);        //保险--卷
                    }
                    Integer buyNum = (Integer) limitMap.get("buyNum");
                    if (buyNum != null && buyNum > 0) {
                        List<Map<String, Object>> orderSizeList = baseDao.queryByS("select ID from fq.ORDER where USER_ID=" + userId + " and STATE not in ('8','9','0')");
                        flag = flag && (buyNum == orderSizeList.size() + 1);
                    }
                    Integer periodSize = (Integer) limitMap.get("period");
                    if (periodSize != null && periodSize > 0) {
                        flag = flag && (periodSize == periodMap.get("period"));
                    }
                    String tag = (String) limitMap.get("tag");
                    if(!CollectionUtils.isEmpty(tags)){
                        flag=flag&&tags.contains(tag);
                    }
                    if (flag) {
                        Object rate = discountsMap.get("rate");
                        Object inst = discountsMap.get("inst");
                        Object exemptMon = discountsMap.get("exemptMon");
                        Object giveMon = discountsMap.get("giveMon");
                        if ( !StringUtils.isEmpty(rate)) {
                            BigDecimal rateD = new BigDecimal(String.valueOf(rate));
                            if (rateD.compareTo(BigDecimal.ZERO) == 0) {
                                rateVal = BigDecimal.ZERO;
                            } else {
                                rateVal = rateVal.subtract(rateD);
                            }
                        }
                        if (!StringUtils.isEmpty(inst)) {
                            BigDecimal instD = new BigDecimal(String.valueOf(inst));
                            if (instD.compareTo(BigDecimal.ZERO) == 0) {
                                insureMoney = BigDecimal.ZERO;
                            } else {
                                insureMoney = insureMoney.subtract(instD);
                                if (insureMoney.compareTo(BigDecimal.ZERO) < 0) insureMoney = BigDecimal.ZERO;
                            }
                        }
                        if (!StringUtils.isEmpty(exemptMon)) {
//                        Integer exemptMonI = Integer.parseInt(String.valueOf(exemptMon));
                            periodMap.put("exemptMon", exemptMon);
                        }
                        if(!StringUtils.isEmpty(giveMon)){
                            itMap.put("remark", order.get("REMARK")+"(赠送" + giveMon + "个月话费)");
                        }
                        //使用
                        it.remove();
                        baseDao.updateByProsInTab(Table.FQ + Table.COUPON, ParamsMap.newMap(Table.Coupon.STATUS.name(), "3").addParams(Table.ID, couMap.get("id")));
                    }
                }
            }
            orderMoney = orderMoney.add(productPrice);
            //首付=总额 * 首付比例 + 产品险 + 手续费
            BigDecimal activityMoney= (BigDecimal) itMap.get(IMarket.ACTIVITY_MONEY);
//            BigDecimal payMoney = isActivity&&activityMoney!=null?activityMoney:productPrice.multiply(new BigDecimal(String.valueOf(periodMap.get("firstPayRatio")))).add(insureMoney).add(new BigDecimal(String.valueOf(periodMap.get("fee"))));
            BigDecimal firstPayRatio=new BigDecimal(String.valueOf(periodMap.get("firstPayRatio")));
            BigDecimal payMoney = productPrice.multiply(firstPayRatio).add(insureMoney).add(new BigDecimal(String.valueOf(periodMap.get("fee"))));     //商品首付
//            BigDecimal diffPay=activityMoney!=null&&activityMoney.compareTo(payMoney)>0?activityMoney:payMoney;
            firstMoney = firstMoney.add(payMoney);        //订单首付
            boolean affected=false;
            if(activityMoney!=null){
                firstMoney = firstMoney.add(activityMoney);
                affected = (boolean) itMap.get(IMarket.AFFECTED_FIRST_PAY);
            }
            //月供=（总额 - 首付）/期数 +（总额 - 首付）* 月利率
            BigDecimal remain = productPrice.subtract(payMoney);
            if(affected) remain = remain.subtract(activityMoney);
            BigDecimal MONTHLY = remain.divide(new BigDecimal(String.valueOf(periodMap.get("period"))), 2, BigDecimal.ROUND_HALF_UP).add(remain.multiply(rateVal));
            itMap.put("monthly", MONTHLY.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());       //月供
//            order.addParams(Table.Order.MONTHLY.name(), MONTHLY.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());       //月供
            if(!StringUtils.isEmpty(insureId))
                baseDao.insertByProsInTab(Table.FQ + Table.USER_INSURANCE, ParamsMap.newMap(Table.UserInsurance.INSURANCE_ID.name(), insureId).addParams(Table.UserInsurance.PRODUCT_ID.name(), itMap.get("id"))
                    .addParams(Table.UserInsurance.USER_ID.name(), userId).addParams(Table.UserInsurance.PRODUCT_NAME.name(), itMap.get("name")));       //保险单
        }

/*            Double DISCOUNT = (Double) order.get(Table.Order.DISCOUNT.name());      //折扣
            if (DISCOUNT != null && DISCOUNT > 0) {
                BigDecimal PREFERENTIAL = orderMoney.multiply(new BigDecimal(1.0 - DISCOUNT));            //优惠金额
                orderMoney = orderMoney.subtract(PREFERENTIAL);               //总额-优惠金额
                order.addParams(Table.Order.PREFERENTIAL.name(), PREFERENTIAL.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            }*/
        if (!CollectionUtils.isEmpty(couponList)) {
            String orderMon = orderMoney.toString();
            Optional<Map<String, Object>> optional = couponList.stream().filter((map) -> "c".equals(map.get("COUPON_TYPE")) && String.valueOf(((Map) ((Map) map.get("COND")).get("limit")).get("money")).compareTo(orderMon) > 0).sorted((m1, m2) -> String.valueOf(m1.get("COUPON_AMOUNT")).compareTo(String.valueOf(m2.get("COUPON_AMOUNT")))).findFirst();
            if (optional.isPresent()) {
                orderMoney = orderMoney.subtract((BigDecimal) optional.get().get("COUPON_AMOUNT"));         //优惠券抵扣
                firstMoney = firstMoney.subtract((BigDecimal) optional.get().get("COUPON_AMOUNT"));
            }
        }
        if (scoreNum>0) {     //使用积分抵扣
            Map<String, Object> account = (Map<String, Object>) baseDao.queryByProsInTab(Table.FQ + Table.ACCOUNT, ParamsMap.newMap(Table.Account.USER_ID.name(), userId)).get(0);
            double nn = 1.0;      //积分兑换比例
            BigDecimal SCORE_MONEY = ((BigDecimal) account.get(Table.Account.SCORE_BALANCE.name())).multiply(new BigDecimal(nn));
            firstMoney = firstMoney.subtract(SCORE_MONEY);      //总额-积分抵扣
            orderMoney = orderMoney.subtract(SCORE_MONEY);      //总额-积分抵扣
            order.addParams(Table.Order.SCORE_MONEY.name(), SCORE_MONEY.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            //TODO:积分消费记录
        }
        order.addParams(Table.Order.PAY_MONEY.name(), firstMoney.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());        //首付
        order.addParams(Table.Order.ORDER_MONEY.name(), orderMoney.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        order.addParams(Table.Order.MONEY.name(), money.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        order.addParams(Table.Order.ITEMS.name(), JSON.toJSONString(itemList));
        order.addParams(Table.Order.PRODUCT_NAME.name(), itemList.get(0).get("name"));
        order.addParams(Table.Order.SUM.name(), sum);
        order.addParams(Table.Order.CTIME.name(), new Date());
        int count = baseDao.insertUpdateByProsInTab(tableName, order);
        if (count > 0) {            //付款通知
            String delay = Constants.getSystemStringValue(orderPayRemind);
            String delay2 = Constants.getSystemStringValue(orderCancelRemind);
            OrderService.submitTask(new OrderPayJob(baseDao, userId, orderNo), StringUtils.isEmpty(delay) ? 7200000L : Integer.parseInt(delay));
            OrderService.submitTask(new OrderCancelJob(baseDao, userId, orderNo), StringUtils.isEmpty(delay2) ? 86400000L : Integer.parseInt(delay2));
        }
        return count > 0 ? new BaseResult(ReturnCode.OK, ColumnProcess.encryMap(order)) : new BaseResult(ReturnCode.FAIL);
    }
}
