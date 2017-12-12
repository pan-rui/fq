package com.hy.task;

import com.google.gson.JsonObject;
import com.hy.core.CacheKey;
import com.hy.core.Constants;
import com.hy.core.ParamsMap;
import com.hy.core.Table;
import com.hy.dao.BaseDao;
import com.hy.service.CommonService;
import com.hy.util.JPushUtil;
import com.hy.vo.RemoteProtocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OrderPayJob implements Job,Runnable {
    private BaseDao baseDao;
    private Number userId;
    private String orderNo;

    public OrderPayJob(){}

    public OrderPayJob(BaseDao dao, Number userId, String orderNo) {
        this.baseDao=dao;
        this.userId=userId;
        this.orderNo=orderNo;
    }

    private static final Logger logger = LogManager.getLogger(OrderPayJob.class);
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("==================================================Start....OrderPayJob===========================================");
        Map<String, Object> dataMap = context.getJobDetail().getJobDataMap();
        String orderNo = (String) dataMap.get(Table.Order.ORDER_NO.name());
        Long userId = (Long) dataMap.get(Table.USER_ID);
        String appMeta = Constants.hgetCache(CacheKey.APP_META, JPushUtil.USER_APP + userId);
        if(StringUtils.isEmpty(appMeta)) return;
        BaseDao baseDao = (BaseDao) dataMap.remove("dao");
        List<Map<String,Object>> orderList=baseDao.queryByProsInTab(Table.FQ + Table.ORDER, dataMap);
        if (!CollectionUtils.isEmpty(orderList) &&"0".equals(orderList.get(0).get("state"))) {
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("orderNo",orderNo);
            jsonObject.addProperty("userId",userId);
            JPushUtil.pushByRegId(JPushUtil.USER_APP,"您有一笔订单未付款,订单号为:"+orderList.get(0).get("orderNo"),"待支付金额:"+orderList.get(0).get("payMoney")+",查看详情",jsonObject ,appMeta.split(Table.SEPARATE_SPLIT)[0]);
        }
    }

    @Override
    public void run() {
        String appMeta = Constants.hgetCache(CacheKey.APP_META, JPushUtil.USER_APP + userId);
        List<Map<String, Object>> orderList = baseDao.queryByProsInTab(Table.FQ + Table.ORDER, ParamsMap.newMap(Table.USER_ID, userId).addParams(Table.Order.ORDER_NO.name(), orderNo));
        if(CollectionUtils.isEmpty(orderList)) return;
        boolean flag=StringUtils.isEmpty(appMeta)||StringUtils.isEmpty(appMeta.split(Table.SEPARATE_SPLIT)[0]);
        if(flag){
            Map<String,Object> userMap=baseDao.queryByIdInTab(Table.FQ + Table.USER, userId);
            String openId= (String) userMap.get("openId");
            if(!StringUtils.isEmpty(openId)){
                CommonService commonService= (CommonService) Constants.applicationContext.getBean("commonService");
                commonService.sendWxMsg(RemoteProtocol.PAY_NOTIFY_MSG, ParamsMap.newMap("params", ParamsMap.newMap("user", userMap).addParams("order", orderList.get(0))));
            }
        }else {
            if ("0".equals(orderList.get(0).get("state"))) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("orderNo", orderNo);
                jsonObject.addProperty("userId", userId);
                JPushUtil.pushByRegId(JPushUtil.USER_APP, "您有一笔订单未付款,订单号为:" + orderList.get(0).get("orderNo"), "待支付金额:" + orderList.get(0).get("orderMoney") + ",查看详情", jsonObject,appMeta.split(Table.SEPARATE_SPLIT)[0]);
            }
        }
    }
}
