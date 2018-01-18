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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class RePayRemind {
    private static final Logger logger = LogManager.getLogger(RePayRemind.class);
    @Autowired
    private BaseImpl baseImpl;
    @Autowired
    private CommonDao commonDao;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy年MM月");

//    @Scheduled(cron = "0 0 10 * * *")
    public void repayMind() {
        String remindInterval= Constants.getSystemStringValue("REPAY_REMIND");
        String overdue= Constants.getSystemStringValue("OVERDUE_REMIND");
        String[] intervalArr = remindInterval.split(Table.SEPARATE_SPLIT);
        for (String str : intervalArr) {
            remind(Integer.parseInt(str));
        }
        String[] interval2Arr = overdue.split(Table.SEPARATE_SPLIT);
        for (String str : interval2Arr) {
            remindY(Integer.parseInt(str));
        }
        logger.info("====================================================RePayRemind==============================================================");
    }

    public void remind(int interval) {
        List<Map<String, Object>> list1 = commonDao.repayMind(interval);
        Calendar calendar=Calendar.getInstance();
        list1.forEach((map)->{
            Object userId= map.get(Table.PlanRepayment.USER_ID.name());
            String appMeta = Constants.hgetCache(CacheKey.APP_META, JPushUtil.USER_APP +userId);
            if(!StringUtils.isEmpty(appMeta)) {
                Date date= (Date) map.get(Table.PlanRepayment.PLANREPAY_DATE.name());
//                String title = "您的" + calendar.get(Calendar.MONTH) + "月账单" + map.get(Table.PlanRepayment.PLANREPAY_MONEY.name()) + " 元," + (interval == 0 ? "今天为最后还款日" : "距离还款日还有" + interval + "天,") + "请及时查账及还款.";
                String[] content = {"距离您账单" + dateFormat.format(date) + "的最后还款日还有" + interval + "天,请您留意是否进行还款."};
                if(interval==0)
                    content[0] = "今天是您账单" + dateFormat.format(date) + "的最后还款日,请您留意是否进行还款.";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("orderId", (Long) map.get(Table.PlanRepayment.ORDER_ID.name()));
                calendar.setTime(date);
                JPushUtil.submitTask(()->JPushUtil.pushByRegId(JPushUtil.USER_APP+userId,"NOTIFY",calendar.get(Calendar.MONTH)+"月账单还款通知" , content[0], jsonObject, appMeta.split(Table.SEPARATE_SPLIT)[0]));     //TODO:appMeta
            }
        });
    }

    public void remindY(int interval) {
        List<Map<String, Object>> list1 = commonDao.repayMind(-interval);      //逾期1天
        Calendar calendar=Calendar.getInstance();
        list1.forEach((map)->{
            Object userId= map.get(Table.PlanRepayment.USER_ID.name());
            String appMeta = Constants.hgetCache(CacheKey.APP_META, JPushUtil.USER_APP + userId);
            if(!StringUtils.isEmpty(appMeta)) {
                Date date= (Date) map.get("planrepayDate");
                calendar.setTime(date);
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("orderId", (Long) map.get(Table.PlanRepayment.ORDER_ID.name()));
//                BigDecimal overdue = new BigDecimal(3.0d);
                JPushUtil.submitTask(()->JPushUtil.pushByRegId(JPushUtil.USER_APP+userId,"NOTIFY", calendar.get(Calendar.MONTH)+"月账单逾期通知","您" + dateFormat.format(date) + "的账单已经逾期"+interval+"天,请您及时还款." , jsonObject, appMeta.split(Table.SEPARATE_SPLIT)[0]));     //TODO:appMeta
//                JPushUtil.pushByRegId(JPushUtil.USER_APP, "您" + dateFormat.format(date) + "的账单已经逾期" + (overdue.add((BigDecimal) map.get(Table.PlanRepayment.PLANREPAY_MONEY.name()))) + " 元未按时还款,请您及时还清", "查看详情:", jsonObject, appMeta.split(Table.SEPARATE_SPLIT)[0]);     //TODO:appMeta
            }
        });
    }
}
