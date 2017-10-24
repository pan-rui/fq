package com.hy.task;

import com.google.gson.JsonObject;
import com.hy.base.BaseImpl;
import com.hy.base.IBase;
import com.hy.core.Constants;
import com.hy.dao.CommonDao;
import com.hy.util.JPushUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Component
public class CouponExpireRemind {
    @Autowired
    private BaseImpl baseImpl;
    @Autowired
    private CommonDao commonDao;

    public void repayMind() {
        remind("今天",0);
        Calendar calendar=Calendar.getInstance();
        String interval= Constants.getSystemStringValue("COUPON_EXPIRE_REMIND");
        int si=StringUtils.isEmpty(interval) ? 3 : Integer.parseInt(interval);
        calendar.add(Calendar.DAY_OF_MONTH, si);
        remind(IBase.dateSdf.format(calendar.getTime()),si);
    }

    public void remind(final String dateString,int interval) {
        List<Map<String, Object>> list1 = commonDao.couponMind(interval);
        list1.forEach((map)->{
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("couponId", (Long)map.get("c_id"));
            JPushUtil.push("您有一张"+map.get("cd_couponAmount")+"元的优惠券将在"+dateString+" 过期,请尽快使用.","前往使用:",jsonObject,"appMeta");     //TODO:appMeta
        });
    }

}
