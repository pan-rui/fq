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

import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Component
public class CouponExpireRemind {
    private static final Logger logger = LogManager.getLogger(CouponExpireRemind.class);
    @Autowired
    private BaseImpl baseImpl;
    @Autowired
    private CommonDao commonDao;

//    @Scheduled(cron = "0 30 10 * * *")
    public void useMind() {
        remind("今天",0);
/*        Calendar calendar=Calendar.getInstance();
        String interval= Constants.getSystemStringValue("COUPON_EXPIRE_REMIND");
        int si=StringUtils.isEmpty(interval) ? 3 : Integer.parseInt(interval);
        calendar.add(Calendar.DAY_OF_MONTH, si);
        remind(IBase.dateSdf.format(calendar.getTime()),si);*/
        logger.info("=========================================CouponExpireRemind===========================================================================");
    }

    public void remind(final String dateString,int interval) {
        List<Map<String, Object>> list1 = commonDao.queryCouponMindMul(interval);
        list1.forEach((map)->{
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("couponId", (Long)map.get("c_id"));
            Object userId=map.get("c_userId");
            String appMeta=Constants.hgetCache(CacheKey.APP_META,JPushUtil.USER_APP+userId);
            //您的优惠券还有3天就失效，再不用就要错失一大波优惠啦
            if(!StringUtils.isEmpty(appMeta))
                JPushUtil.submitTask(()->JPushUtil.pushByRegId(JPushUtil.USER_APP+userId,"NOTIFY","优惠券过期提醒","您有超值优惠券今天到期,快去用掉吧!",jsonObject,appMeta.split(Table.SEPARATE_SPLIT)[0]));     //TODO:appMeta
        });
    }
//"您有一张"+map.get("cd_couponAmount")+"元的优惠券将在"+dateString+" 过期,请尽快使用."
}
