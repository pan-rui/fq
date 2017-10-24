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
public class RePayRemind {
    @Autowired
    private BaseImpl baseImpl;
    @Autowired
    private CommonDao commonDao;

    public void repayMind() {
        remind("今天",0);
        remind("明天",1);
        Calendar calendar=Calendar.getInstance();
        String interval= Constants.getSystemStringValue("REPAY_REMIND");
        int si=StringUtils.isEmpty(interval) ? 3 : Integer.parseInt(interval);
        calendar.add(Calendar.DAY_OF_MONTH, si);
        remind(IBase.dateSdf.format(calendar.getTime()),si);
    }

    public void remind(final String dateString,int interval) {
        List<Map<String, Object>> list1 = commonDao.repayMind(interval);
        list1.forEach((map)->{
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("orderId", (Long)map.get("orderId"));
            JPushUtil.push("您"+dateString+"有一笔订单需要还款,支付金额为:"+map.get("planrepayMoney")+" 元.","查看详情:",jsonObject,"appMeta");     //TODO:appMeta
        });
    }

}
