package com.hy.action;

import com.hy.annotation.EncryptProcess;
import com.hy.base.BaseResult;
import com.hy.base.ReturnCode;
import com.hy.core.Constants;
import com.hy.core.Page;
import com.hy.core.Table;
import com.hy.dao.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("product")
public class ProductAction extends BaseAction {
    @Autowired
    private BaseDao baseDao;
    private String tableName=Table.FQ+Table.PRODUCT;
    @GetMapping("id")
    public BaseResult getByID(@RequestHeader(Constants.APP_VER) String appVer, @RequestParam("pId") Long pId) {
/*        if (StringUtils.isEmpty(pId)) {
            return new BaseResult(ReturnCode.OK, baseDao.queryAllInTab(Table.FQ + Table.PRODUCT));
        }*/
        return new BaseResult(ReturnCode.OK,baseDao.queryByIdInTab(tableName,pId));
    }

    @PostMapping("page")
    public BaseResult getByPage(@RequestHeader(Constants.APP_VER) String appVer, @EncryptProcess Page page) {
        baseDao.queryPageInTab(tableName, page);
        return new BaseResult(ReturnCode.OK, page);
    }

}
