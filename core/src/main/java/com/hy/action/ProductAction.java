package com.hy.action;

import com.hy.annotation.EncryptProcess;
import com.hy.base.BaseResult;
import com.hy.base.ReturnCode;
import com.hy.core.Constants;
import com.hy.core.Page;
import com.hy.core.Table;
import com.hy.dao.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("product")
public class ProductAction extends BaseAction {
    @Autowired
    private BaseDao baseDao;
    private String tableName = Table.FQ + Table.PRODUCT;

    @GetMapping("id")
    public BaseResult getByID(@RequestHeader(Constants.APP_VER) String appVer, @RequestParam("pId") Long pId) {
/*        if (StringUtils.isEmpty(pId)) {
            return new BaseResult(ReturnCode.OK, baseDao.queryAllInTab(Table.FQ + Table.PRODUCT));
        }*/
        return new BaseResult(ReturnCode.OK, baseDao.queryByIdInTab(tableName, pId));
    }

    @PostMapping("page")
    public BaseResult getByPage(@RequestHeader(Constants.APP_VER) String appVer, @EncryptProcess Page page) {
        baseDao.queryPageInTab(tableName, page);
        return new BaseResult(ReturnCode.OK, page);
    }

    @GetMapping("discuss/{id:[0-9]+}")
    public BaseResult getDiscuss(@RequestHeader(Constants.APP_VER) String appVer, @PathVariable Long id) {
        List<Map<String, Object>> resuls = baseDao.queryByS("select LEVEL,PICS from fq.PRODUCT_DISCUSS where PRODUCT_ID=" + id);
        int[] result = new int[]{0, 0, 0, 0, resuls.size()};
        resuls.forEach((map) -> {
            switch ((int) map.get(Table.ProductDiscuss.LEVEL.name())) {
                case 0:
                case 1:
                case 2:
                case 3:
                    result[0] += 1;     //差
                    break;
                case 4:
                case 5:
                case 6:
                    result[0] += 1;     //中[2.5~3.5]
                    break;
                default:
                    result[2] += 1;     //好
                    break;
            }
            if(!StringUtils.isEmpty(map.get(Table.ProductDiscuss.PICS.name())))
                result[3]+=1;
        });
        return new BaseResult(ReturnCode.OK, result);
    }

}
