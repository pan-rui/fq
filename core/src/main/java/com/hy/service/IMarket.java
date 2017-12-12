package com.hy.service;

import com.alibaba.fastjson.JSON;
import com.hy.core.Table;
import com.hy.dao.BaseDao;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IMarket {
    public static final Map<String, IMarket> marketMap = new HashMap<>();
    public static final String markField = "activity";
    public static final String ACTIVITY_MONEY = "activityMoney";
    public static final String AFFECTED_FIRST_PAY = "affectedFirstPay";

    Map<String, Object> processActivity(final Map<String, Object> itMap);

  default public  Map<String, Object> commonProcess(final Map<String, Object> itMap, BaseDao baseDao,Map<String,Object> couponMap){
      Map<String, Object> periodMap = (Map<String, Object>) itMap.get("period");
      BigDecimal insureMoney = BigDecimal.ZERO;
      Object insureId = periodMap.get("insure");
      if (!StringUtils.isEmpty(insureId)) {
          Map<String, Object> insureMap = baseDao.queryByIdInTab(Table.FQ + Table.PRODUCT_INSURANCE, insureId);
          insureMoney = BigDecimal.ZERO.add((BigDecimal) insureMap.get("insurMoney"));
      }
      BigDecimal rateVal = new BigDecimal(String.valueOf(periodMap.get("rate")));
      Map<String, Object> conditionMap = JSON.parseObject((String) couponMap.get("cond"), Map.class);
      Map<String, Object> limitMap = (Map<String, Object>) conditionMap.get("limit");
      Map<String, Object> discountsMap = (Map<String, Object>) conditionMap.get("discounts");
      Object rate = discountsMap.get("rate");
      Object inst = discountsMap.get("inst");
      List<Integer> exemptMon = (List<Integer>) discountsMap.get("exemptMon");
      Object giveMon = discountsMap.get("giveMon");
      if (!StringUtils.isEmpty(rate)) {
          BigDecimal rateD = new BigDecimal(String.valueOf(rate));
          if (rateD.compareTo(BigDecimal.ZERO) == 0) {
              rateVal = BigDecimal.ZERO;
          } else {
              rateVal = rateVal.subtract(rateD);
          }
      }
      if (!StringUtils.isEmpty(inst)) {
          BigDecimal instD = new BigDecimal(String.valueOf(inst));
          if (instD.compareTo(BigDecimal.ZERO) == 0) {
              insureMoney = BigDecimal.ZERO;
          } else {
              insureMoney = insureMoney.subtract(instD);
              if (insureMoney.compareTo(BigDecimal.ZERO) < 0) insureMoney = BigDecimal.ZERO;
          }
      }
      if (!CollectionUtils.isEmpty(exemptMon)) {
//                        Integer exemptMonI = Integer.parseInt(String.valueOf(exemptMon));
          periodMap.put("exemptMon", exemptMon);
      }
      if(!StringUtils.isEmpty(giveMon)){
          itMap.put("remark", "赠送" + giveMon + "个月话费");
      }
      return itMap;
  }
}
