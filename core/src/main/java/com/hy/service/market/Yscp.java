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
import java.util.List;
import java.util.Map;

@Service
public class Yscp implements IMarket{
    @Autowired
    private BaseDao baseDao;
    @Autowired
    private CommonDao commonDao;

    private String name = "1006";
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
        BigDecimal deposit = new BigDecimal(String.valueOf(mMap.get("deposit")));
        itMap.put(ACTIVITY_MONEY, deposit);
        itMap.put(AFFECTED_FIRST_PAY, false);
        Map<String,Object> periodMap= (Map<String, Object>) itMap.get("period");
        periodMap.put("exemptMoney", deposit);
        if(!CollectionUtils.isEmpty(couMap))
            commonProcess(itMap, baseDao, couMap);
        return itMap;
    }
}
