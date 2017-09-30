package com.hy.service;

import com.hy.base.BaseImpl;
import com.hy.base.BaseResult;
import com.hy.base.ReturnCode;
import com.hy.dao.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OrderService {
    @Autowired
    private BaseImpl baseImpl;
    @Autowired
    private BaseDao baseDao;

    public BaseResult pay(Map<String, Object> params) {
        //验证
        //支付接口
        //更新订单信息及相关信息
        //保存交易记录

        return new BaseResult(ReturnCode.OK);
    }
}
