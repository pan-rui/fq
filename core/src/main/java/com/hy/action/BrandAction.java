package com.hy.action;

import com.hy.annotation.EncryptProcess;
import com.hy.base.BaseResult;
import com.hy.base.ReturnCode;
import com.hy.core.Constants;
import com.hy.core.Page;
import com.hy.core.Table;
import com.hy.dao.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("brand")
public class BrandAction extends BaseAction {
    @Autowired
    private BaseDao baseDao;
    private String tableName=Table.FQ + Table.BRAND;

    @PostMapping("page")
    public BaseResult getList(@RequestHeader(Constants.USER_PHONE) String phone, @EncryptProcess Page page) {
        List<Map<String, Object>> resultList = baseDao.queryPageInTab(tableName,page);
        return new BaseResult(ReturnCode.OK, page);
    }
}
