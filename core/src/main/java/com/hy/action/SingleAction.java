package com.hy.action;

import com.alibaba.fastjson.JSON;
import com.hy.annotation.EncryptProcess;
import com.hy.base.BaseResult;
import com.hy.base.IBase;
import com.hy.base.ReturnCode;
import com.hy.core.ColumnProcess;
import com.hy.core.Constants;
import com.hy.core.Page;
import com.hy.core.ParamsMap;
import com.hy.core.Table;
import com.hy.dao.BaseDao;
import com.hy.service.CommonService;
import com.hy.unionpay.sdk.AcpService;
import com.hy.vo.ParamsVo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("single")
public class SingleAction extends BaseAction {
    @Autowired
    private BaseDao baseDao;
    @Autowired
    private CommonService commonService;
    private final static Logger logger = LogManager.getLogger(SingleAction.class);

    /**
     * 添加/修改 通用
     *
     * @param appVer
     * @param tableName
     * @param paramsVo
     * @return
     */
    @PostMapping("{tableName:[A-Za-z]+}")
    public BaseResult cart(@RequestHeader(Constants.APP_VER) String appVer, @RequestHeader(Constants.USER_ID) Long uId, @PathVariable String tableName, @EncryptProcess ParamsVo paramsVo) {
//        String id= (String) paramsVo.getParams().remove(Table.ID);
//        if(!StringUtils.isEmpty(id)) paramsVo.getParams().put(Table.ID, id);
        int size = baseDao.insertUpdateByProsInTab(Table.FQ + ColumnProcess.decryptVal(tableName), paramsVo.getParams().addParams(Table.UP_ID, uId).addParams(Table.UTIME, new Date()));
        return size > 0 ? new BaseResult(ReturnCode.OK) : new BaseResult(ReturnCode.FAIL);
    }

    /**
     * 分页查询通用
     *
     * @param appVer
     * @param tableName
     * @param page
     * @return
     */
    @PutMapping("{tableName:[A-Za-z]+}")
    public BaseResult getCart(@RequestHeader(Constants.APP_VER) String appVer, @PathVariable String tableName, @EncryptProcess Page page) {
        baseDao.queryPageInTab(Table.FQ + ColumnProcess.decryptVal(tableName), page);
        return new BaseResult(0, page);
    }

    @PostMapping("{tableName:[A-Za-z]+}/mul")
    public BaseResult getMulCart(@RequestHeader(Constants.APP_VER) String appVer, @PathVariable String tableName, @EncryptProcess Page page) {
        baseDao.queryPageMulTab(Table.FQ + ColumnProcess.decryptVal(tableName), page);
        return new BaseResult(0, page);
    }

    /**
     * 删除 通用
     *
     * @param appVer
     * @param tableName
     * @param paramsVo
     * @return
     */
    @PatchMapping("{tableName:[A-Za-z]+}")
    public BaseResult delCart(@RequestHeader(Constants.APP_VER) String appVer, @PathVariable String tableName, @EncryptProcess ParamsVo paramsVo) {
        int size = baseDao.deleteByProsInTab(Table.FQ + ColumnProcess.decryptVal(tableName), paramsVo.getParams());
        return size > 0 ? new BaseResult(ReturnCode.OK) : new BaseResult(ReturnCode.FAIL);
    }

    /**
     * 据ID查询
     *
     * @param appVer
     * @param tableName
     * @param id
     * @return
     */
    @GetMapping("{tableName:[A-Za-z]+}/{id:\\d+}")
    public BaseResult get(@RequestHeader(Constants.APP_VER) String appVer, @PathVariable String tableName, @PathVariable Long id) {
        Map<String, Object> resultMap = baseDao.queryByIdInTab(Table.FQ + ColumnProcess.decryptVal(tableName), id);
        return new BaseResult(ReturnCode.OK, resultMap);
    }

