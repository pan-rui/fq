package com.hy.action;

import com.hy.annotation.EncryptProcess;
import com.hy.base.BaseResult;
import com.hy.core.Constants;
import com.hy.core.Page;
import com.hy.core.Table;
import com.hy.dao.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.text.TabExpander;

@RestController
@RequestMapping("company")
public class CompanyAction extends BaseAction {
    @Autowired
    private BaseDao baseDao;
    private String tableName=Table.FQ + Table.COMPANY;
    @GetMapping("")
    public Object getList(@RequestHeader(Constants.USER_ID) String userId) {
        return new BaseResult(0,baseDao.queryAllInTab(tableName));
    }
    @PostMapping("")
    public Object getPage(@RequestHeader(Constants.USER_ID) String userId, @EncryptProcess Page page) {
        page.getParams().put(Table.IS_ENABLE, 1);
        baseDao.queryPageInTab(tableName, page);
        return new BaseResult(0,page);
    }
}
