package com.hy.service;

import com.hy.base.BaseImpl;
import com.hy.base.BaseResult;
import com.hy.base.ReturnCode;
import com.hy.core.ParamsMap;
import com.hy.core.Table;
import com.hy.dao.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Map;

@Service
public class OrderService {
    @Autowired
    private BaseImpl baseImpl;
    @Autowired
    private BaseDao baseDao;

    public BaseResult pay(Map<String, Object> params) {
        Calendar calendar=Calendar.getInstance();
        //验证
        //支付接口
        //更新订单信息及相关信息
        //保存交易记录
        ParamsMap paramsMap = ParamsMap.newMap(Table.Order.STATE.name(), "1").addParams(Table.Order.PAY_TIME.name(), calendar.getTime());
        baseDao.updateByProsInTab(Table.FQ + Table.ORDER,paramsMap .addParams(Table.ID, params.get("id")));
        return new BaseResult(ReturnCode.OK);
    }

    public BaseResult repay(Map<String, Object> params) {
        return new BaseResult(ReturnCode.OK);
    }
}
