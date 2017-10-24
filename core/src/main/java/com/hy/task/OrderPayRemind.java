package com.hy.task;

import com.google.gson.JsonObject;
import com.hy.core.Constants;
import com.hy.core.Table;
import com.hy.dao.BaseDao;
import com.hy.util.JPushUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Map;

public class OrderPayRemind implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Map<String, Object> dataMap = context.getJobDetail().getJobDataMap();
        Long orderId = (Long) dataMap.get("orderId");
        Long userId = (Long) dataMap.get("userId");
        String appMeta = (String) dataMap.get("appMeta");       //TODO:AppMeta
        BaseDao baseDao = (BaseDao) dataMap.get("dao");
        Map<String,Object> orderMap=baseDao.queryByIdInTab(Table.FQ + Table.ORDER, orderId);
        if ("0".equals(orderMap.get("state"))) {
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("orderId",orderId);
            jsonObject.addProperty("userId",userId);
            JPushUtil.push("您有一笔订单未付款,订单号为:"+orderMap.get("orderNo"),"待支付金额:"+orderMap.get("orderMoney")+",查看详情",jsonObject ,appMeta);
        }
    }
}
