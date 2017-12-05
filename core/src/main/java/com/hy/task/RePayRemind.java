package com.hy.task;

import com.google.gson.JsonObject;
import com.hy.base.BaseImpl;
import com.hy.base.IBase;
import com.hy.core.CacheKey;
import com.hy.core.Constants;
import com.hy.core.Table;
import com.hy.dao.CommonDao;
import com.hy.util.JPushUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Component
public class RePayRemind {
    private static final Logger logger = LogManager.getLogger(CouponExpireRemind.class);
    @Autowired
    private BaseImpl baseImpl;
    @Autowired
    private CommonDao commonDao;

//    @Scheduled(cron = "0 0 10 * * *")
    public void repayMind() {
        String remindInterval= Constants.getSystemStringValue("REPAY_REMIND");
        String[] intervalArr = remindInterval.split(Table.SEPARATE_SPLIT);
        for (String str : intervalArr) {
            remind(Integer.parseInt(str));
        }
        remindY();
        logger.info("====================================================RePayRemind==============================================================");
    }

    public void remind(int interval) {
        List<Map<String, Object>> list1 = commonDao.repayMind(interval);
        Calendar calendar=Calendar.getInstance();
        list1.forEach((map)->{
            String appMeta = Constants.hgetCache(CacheKey.APP_META, JPushUtil.USER_APP + map.get(Table.PlanRepayment.USER_ID.name()));
            if(!StringUtils.isEmpty(appMeta)) {
                String title = "您的" + calendar.get(Calendar.MONTH) + "月账单" + map.get(Table.PlanRepayment.PLANREPAY_MONEY.name()) + " 元," + (interval == 0 ? "今天为最后还款日" : "距离还款日还有" + interval + "天,") + "请及时查账及还款.";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("orderId", (Long) map.get(Table.PlanRepayment.ORDER_ID.name()));
                JPushUtil.pushByRegId(JPushUtil.USER_APP, title, "查看详情:", jsonObject, appMeta.split(Table.SEPARATE_SPLIT)[0]);     //TODO:appMeta
            }
        });
    }

    public void remindY() {
        List<Map<String, Object>> list1 = commonDao.repayMind(-1);      //逾期1天
        Calendar calendar=Calendar.getInstance();
        list1.forEach((map)->{
            String appMeta = Constants.hgetCache(CacheKey.APP_META, JPushUtil.USER_APP + map.get(Table.PlanRepayment.USER_ID.name()));
            if(!StringUtils.isEmpty(appMeta)) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("orderId", (Long) map.get(Table.PlanRepayment.ORDER_ID.name()));
                BigDecimal overdue = new BigDecimal(3.0d);
                JPushUtil.pushByRegId(JPushUtil.USER_APP, "您的" + calendar.get(Calendar.MONTH) + "月账单" + (overdue.add((BigDecimal) map.get(Table.PlanRepayment.PLANREPAY_MONEY.name()))) + " 元未按时还款,请您及时还清", "查看详情:", jsonObject, appMeta.split(Table.SEPARATE_SPLIT)[0]);     //TODO:appMeta
            }
        });
    }
}
