package com.hy.service;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonObject;
import com.hy.base.BaseImpl;
import com.hy.base.BaseResult;
import com.hy.base.IBase;
import com.hy.base.ReturnCode;
import com.hy.core.CacheKey;
import com.hy.core.Constants;
import com.hy.core.ParamsMap;
import com.hy.core.Table;
import com.hy.dao.BaseDao;
import com.hy.dao.ProductDao;
import com.hy.dao.UserDao;
import com.hy.service.pay.IPaymentService;
import com.hy.util.FtpUtil;
import com.hy.util.HttpUtil;
import com.hy.util.ImgUtil;
import com.hy.util.JPushUtil;
import com.hy.util.JTUtil;
import com.hy.util.SendMail;
import com.hy.vo.RemoteProtocol;
import org.apache.commons.lang.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class CommonService {
    @Autowired
    private BaseImpl baseImpl;
    @Autowired
    private BaseDao baseDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ProductDao productDao;
    private String userTable = Table.FQ + Table.USER;
    private String saleTable = Table.FQ + Table.EMPLOYEE;
    @Autowired
    private DataSourceTransactionManager transactionManager;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy年MM月");
    private static final Logger logger = LogManager.getLogger(CommonService.class);

    public BaseResult uploadImg(String appVer, String userId, String phone, MultipartFile file, String fileType) throws IOException {
//        if(StringUtils.isEmpty(phone)) return new BaseResult(ReturnCode.HEADER_PARAMS_VERIFY_ERROR);
        String fileName = file.getOriginalFilename();
        boolean isUser = appVer.startsWith(Constants.USER);
        String path = (isUser ? ImgUtil.USER_IMG_PATH : ImgUtil.SALE_IMG_PATH) + phone + "/" + fileType + "/" + UUID.randomUUID().toString() + "." + fileName.split("\\.")[1];
        File newFile = new File(ImgUtil.BASE_PATH + path);
        if (!newFile.getParentFile().exists()) newFile.getParentFile().mkdirs();
        file.transferTo(newFile);
        ParamsMap paramsMap = ParamsMap.newMap(Table.UserAttach.ATTACH_TYPE.name(), fileType).addParams(Table.UserAttach.URL.name(), path).addParams(Table.UserAttach.ATTACH_LEN.name(), newFile.length())
                .addParams(Table.UserAttach.UP_ID.name(), userId).addParams(Table.UserAttach.USER_ID.name(), userId).addParams(Table.UserAttach.IS_ENABLE.name(), 1);
        if ("TX".equals(fileType)) {
            baseDao.updateByProsInTab(isUser ? userTable : saleTable, ParamsMap.newMap(Table.User.OSS_ID.name(), path).addParams(Table.ID, userId));
            paramsMap.addParams(Table.UserAttach.ATTACH_TYPE.name(), "b");
        }
        baseDao.insertUpdateByProsInTab(Table.FQ + Table.USER_ATTACH, paramsMap);
        return new BaseResult(ReturnCode.OK, path);
    }

    public BaseResult uploadApk(String appType, MultipartFile file, String appVer, String upContent, String updateCount) throws IOException {
//        if(StringUtils.isEmpty(phone)) return new BaseResult(ReturnCode.HEADER_PARAMS_VERIFY_ERROR);
        String fileName = file.getOriginalFilename();
        String path = ImgUtil.APK_PATH + "/" + fileName;
        File newFile = new File(ImgUtil.BASE_PATH + path);
        if (!newFile.getParentFile().exists()) newFile.getParentFile().mkdirs();
        file.transferTo(newFile);
        int count = baseDao.insertByProsInTab(Table.FQ + Table.APP_VESION, ParamsMap.newMap(Table.AppVesion.APP_TYPE.name(), appType)
                .addParams(Table.AppVesion.VERSION.name(), appVer).addParams(Table.AppVesion.UPDATE_CONTENT.name(), upContent).addParams(Table.AppVesion.FILE_SIZE.name(), newFile.length()).addParams(Table.AppVesion.UPDATE_COUNT.name(), updateCount));
        return count > 0 ? new BaseResult(ReturnCode.OK, path) : new BaseResult(ReturnCode.FAIL);
    }

    @CacheEvict(value = "tmp", allEntries = true, cacheManager = "cacheManager")
    public void clearTmpCache() {
        System.out.println("清除Tmp缓存....");
    }

    @CacheEvict(value = "system", allEntries = true, cacheManager = "cacheManager")
    public void resetSystemCache() {
        baseImpl.initSystemData();
        System.out.println("重围system缓存....");
    }

    @Cacheable(value = "common", key = "'allLevel_'+#tableName")
    public List<Map<String, Object>> allMultilLevel(String tableName, ParamsMap<String, Object> params, String childrenField) {
        final List<Map<String, Object>> datas = baseDao.queryListInTab(tableName, params, null);
        List<Map<String, Object>> resultList = new LinkedList<Map<String, Object>>();
        for (Map<String, Object> data : datas) {
            Long parentId = StringUtils.isEmpty(data.get("parentId")) ? 0L : Long.parseLong(String.valueOf(data.get("parentId")));
            if (parentId != 0L) {
                for (Map<String, Object> pData : datas)
                    if (parentId.equals(pData.get("id"))) {
                        List<Map<String, Object>> children = (List<Map<String, Object>>) pData.get(childrenField);
                        if (children == null) {
                            children = new LinkedList<Map<String, Object>>();
                            pData.put(childrenField, children);                //区别所在
                        }
                        children.add(data);
                    }
            } else resultList.add(data);
        }
        return resultList;
    }

    public boolean paySuccessCallBack(Map params, String orderNo, String payNo, String payTime, String custom, String amount, String payType) {
        boolean flag = true;
//        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
//        def.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
//        TransactionStatus transStatus = transactionManager.getTransaction(def);
        String[] bodyArr = ((String) params.remove(custom)).split(Table.SEPARATE_SPLIT);
        String tradeType = bodyArr[0];
        try {
//            transactionManager.commit(transStatus);
            String payInfo=JSON.toJSONString(params);
            if (IPaymentService.TradeType.firstPay.name().equals(tradeType)) {
                //更新订单信息及相关信息
                //保存交易记录
                ParamsMap paramsMap = ParamsMap.newMap(Table.Order.STATE.name(), "1").addParams(Table.Order.PAY_TIME.name(), payTime).addParams(Table.Order.PAY_NO.name(), payNo).addParams(Table.Order.PAY_INFO.name(), payInfo);
                baseDao.insertByProsInTab(Table.FQ + Table.TRADE_RECORD, ParamsMap.newMap(Table.TradeRecord.TRADE_TYPE.name(), tradeType).addParams(Table.TradeRecord.ACCT_TIME.name(), payTime).addParams(Table.USER_ID, bodyArr[1]).addParams(Table.TradeRecord.TRADE_AMOUNT.name(), amount)
                        .addParams(Table.TradeRecord.PAY_TYPE.name(), payType).addParams(Table.TradeRecord.ORDER_NO.name(), orderNo).addParams(Table.TradeRecord.TRADE_NO.name(), payNo).addParams(Table.TradeRecord.PAY_INFO.name(), payInfo));
                baseDao.updateByProsInTab(Table.FQ + Table.ORDER, paramsMap.addParams(Table.ID, bodyArr[2]));
                Map<String, Object> orderMap = baseDao.queryByIdInTab(Table.FQ + Table.ORDER, bodyArr[2]);
                flag = genPlanRepay(orderMap);
                Map<String, Object> userMap = baseDao.queryByIdInTab(userTable, bodyArr[1]);
                Object inviterId = userMap.get("inviterId");
                if (!StringUtils.isEmpty(inviterId)) {
                    baseDao.insertByProsInTab(Table.FQ + Table.COUPON, ParamsMap.newMap(Table.Coupon.STATUS.name(), "2").addParams(Table.Coupon.REMARK.name(), bodyArr[1]).addParams(Table.Coupon.COUPON_ID.name(), "7").addParams(Table.Coupon.USER_ID.name(), inviterId));
                }
                sendWxMsg(RemoteProtocol.PAY_SUCCESS_MSG, ParamsMap.newMap("amount", amount + "元").addParams("user", userMap).addParams("order", orderMap));
            } else if (IPaymentService.TradeType.repay.name().equals(tradeType)) {
                baseDao.insertByProsInTab(Table.FQ + Table.TRADE_RECORD, ParamsMap.newMap(Table.TradeRecord.TRADE_TYPE.name(), tradeType).addParams(Table.TradeRecord.ACCT_TIME.name(), payTime).addParams(Table.USER_ID, bodyArr[1]).addParams(Table.TradeRecord.TRADE_AMOUNT.name(), amount)
                        .addParams(Table.TradeRecord.PAY_TYPE.name(), payType).addParams(Table.TradeRecord.BILL_DATE.name(), bodyArr[2]).addParams(Table.TradeRecord.ORDER_NO.name(), orderNo).addParams(Table.TradeRecord.TRADE_NO.name(), payNo)
                        .addParams(Table.TradeRecord.PAY_INFO.name(), JSON.toJSONString(params)));
                Map<String, Object> repayMap = baseDao.queryByIdInTab(Table.FQ + Table.PLAN_REPAYMENT, bodyArr[2]);
                long diffSecond = System.currentTimeMillis() - ((Date) repayMap.get("planrepayDate")).getTime();
                baseDao.updateByProsInTab(Table.FQ + Table.PLAN_REPAYMENT, ParamsMap.newMap(Table.PlanRepayment.STATUS.name(), diffSecond > 0 ? "1" : "2").addParams(Table.PlanRepayment.PAY_DATE, new Date()).addParams(Table.ID, bodyArr[2]));
                String appMeta = Constants.hgetCache(CacheKey.APP_META, JPushUtil.USER_APP + bodyArr[1]);
                if (repayMap.get("periodSum") == repayMap.get("repayNum")) {
                    Long orderId = (Long) repayMap.get("orderId");
                    baseDao.updateByProsInTab(Table.FQ + Table.ORDER, ParamsMap.newMap(Table.Order.STATE.name(), "10").addParams(Table.ID, orderId));           //更新订单状态
                    if (!StringUtils.isEmpty(appMeta)) {
                        Map<String, Object> productMap = baseDao.queryByIdInTab(Table.FQ + Table.PRODUCT, repayMap.get("productId"));
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("orderId", orderId);
                        jsonObject.addProperty("payNo", payNo);
//                            JPushUtil.submitTask(()->JPushUtil.pushByRegId(JPushUtil.USER_APP, "您有一笔融宝分期订单已全部还款完成.", "点击查看详情!", jsonObject, appMeta.split(Table.SEPARATE_SPLIT)[0]));
                        JPushUtil.submitTask(() -> JPushUtil.pushByRegId(JPushUtil.USER_APP+bodyArr[1],"TRADE", "分期结束通知", "您购买的商品【" + productMap.get("name") + "】已完成" + repayMap.get("periodSum") + "期分期.", jsonObject, appMeta.split(Table.SEPARATE_SPLIT)[0]));
                    }
                }
                if (!StringUtils.isEmpty(appMeta)) {
                    Calendar calendar = Calendar.getInstance();
                    Date date = (Date) repayMap.get("planrepayDate");
                    calendar.setTime(date);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("repayId", bodyArr[2]);
                    jsonObject.addProperty("payNo", payNo);
//                    JPushUtil.submitTask(()->JPushUtil.pushByRegId(JPushUtil.USER_APP, "您" + calendar.get(Calendar.MONTH) + "月账单" + repayMap.get("planrepayMoney") + "元已还清!", "点击查看详情:", jsonObject, appMeta.split(Table.SEPARATE_SPLIT)[0]));   //TODO:AppMeta
                    JPushUtil.submitTask(() -> JPushUtil.pushByRegId(JPushUtil.USER_APP+bodyArr[1],"TRADE", calendar.get(Calendar.MONTH) + "月账单还清通知", "您已按期还清" + dateFormat.format(date) + "账单" + repayMap.get("planrepayMoney") + "元,请继续保持哦!", jsonObject, appMeta.split(Table.SEPARATE_SPLIT)[0]));   //TODO:AppMeta
                }

            } else if (IPaymentService.TradeType.freeRepay.name().equals(tradeType)) {
                Calendar calendar = Calendar.getInstance();
                String[] dateArr = bodyArr[2].split(Table.SEPARATE_CACHE);
                baseDao.insertByProsInTab(Table.FQ + Table.TRADE_RECORD, ParamsMap.newMap(Table.TradeRecord.TRADE_TYPE.name(), "freeRepay").addParams(Table.TradeRecord.ACCT_TIME.name(), payTime).addParams(Table.USER_ID, bodyArr[1])
                        .addParams(Table.TradeRecord.TRADE_AMOUNT, amount).addParams(Table.TradeRecord.PAY_TYPE.name(), payType)
                        .addParams(Table.TradeRecord.BILL_DATE.name(), new Calendar.Builder().setDate(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]) - 1, calendar.get(Calendar.DAY_OF_MONTH)).build().getTime())
                        .addParams(Table.TradeRecord.ORDER_NO.name(), orderNo).addParams(Table.TradeRecord.TRADE_NO.name(), payNo).addParams(Table.TradeRecord.PAY_INFO.name(), JSON.toJSONString(params)));
                List<Map<String, Object>> repayList = userDao.queryRepaysTab(bodyArr[1], bodyArr[2]);
//                        Calendar calendar = Calendar.getInstance();
                BigDecimal money = new BigDecimal(amount);
                List<Map<String, Object>> calcList = new ArrayList<>(repayList);
                BigDecimal[] repayMoney = {BigDecimal.ZERO};
                for (Iterator<Map<String, Object>> it = calcList.iterator(); it.hasNext(); ) {
                    Map<String, Object> repayMap = it.next();
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
                            it.remove();
                            repayMoney[0] = repayMoney[0].add(syRepay);
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
                                it.remove();
                                repayMoney[0] = repayMoney[0].add(syRepay);
                            } else {
                                upMap.addParams(Table.PlanRepayment.REAL_REPAY_MONEY.name(), realRepayMoney.add(money));
                                money = BigDecimal.ZERO;
                            }
                            repayMoney[0] = repayMoney[0].add(syRepayOve);
                        } else {      //少于利息
                            upMap.addParams(Table.PlanRepayment.REAL_REPAY_INTEREST.name(), realRepayInterest.add(money));
                            money = BigDecimal.ZERO;
                        }
                    }
                    baseDao.updateByProsInTab(Table.FQ + Table.PLAN_REPAYMENT, upMap.addParams(Table.ID, repayMap.get("id")));
                }
                if (CollectionUtils.isEmpty(calcList)) {
                    String appMeta = Constants.hgetCache(CacheKey.APP_META, JPushUtil.USER_APP + bodyArr[1]);
                    JsonObject jsonObject = new JsonObject();
                    if (!StringUtils.isEmpty(appMeta)) {
                        jsonObject.addProperty("repayId", bodyArr[2]);
                        jsonObject.addProperty("payNo", payNo);
//                    JPushUtil.submitTask(()->JPushUtil.pushByRegId(JPushUtil.USER_APP, "您" + calendar.get(Calendar.MONTH) + "月账单" + repayMap.get("planrepayMoney") + "元已还清!", "点击查看详情:", jsonObject, appMeta.split(Table.SEPARATE_SPLIT)[0]));   //TODO:AppMeta
                        JPushUtil.submitTask(() -> JPushUtil.pushByRegId(JPushUtil.USER_APP+bodyArr[1], "TRADE",dateArr[1] + "月账单还清通知", "您已按期还清" + dateArr[0] + "年" + dateArr[1] + "月账单" + repayMoney[0].toString() + "元,请继续保持哦!", jsonObject, appMeta.split(Table.SEPARATE_SPLIT)[0]));   //TODO:AppMeta
                    }
                    List<Map<String, Object>> repayList2 = userDao.queryUserRepaysTab(bodyArr[1], repayList);
                    if (repayList2.isEmpty()) {
                        Map<String, Object> productMap = baseDao.queryByIdInTab(Table.FQ + Table.PRODUCT, repayList.get(0).get("productId"));
                        JPushUtil.submitTask(() -> JPushUtil.pushByRegId(JPushUtil.USER_APP+bodyArr[1],"TRADE", "分期结束通知", "您购买的商品【" + productMap.get("name") + "】已完成" + repayList.get(0).get("periodSum") + "期分期.", jsonObject, appMeta.split(Table.SEPARATE_SPLIT)[0]));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
//            transactionManager.rollback(transStatus);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return flag;
    }

    @Transactional
    public boolean genPlanRepay(Map<String, Object> orderMap) {
        String items = (String) orderMap.get("items");
        Long uId = (Long) orderMap.get("userId");
        org.springframework.util.Assert.notNull(items, "未提交购买的商品(items");
        try {
            List<Map> itemList = JSON.parseArray(items, Map.class);
            for (Map itMap : itemList) {
//                    Map<String, Object> periodMap = JSON.parseObject((String) orderMap.get("period"));
                Map<String, Object> periodMap = (Map<String, Object>) itMap.get("period");
                List<Integer> exemptMonList = (List<Integer>) periodMap.get("exemptMon");
                Object exemptMoney = periodMap.get("exemptMoney");
                Object addMonthlyObj = periodMap.get("addMonthly");
//                Integer exemptMon = StringUtils.isEmpty(exemptMonObj)?null:Integer.parseInt(String.valueOf(exemptMonObj));
                BigDecimal addMonthly = StringUtils.isEmpty(addMonthlyObj) ? null : new BigDecimal(String.valueOf(addMonthlyObj));
                BigDecimal monthly = new BigDecimal(itMap.get("monthly").toString());
                if (addMonthly != null)
                    monthly = monthly.add(addMonthly);
                Long userId = (Long) orderMap.get("userId");
                int period = Integer.parseInt(String.valueOf(periodMap.get("period")));
                Calendar calendar = Calendar.getInstance();
                List<Map<String, Object>> srcList = new ArrayList<>();
                String repayDate = Constants.getSystemStringValue("REPAY_DATE");
                if (!StringUtils.isEmpty(repayDate) && !"0".equals(repayDate.trim()) && NumberUtils.isNumber(repayDate))
                    calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(repayDate));
                for (int i = 1; i <= period; i++) {
                    calendar.add(Calendar.MONTH, 1);
                    ParamsMap planRepayMap = ParamsMap.newMap(Table.PlanRepayment.USER_ID.name(), uId)
                            .addParams(Table.PlanRepayment.ORDER_ID.name(), orderMap.get("id"))
                            .addParams(Table.PlanRepayment.PRODUCT_ID.name(), periodMap.get("productId"))
                            .addParams(Table.PlanRepayment.PLANREPAY_DATE.name(), calendar.getTime())
                            .addParams(Table.PlanRepayment.PLANREPAY_MONEY.name(), monthly)
                            .addParams(Table.PlanRepayment.REAL_REPAY_MONEY.name(), BigDecimal.ZERO)
                            .addParams(Table.PlanRepayment.STATUS.name(), "0")      //0未还
                            .addParams(Table.PlanRepayment.REPAY_NUM.name(), i)
                            .addParams(Table.PlanRepayment.PERIOD_SUM.name(), period)
                            .addParams(Table.PlanRepayment.REPAY_NO.name(), IBase.sdf.format(calendar.getTime()))
                            .addParams(Table.PlanRepayment.UP_ID.name(), uId);
                    if (i == 1 && exemptMoney != null) {
                        planRepayMap.addParams(Table.PlanRepayment.REAL_REPAY_MONEY.name(), new BigDecimal(String.valueOf(exemptMoney)));
                    }
//                    if(exemptMon!=null && i<=exemptMon){
                    if (!CollectionUtils.isEmpty(exemptMonList)) {
                        if (exemptMonList.contains(i))
                            planRepayMap.addParams(Table.PlanRepayment.STATUS.name(), "4").addParams(Table.PlanRepayment.REPAY_NO.name(), null);
                    }
                    srcList.add(planRepayMap);
                }
                int count = baseDao.insertBatchByProsInTab(Table.FQ + Table.PLAN_REPAYMENT, srcList);
                if (count < period)
                    logger.error("ERROR:还款计划生成有误。。。。");
                //TODO:销量
                Object productId = itMap.get("id");
                Object storeId = itMap.get("storeId");
                Map<String, Object> productMap = baseDao.queryByIdInTab(Table.FQ + Table.PRODUCT, productId);
                String attJson = JSON.toJSONString(mergeData(JSON.parseArray((String) productMap.get("attJson"), Map.class), itMap, "attr", "attGroup"));
                productDao.incrSales((int) itMap.get("size"), attJson, productId, storeId);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("支付销量更新异常:" + items, e);
            throw new RuntimeException(e);
        }
    }

    public static List<Map> mergeData(final List<Map> dataList, Map<String, Object> dataMap, String diffMapField, String diffListField) {
        Map<String, Object> attG = (Map<String, Object>) dataMap.get(diffMapField);
        a:
        for (Map<String, Object> map : dataList) {
            Map<String, Object> att = (Map<String, Object>) map.get(diffListField);
            int i = 0;
//            if (attG.size() == att.size()) {
            b:
            for (String key : att.keySet()) {
                if (!attG.containsKey(key) || !attG.get(key).equals(att.get(key))) {
                    break b;
                }
                i++;
            }
//            }
            if (i == att.size()) {
                int size = Integer.parseInt(String.valueOf(dataMap.get("size")));
                map.put("sold", (int) map.get("sold") + size);
                map.put("stock", (int) map.get("stock") - size);
            }
        }
        return dataList;
    }

    public void sendWxMsg(RemoteProtocol protocol, Map<String, Object> params) {
        Map<String, Object> userMap = (Map<String, Object>) params.get("user");
        if (StringUtils.isEmpty(userMap.get("openId"))) {

        } else {
            BaseResult baseResult = JSON.parseObject(HttpUtil.execute(protocol, ParamsMap.newMap("params", params)), BaseResult.class);
            if (baseResult.getCode() != 0) {
                try {
                    TimeUnit.MINUTES.sleep(5L);
                    baseResult = JSON.parseObject(HttpUtil.execute(protocol, ParamsMap.newMap("params", params)), BaseResult.class);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            logger.info(baseResult.toString());
        }
    }
}
