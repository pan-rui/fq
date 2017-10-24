package com.hy.action;

import com.hy.annotation.EncryptProcess;
import com.hy.base.BaseResult;
import com.hy.base.ReturnCode;
import com.hy.core.Constants;
import com.hy.core.ParamsMap;
import com.hy.core.Table;
import com.hy.dao.BaseDao;
import com.hy.vo.ParamsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("delivery")
public class DeliveryAction extends BaseAction {
    @Autowired
    private BaseDao baseDao;
    private String tableName = Table.FQ + Table.DELIVERY;

    @PostMapping("")
    public BaseResult addDelivery(@RequestHeader(Constants.USER_ID) String userId, @EncryptProcess ParamsVo paramsVo) {
        int isDefault = Integer.parseInt(String.valueOf(paramsVo.getParams().get(Table.Delivery.IS_DEFAULT.name())));
        if (isDefault == 1) {
            baseDao.updateByProsInTab(tableName, ParamsMap.newMap(Table.Delivery.IS_DEFAULT.name(), 0));
        }
        int count = baseDao.insertByProsInTab(tableName, paramsVo.getParams());
        return count > 0 ? new BaseResult(ReturnCode.OK) : new BaseResult(ReturnCode.FAIL);
    }

    @PutMapping("")
    public BaseResult updateDelivery(@RequestHeader(Constants.USER_ID) String userId, @EncryptProcess ParamsVo paramsVo) {
        int count = baseDao.updateByProsInTab(tableName, paramsVo.getParams().addParams(Table.ID, paramsVo.getParams().remove(Table.ID)));
        return count > 0 ? new BaseResult(ReturnCode.OK) : new BaseResult(ReturnCode.FAIL);
    }

    @PatchMapping("")
    public BaseResult deleteDelivery(@RequestHeader(Constants.USER_ID) String userId, @EncryptProcess ParamsVo paramsVo) {
        int count = baseDao.updateByProsInTab(tableName, paramsVo.getParams().addParams(Table.Delivery.IS_ENABLE.name(), 0).addParams(Table.ID, paramsVo.getParams().remove(Table.ID)));
        return count > 0 ? new BaseResult(ReturnCode.OK) : new BaseResult(ReturnCode.FAIL);
    }

    @PostMapping("list")
    public BaseResult getDeliveryList(@RequestHeader(Constants.USER_ID) String userId, @EncryptProcess ParamsVo paramsVo) {
        return new BaseResult(ReturnCode.OK, baseDao.queryListInTab(tableName, paramsVo.getParams().addParams(Table.Delivery.IS_ENABLE.name(), 1)
                , ParamsMap.newMap(Table.Delivery.IS_DEFAULT.name(), Table.DESC).addParams(Table.Delivery.CTIME.name(), Table.DESC)));
    }
}
