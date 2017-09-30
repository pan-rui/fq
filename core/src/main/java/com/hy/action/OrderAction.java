package com.hy.action;

import com.alibaba.fastjson.JSON;
import com.hy.annotation.EncryptProcess;
import com.hy.base.BaseResult;
import com.hy.base.ReturnCode;
import com.hy.core.ColumnProcess;
import com.hy.core.Constants;
import com.hy.core.Page;
import com.hy.core.ParamsMap;
import com.hy.core.Table;
import com.hy.dao.BaseDao;
import com.hy.service.OrderService;
import com.hy.util.ImageCode;
import com.hy.vo.ParamsVo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.apache.bcel.generic.RET;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("order")
public class OrderAction extends BaseAction {
    @Autowired
    private BaseDao baseDao;
    @Autowired
    private OrderService orderService;
    private static final Logger logger = LogManager.getLogger(OrderAction.class);
    private String tableName = Table.FQ + Table.ORDER;
    @PostMapping("/add")
    public BaseResult addOrder(HttpServletRequest request,@RequestHeader(Constants.USER_ID) String userId, @RequestHeader(Constants.USER_PHONE) String phone, @EncryptProcess ParamsVo paramsVo) {
        ParamsMap<String,Object> order=paramsVo.getParams();
        int uId = (int) order.get(Table.USER_ID);
        List<Map<String,Object>> countList=baseDao.queryByS("select count(1) cou from fq.ORDER where USER_ID="+uId+" and STATE='0'");
        if(!CollectionUtils.isEmpty(countList) && Integer.parseInt(String.valueOf(countList.get(0).get("cou")))>=3)
            return new BaseResult(ReturnCode.ORDER_LIMIT_SIZE);
        String orderNo = sdf.format(new Date());
        order.addParams(Table.Order.ORDER_NO.name(), orderNo);
        order.addParams(Table.Order.USER_ID.name(),uId );
        order.addParams(Table.Order.STATE.name(), "0");
        order.addParams(Table.Order.REQ_IP.name(), getIpAddr(request));
        Map<String,Object> attr= (Map<String, Object>) order.get(Table.Order.ATTR.name());
        order.addParams(Table.Order.ATTR.name(), JSON.toJSONString(attr));
        Map<String,Object> period= (Map<String, Object>) order.get(Table.Order.PERIOD.name());
        order.addParams(Table.Order.PERIOD.name(), JSON.toJSONString(period));
        order.addParams(Table.Order.UP_ID.name(), userId);
        BigDecimal orderMoney = new BigDecimal((String) order.get(Table.Order.MONEY.name()));
        Double DISCOUNT = (Double) order.get(Table.Order.DISCOUNT.name());      //折扣
        if (DISCOUNT!=null && DISCOUNT > 0) {
           BigDecimal  PREFERENTIAL=orderMoney.multiply(new BigDecimal(1.0 - DISCOUNT));            //优惠金额
            orderMoney=orderMoney.subtract(PREFERENTIAL);               //总额-优惠金额
            order.addParams(Table.Order.PREFERENTIAL.name(), PREFERENTIAL.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
        }
        if(false) {     //使用积分抵扣
            Map<String,Object> account= (Map<String, Object>) baseDao.queryByProsInTab(Table.FQ + Table.ACCOUNT, ParamsMap.newMap(Table.Account.USER_ID.name(), userId)).get(0);
            double nn=1.0;      //积分兑换比例
            BigDecimal SCORE_MONEY=((BigDecimal) account.get(Table.Account.SCORE_BALANCE.name())).multiply(new BigDecimal(nn));
            orderMoney = orderMoney.subtract(SCORE_MONEY);      //总额-积分抵扣
            order.addParams(Table.Order.SCORE_MONEY.name(), SCORE_MONEY.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        }
        order.addParams(Table.Order.ORDER_MONEY.name(), orderMoney.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        //首付=总额 * 首付比例 + 产品险 + 手续费
        BigDecimal payMoney = orderMoney.multiply(new BigDecimal(String.valueOf(period.get("FIRST_PAY_RATIO")))).add(new BigDecimal(String.valueOf(period.get("INSURE")))).add(new BigDecimal(String.valueOf(period.get("FEE"))));
        order.addParams(Table.Order.PAY_MONEY.name(), payMoney.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());        //首付
        //月供=（总额 - 首付）/期数 +（总额 - 首付）* 月利率
        BigDecimal remain=orderMoney.subtract(payMoney);
        BigDecimal MONTHLY = remain.divide(new BigDecimal((int) period.get("PERIOD")),2,BigDecimal.ROUND_HALF_UP).add(remain.multiply(new BigDecimal((String) period.get("RATE"))));
        order.addParams(Table.Order.MONTHLY.name(), MONTHLY.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());       //月供
        int count=baseDao.insertByProsInTab(tableName, order);
        return count>0?new BaseResult(ReturnCode.OK, ColumnProcess.encryMap(order)):new BaseResult(ReturnCode.FAIL);
    }

    @GetMapping("id")
    public BaseResult getById(@RequestHeader(Constants.USER_ID) String userId, @RequestHeader(Constants.USER_PHONE) String phone, @RequestParam("pId") Long pId) {
        return new BaseResult(ReturnCode.OK, baseDao.queryByIdInTab(tableName, pId));
    }

    @PostMapping("/page")
    public BaseResult getByPage(@RequestHeader(Constants.USER_ID) String userId, @RequestHeader(Constants.USER_PHONE) String phone, @EncryptProcess Page page) {
        baseDao.queryPageInTab(tableName, page);
        return new BaseResult(ReturnCode.OK, page);
    }

    public BaseResult payOrder(@RequestHeader(Constants.USER_ID) String userId, @RequestHeader(Constants.USER_PHONE) String phone, @EncryptProcess ParamsVo paramsVo) {
        BaseResult baseResult=orderService.pay(paramsVo.getParams());
        if (baseResult.getCode() == 0) {
            Map<String,Object> orderMap=paramsVo.getParams();
            Map<String, Object> periodMap = (Map<String, Object>) paramsVo.getParams().get(Table.Order.PERIOD.name());
            Long uId = (Long) orderMap.get(Table.Order.USER_ID.name());
            int period= (int) periodMap.get("PERIOD");
            Calendar calendar=Calendar.getInstance();
            List<Map<String, Object>> srcList = new ArrayList<>();
            for(int i=1;i<=period;i++) {
                calendar.add(Calendar.MONTH,1);
                ParamsMap planRepayMap = ParamsMap.newMap(Table.PlanRepayment.USER_ID.name(), uId)
                        .addParams(Table.PlanRepayment.ORDER_ID.name(), orderMap.get(Table.PlanRepayment.ORDER_ID.name()))
                        .addParams(Table.PlanRepayment.PLANREPAY_DATE.name(), calendar.getTime())
                        .addParams(Table.PlanRepayment.PLANREPAY_MONEY.name(), orderMap.get(Table.Order.MONTHLY.name()))
                        .addParams(Table.PlanRepayment.REPAY_NUM.name(), i)
                        .addParams(Table.PlanRepayment.STATUS.name(), "0")      //0未还
                        .addParams(Table.PlanRepayment.UP_ID.name(), userId);
                srcList.add(planRepayMap);
            }
            int count=baseDao.insertBatchByProsInTab(Table.FQ + Table.PLAN_REPAYMENT, srcList);
            if(count<period)
                    logger.error("ERROR:还款计划生成有误。。。。");
            return baseResult;
        }else
        return baseResult;
    }

}