    /**
     *  修改
     * @param appVer
     * @param uId
     * @param tableName
     * @param paramsVo
     * @return
     */
    @PutMapping("{tableName:[A-Za-z]+}/up")
    public BaseResult update(@RequestHeader(Constants.APP_VER) String appVer, @RequestHeader(Constants.USER_ID) Long uId, @PathVariable String tableName, @EncryptProcess ParamsVo paramsVo) {
        int size = baseDao.updateByProsInTab(Table.FQ + ColumnProcess.decryptVal(tableName), paramsVo.getParams().addParams(Table.UP_ID, uId).addParams(Table.UTIME, new Date()).addParams(Table.ID, paramsVo.getParams().remove(Table.ID)));
        return size > 0 ? new BaseResult(ReturnCode.OK) : new BaseResult();
    }

/*    @PostMapping("collect")
    public BaseResult collect(@RequestHeader(Constants.USER_ID) Long uId,@RequestHeader(Constants.APP_VER) String appVer,@EncryptProcess ParamsVo paramsVo){
        int size=baseDao.insertUpdateByProsInTab(Table.FQ + Table.USER_COLLECT, paramsVo.getParams());
        return size>0?new BaseResult(ReturnCode.OK):new BaseResult(ReturnCode.FAIL);
    }

    @GetMapping("collect")
    public BaseResult getCollect(@RequestHeader(Constants.USER_ID) Long uId,@RequestHeader(Constants.APP_VER) String appVer,@EncryptProcess Page page){
        baseDao.queryPageInTab(Table.FQ + Table.USER_COLLECT, page);
        return new BaseResult(0,page);
    }

    @PatchMapping("collect")
    public BaseResult delCollect(@RequestHeader(Constants.USER_ID) Long uId,@RequestHeader(Constants.APP_VER) String appVer,@EncryptProcess ParamsVo paramsVo){
        int size=baseDao.deleteByProsInTab(Table.FQ + Table.USER_COLLECT, paramsVo.getParams());
        return size>0?new BaseResult(ReturnCode.OK):new BaseResult(ReturnCode.FAIL);
    }*/

    @PostMapping("shipCall")
    public Object shipCall(@RequestParam Long orderId, @RequestParam String param, @RequestParam(required = false) String sign) {
        Map<String, Object> pMap = JSON.parseObject(param, Map.class);
        if ("abort".equals(pMap.get("status")) && StringUtils.isEmpty(pMap.get("comNew"))) {
            baseDao.updateByProsInTab(Table.FQ + Table.ORDER, ParamsMap.newMap(Table.Order.STATE.name(), "1").addParams(Table.ID, orderId));
        } else {
            String state = "5";
            String resultStr=JSON.toJSONString(pMap.get("lastResult"));
            if(resultStr.contains("签收"))
                state = "7";
            baseDao.updateByProsInTab(Table.FQ + Table.ORDER, ParamsMap.newMap(Table.Order.SHIPMENTS_INFO.name(), resultStr).addParams(Table.Order.STATE.name(),state).addParams(Table.ID, orderId));
        }
        return ParamsMap.newMap("result", true).addParams("returnCode", "200");
    }

    @PostMapping("unionBack")
    public Object unionBack(HttpServletRequest request) {
        System.out.println("========================unionPay notify=========================");
        Map<String, String> rspData = new HashMap<>();
        request.getParameterMap().forEach((k, v) -> {
            rspData.put(k, v[0]);
            System.out.println(k + "========>" + v[0]);
        });
//        origQryId
        if(rspData.containsKey("origQryId")){
//                commonService    退款方法
        }else {
            if (AcpService.validate(rspData, IBase.DEF_CHATSET)) {
                String respCode = rspData.get("respCode");
                if (("00").equals(respCode)) {
                    String amount = rspData.get("txnAmt");
                    commonService.paySuccessCallBack(rspData, rspData.get("orderId"), rspData.get("queryId"), rspData.get("txnTime"), "reqReserved",new BigDecimal(amount).divide(new BigDecimal("100"),2, RoundingMode.HALF_UP).toString(), "2");
                } else if (("03").equals(respCode) || ("04").equals(respCode) || ("05").equals(respCode)) {
                    //后续需发起交易状态查询交易确定交易状态
                    logger.warn("====================银联代收需要查询状态....");
                } else {
                    //其他应答码为失败请排查原因
                    logger.warn("====================银联代收失败....");
                }
            } else {
                //TODO 检查验证签名失败的原因
                logger.warn("====================银联代收验证签名失败....");
            }
        }
        return null;
    }
}
