package com.hy.action;

import com.hy.annotation.EncryptProcess;
import com.hy.base.BaseResult;
import com.hy.base.ReturnCode;
import com.hy.core.CacheKey;
import com.hy.core.Constants;
import com.hy.core.ParamsMap;
import com.hy.core.SerializeUtil;
import com.hy.core.Table;
import com.hy.dao.BaseDao;
import com.hy.dao.UserDao;
import com.hy.intercept.TokenInterceptor;
import com.hy.service.UserService;
import com.hy.util.ImageCode;
import com.hy.vo.ParamsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
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
    @Autowired
    private UserDao userDao;
    private String tableName = Table.FQ + Table.USER;

    @PostMapping("add")
    public Object addUser(@EncryptProcess ParamsVo params) {
        ParamsMap<String, Object> map = params.getParams();
        String code = (String) map.remove("SMS_CODE");
        String phone = (String) map.get(Table.User.PHONE.name());
        if (StringUtils.isEmpty(code) || !code.equals(Constants.getCache(CacheKey.U_SMS_Prefix + phone)))
            return new BaseResult(103, "短信验证码错误");
        List<Map<String, Object>> users = baseDao.queryByProsInTab(tableName, ParamsMap.newMap(Table.User.PHONE.name(), phone));
        if (!CollectionUtils.isEmpty(users)) return new BaseResult(333, "该手机号已注册");
        String userName=phone+Table.SEPARATE_CACHE+ImageCode.getPartSymbol(6);
        map.put(Table.User.USER_NAME.name(), userName);        //默认用户名
        map.put(Table.User.NICKNAME.name(),userName );        //默认昵称
        int result = userService.addUser(map.addParams(Table.User.TYPE.name(), "User"));
        if (result > 0) {
//            Constants.setCache(CacheKey.U_SN_Prefix + phone, (String) map.get(Table.User.CLIENT_SN.name()));        //TODO:更新用户表CLIENT_SN字段时缓存起来
            return new BaseResult(ReturnCode.OK,map);
        } else return new BaseResult(ReturnCode.FAIL);
    }

    @PostMapping("forget")
    public Object forgetPasswd(@RequestParam String phone,@RequestParam(required = false) String clientSn,@RequestParam String smsCode, @RequestParam String newPasswd) {
        if (StringUtils.isEmpty(smsCode) || !smsCode.equals(Constants.getCache(CacheKey.U_SMS_Prefix + phone)))
            return new BaseResult(103, "短信验证码错误");
        List<Map<String,Object>> userList=baseDao.queryByProsInTab(tableName, ParamsMap.newMap(Table.User.PHONE.name(), phone));
        if(CollectionUtils.isEmpty(userList))
            return new BaseResult(201, "您输入的用户不存在");
//        if(!userList.get(0).get("clientSn").equals(clientSn))     //TODO:去掉设备序列号限制
//            return new BaseResult(ReturnCode.ONLY_LIMIT_CLIENT);
        int count = baseDao.updateByProsInTab(tableName, ParamsMap.newMap(Table.User.PWD.name(),newPasswd).addParams(Table.User.PHONE.name(),phone));
        return count > 0 ? new BaseResult(ReturnCode.OK) : new BaseResult(ReturnCode.FAIL);
    }

    //addParams("PWD", Base64.encode(DigestUtils.md5Hex(tenantPhone.substring(5)).getBytes()))

    @PostMapping("login")
    public Object login(@RequestHeader(Constants.APP_VER)String reqAppVer,@RequestHeader(required = false)String openId,@RequestBody ParamsVo paramsVo) {
        String phone = (String) paramsVo.getParams().get("phone");
        String clientSn = (String) paramsVo.getParams().get("clientSn");
        String appMeta = (String) paramsVo.getParams().get("appMeta");
/*        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(clientSn))
            return new BaseResult(ReturnCode.REQUEST_PARAMS_VERIFY_ERROR);
        List<Map<String, Object>> users = baseDao.queryByProsInTab(tableName, ParamsMap.newMap(Table.User.PHONE.name(), phone).addParams(Table.User.CLIENT_SN.name(), clientSn).addParams(Table.User.IS_ENABLE.name(), 1));*/
        if (StringUtils.isEmpty(phone))
            return new BaseResult(ReturnCode.REQUEST_PARAMS_VERIFY_ERROR);
//        List<Map<String, Object>> users = baseDao.queryByProsInTab(tableName, ParamsMap.newMap(Table.User.PHONE.name(), phone).addParams(Table.User.IS_ENABLE.name(), 1));      //TODO:AppStore上架
        List<Map<String, Object>> users = userDao.queryLoginTab(phone,null);      //TODO:AppStore上架
        if (CollectionUtils.isEmpty(users)) {
            return new BaseResult(ReturnCode.USER_NOT_EXISTS);
        }
        Map<String, Object> user = users.get(0);
/*        if (!user.get("clientSn").equals(paramsVo.getParams().get("clientSn"))) {     //TODO:AppStore 上架
            return new BaseResult(ReturnCode.ONLY_LIMIT_CLIENT);
        }*/
        if (paramsVo.getParams().get("pwd").equals(user.get("pwd"))) {
//            Constants.setCacheValue("tmp", CacheKey.U_ + phone, SerializeUtil.serialize(user));     //缓存登录信息
            String token = ImageCode.getPartSymbol(32);
            Long id = (Long) user.get("id");
            if(!StringUtils.isEmpty(appMeta))
                Constants.hsetCache(CacheKey.APP_META,"U_"+id,appMeta);
            if (StringUtils.isEmpty(user.get("openId"))&&!StringUtils.isEmpty(openId)) {            //公众号
                baseDao.updateByProsInTab(Table.FQ + Table.USER, ParamsMap.newMap(Table.User.OPEN_ID.name(), openId).addParams(Table.UTIME,new Date()).addParams(Table.ID,id));
            }
            if(TokenInterceptor.WeChat.equals(reqAppVer.split(Table.SEPARATE_CACHE)[1])){
                Constants.hsetCache(CacheKey.WX_HEAD, Constants.USER_ID+openId,String.valueOf(id));
                Constants.hsetCache(CacheKey.WX_HEAD, Constants.USER_PHONE+openId,(String)user.get("phone"));
            }else
                Constants.setCacheOnExpire(CacheKey.U_TOKEN_Prefix + id, token, sessionExpire);
            return new BaseResult(0, ParamsMap.newMap("token", token).addParams("userInfo", user));
        } else return new BaseResult(ReturnCode.LOGIN_PWD_ERROR);
    }

}
