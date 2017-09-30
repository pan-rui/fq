package com.hy.action;

import com.hy.base.BaseResult;
import com.hy.base.ReturnCode;
import com.hy.core.CacheKey;
import com.hy.core.Constants;
import com.hy.core.ParamsMap;
import com.hy.core.SerializeUtil;
import com.hy.core.Table;
import com.hy.dao.BaseDao;
import com.hy.service.UserService;
import com.hy.util.ImageCode;
import com.hy.vo.ParamsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by kaifa_03 on 2017/9/18.
 */
@RestController
@RequestMapping("user")
public class UserAction extends BaseAction {
    @Value("#{config['sessionExpireTime']}")
    private int sessionExpire;
    @Autowired
    private UserService userService;
    @Autowired
    private BaseDao baseDao;
    private String tableName = Table.FQ + Table.EMPLOYEE;

    @PostMapping("login")
    public Object login(@RequestBody ParamsVo paramsVo) {
        String phone = (String) paramsVo.getParams().get("phone");
        String clientSn = (String) paramsVo.getParams().get("clientSn");
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(clientSn))
            return new BaseResult(ReturnCode.REQUEST_PARAMS_VERIFY_ERROR);
        String uClientSn = Constants.getCache(CacheKey.U_SN_Prefix + phone);
        List<Map<String, Object>> users = baseDao.queryByProsInTab(tableName, ParamsMap.newMap(Table.Employee.PHONE.name(), phone).addParams(Table.Employee.STATE.name(),"1"));
        if (CollectionUtils.isEmpty(users)) {
            return new BaseResult(ReturnCode.USER_NOT_EXISTS);
        }
        Map<String, Object> user = users.get(0);
        if (StringUtils.isEmpty(uClientSn)) {
            uClientSn = (String) user.get("clientSn");
        }
        if (paramsVo.getParams().get("pwd").equals(user.get("pwd"))) {
            if (StringUtils.isEmpty(uClientSn)) {
                baseDao.updateByProsInTab(tableName, ParamsMap.newMap(Table.Employee.CLIENT_SN.name(), clientSn).addParams(Table.Employee.PHONE.name(), phone));
                Constants.setCacheValue("tmp", CacheKey.U_ + phone, SerializeUtil.serialize(user));     //缓存登录信息
                Constants.setCache(CacheKey.U_SN_Prefix + phone, clientSn);        //TODO:更新用户表CLIENT_SN字段时缓存起来
            } else {
                Constants.setCache(CacheKey.U_SN_Prefix + phone, uClientSn);        //TODO:更新用户表CLIENT_SN字段时缓存起来
                if (!uClientSn.equals(clientSn)) {
                    return new BaseResult(ReturnCode.ONLY_LIMIT_CLIENT);
                }
            }
            String token = ImageCode.getPartSymbol(32);
            Constants.setCacheOnExpire(CacheKey.U_TOKEN_Prefix + phone, token, sessionExpire);
            return new BaseResult(0, ParamsMap.newMap("token", token).addParams("userInfo", user));
        } else return new BaseResult(ReturnCode.LOGIN_PWD_ERROR);
    }

}
