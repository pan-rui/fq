package com.hy.service.market;

import com.alibaba.fastjson.JSON;
import com.hy.core.ParamsMap;
import com.hy.core.Table;
import com.hy.dao.BaseDao;
import com.hy.dao.CommonDao;
import com.hy.service.IMarket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class Ydtc implements IMarket{
    @Autowired
    private BaseDao baseDao;
    @Autowired
    private CommonDao commonDao;

    private String name = "1005";
    @PostConstruct
    private void init() {
        marketMap.put(name, this);
    }


    @Override
    public Map<String, Object> processActivity(final Map<String, Object> itMap) {
        List<Map<String, Object>> couponMap = baseDao.queryByProsInTab(Table.FQ + Table.COUPON_DICT, ParamsMap.newMap(Table.CouponDict.COUPON_NAME.name(), name));
        if(CollectionUtils.isEmpty(couponMap)) return itMap;
        Map<String, Object> couMap = couponMap.get(0);
        //heyue,zifei,
        Map<String, Object> mMap = (Map<String, Object>) itMap.get(markField);
        String s1007=String.valueOf(mMap.get("1007"));
        String s1008=String.valueOf(mMap.get("1008"));
        BigDecimal activityMoney = new BigDecimal(s1007).multiply(new BigDecimal(s1008));
        itMap.put(ACTIVITY_MONEY, activityMoney);
        itMap.put(AFFECTED_FIRST_PAY, true);
        if(!CollectionUtils.isEmpty(couMap)) {
            Map<String, Object> conditionMap = JSON.parseObject((String) couMap.get("cond"), Map.class);
                    Map<String, Object> limitMap = (Map<String, Object>) conditionMap.get("limit");
                    Map<String, Object> discountsMap = (Map<String, Object>) conditionMap.get("discounts");
                    Map<String, Object> periodMap = (Map<String, Object>) itMap.get("period");

            switch (Integer.parseInt(s1007)) {
                case 6:
                    limitMap.put("period", 6);
                    discountsMap.put("exemptMon", Arrays.asList(1));
                    couMap.put("cond", JSON.toJSONString(conditionMap));
                    break;
                case 1:
                    discountsMap.put("exemptMon", Arrays.asList());
                    periodMap.put("addMonthly", Integer.parseInt(s1008));
                    couMap.put("cond", JSON.toJSONString(conditionMap));
                    break;
            }
            commonProcess(itMap, baseDao, couMap);
        }
        //TODO:二次购机套餐判断
        return itMap;
    }
}
