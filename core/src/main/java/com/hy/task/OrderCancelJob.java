package com.hy.task;

import com.google.gson.JsonObject;
import com.hy.core.CacheKey;
import com.hy.core.Constants;
import com.hy.core.ParamsMap;
import com.hy.core.Table;
import com.hy.dao.BaseDao;
import com.hy.util.JPushUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

public class OrderCancelJob implements Job,Runnable {
    private BaseDao baseDao;
    private Number userId;
    private String orderNo;

    public OrderCancelJob(){}

    public OrderCancelJob(BaseDao dao, Number userId, String orderNo) {
        this.baseDao=dao;
        this.userId=userId;
        this.orderNo=orderNo;
    }
    private static final Logger logger = LogManager.getLogger(OrderCancelJob.class);
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("==================================================Start....OrderCancelJob===========================================");
        Map<String, Object> dataMap = context.getJobDetail().getJobDataMap();
        String orderNo = (String) dataMap.get(Table.Order.ORDER_NO.name());
        Long userId = (Long) dataMap.get(Table.USER_ID);
        BaseDao baseDao = (BaseDao) dataMap.remove("dao");
        int size=baseDao.updateByProsInTab(Table.FQ + Table.ORDER, ParamsMap.newMap(Table.Order.STATE.name(), "8").addParams(Table.Order.STATE.name(), "0").addParams(Table.Order.ORDER_NO.name(), orderNo), 2);
        if(size>0) {
            String appMeta = Constants.hgetCache(CacheKey.APP_META, JPushUtil.USER_APP + userId);
            if (StringUtils.isEmpty(appMeta)) return;
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("orderNo", orderNo);
                jsonObject.addProperty("userId", userId);
                JPushUtil.pushByRegId(JPushUtil.USER_APP, "您有一笔订单超过24小时未付款,系统已自动取消.","查看详情", jsonObject, appMeta.split(Table.SEPARATE_SPLIT)[0]);
        }
    }

    @Override
    public void run() {
        List<Map<String, Object>> resultList = baseDao.queryByProsInTab(Table.FQ + Table.ORDER, ParamsMap.newMap(Table.Order.STATE.name(), "0").addParams(Table.Order.ORDER_NO.name(), orderNo));
        if(!CollectionUtils.isEmpty(resultList)) {
            int size = baseDao.updateByProsInTab(Table.FQ + Table.ORDER, ParamsMap.newMap(Table.Order.STATE.name(), "8").addParams(Table.Order.ORDER_NO.name(), orderNo));
            if (size > 0) {
                String appMeta = Constants.hgetCache(CacheKey.APP_META, JPushUtil.USER_APP + userId);
                if (StringUtils.isEmpty(appMeta)) return;
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("orderNo", orderNo);
                jsonObject.addProperty("userId", userId);
                JPushUtil.pushByRegId(JPushUtil.USER_APP, "您有一笔订单超过24小时未付款,系统已自动取消.", "查看详情", jsonObject, appMeta.split(Table.SEPARATE_SPLIT)[0]);
            }
        }
    }
}
