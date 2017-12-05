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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("productAttr")
public class ProductAttrAction extends BaseAction {
    @Autowired
    private BaseDao baseDao;
    private String tableName = Table.FQ + Table.C_PRODUCT_ATTRIBUTE;

    @PostMapping("")
    public BaseResult getByProduct(@RequestHeader(Constants.APP_VER) String appVer, @EncryptProcess ParamsVo paramsVo) {
        return new BaseResult(ReturnCode.OK, baseDao.queryListInTab(tableName, paramsVo.getParams().addParams(Table.IS_ENABLE,1), ParamsMap.newMap(Table.CProductAttribute.SEQ.name(),"asc")));

    }
}
