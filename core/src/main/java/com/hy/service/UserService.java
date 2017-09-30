package com.hy.service;

import com.alibaba.fastjson.JSON;
import com.hy.base.BaseResult;
import com.hy.base.ReturnCode;
import com.hy.core.CacheKey;
import com.hy.core.Constants;
import com.hy.core.ParamsMap;
import com.hy.core.SerializeUtil;
import com.hy.core.Table;
import com.hy.dao.BaseDao;
import com.hy.util.AliUtil;
import com.hy.util.ImgUtil;
import com.hy.vo.ParamsVo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.w3c.dom.html.HTMLTableElement;
import sun.misc.Cache;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by kaifa_03 on 2017/9/18.
 */
@Service
public class UserService {
    @Autowired
    private BaseDao baseDao;
    public static int certStatusExpire=172800;
    private final Logger logger = LogManager.getLogger(UserService.class);
    public int addUser(Map<String, Object> uMap) {
        return baseDao.insertByProsInTab(Table.FQ+ Table.USER, uMap);
    }

//    @Transactional
    public Map<String, Object> updateCert(String userId, String phone,MultipartFile cardUp, MultipartFile cardDown,MultipartFile card) throws IOException {
        List<Map<String, Object>> attachs = new ArrayList<>();
        Map<String, Object> pathMap = new HashMap<>();
        String cardUpFileName = cardUp.getOriginalFilename();
        String cardUpPath = ImgUtil.USER_CERT_PATH+phone+"/"+ UUID.randomUUID().toString() + Table.SEPARATE + cardUpFileName.split("\\.")[1];
        File cardUpFile = new File(ImgUtil.BASE_PATH + cardUpPath);
        if(!cardUpFile.getParentFile().exists()) cardUpFile.getParentFile().mkdirs();
        cardUp.transferTo(cardUpFile);
        pathMap.put("cardUp",cardUpPath);
        attachs.add(ParamsMap.newMap(Table.UserAttach.ATTACH_TYPE.name(), "0").addParams(Table.UserAttach.URL.name(), cardUpPath).addParams(Table.UserAttach.ATTACH_LEN.name(), cardUpFile.length())
                .addParams(Table.UserAttach.UP_ID.name(), userId).addParams(Table.UserAttach.USER_ID.name(),userId).addParams(Table.UserAttach.IS_ENABLE.name(), 1));
        String cardDwonFileName = cardDown.getOriginalFilename();
        String cardDownPath = ImgUtil.USER_CERT_PATH+phone+"/"+UUID.randomUUID().toString() + Table.SEPARATE + cardDwonFileName.split("\\.")[1];
        File cardDownFile = new File(ImgUtil.BASE_PATH + cardDownPath);
        cardDown.transferTo(cardDownFile);
        pathMap.put("cardDown",cardDownPath);
        attachs.add(ParamsMap.newMap(Table.UserAttach.ATTACH_TYPE.name(), "0").addParams(Table.UserAttach.URL.name(), cardUpPath).addParams(Table.UserAttach.ATTACH_LEN.name(), cardUpFile.length())
                .addParams(Table.UserAttach.UP_ID.name(), userId).addParams(Table.UserAttach.USER_ID.name(),userId).addParams(Table.UserAttach.IS_ENABLE.name(), 1));
        String cardFileName = card.getOriginalFilename();
        String cardPath = ImgUtil.USER_CERT_PATH+phone+"/"+UUID.randomUUID().toString() + Table.SEPARATE + cardFileName.split("\\.")[1];
        File cardFile = new File(ImgUtil.BASE_PATH + cardPath);
        card.transferTo(cardFile);
        pathMap.put("card",cardPath);
        attachs.add(ParamsMap.newMap(Table.UserAttach.ATTACH_TYPE.name(), "0").addParams(Table.UserAttach.URL.name(), cardUpPath).addParams(Table.UserAttach.ATTACH_LEN.name(), cardFile.length())
                .addParams(Table.UserAttach.UP_ID.name(), userId).addParams(Table.UserAttach.USER_ID.name(),userId).addParams(Table.UserAttach.IS_ENABLE.name(), 1));
        baseDao.insertUpdateBatchByProsInTab(Table.FQ + Table.USER_ATTACH, attachs);
        return pathMap;
    }

    public BaseResult submitCert(String userId,String phone,ParamsVo paramsVo) {
        String certCache = Constants.getCache(CacheKey.U_BANK_Prefix + phone);
        String bankName="",bankPhone="",cardNo="";
        if (StringUtils.isEmpty(certCache)) {
            Map<String, Object> user = baseDao.queryByIdInTab(Table.FQ + Table.USER, userId);
            bankName= (String) user.get("name");
            bankPhone= (String) user.get("phone");
            cardNo = (String) user.get("cardNo");
        }else{
            String[] arry = certCache.split(Table.FIELD_INTERVAL);
            bankName = arry[0];
            cardNo = arry[1];
            bankPhone = arry[2];
        }
        int count = baseDao.insertUpdateByProsInTab(Table.FQ + Table.USER_BANK, paramsVo.getParams().addParams(Table.UserBank.USER_ID.name(), userId).addParams(Table.UserBank.STATUS.name(), "2").addParams(Table.UserBank.USER_NAME.name(), bankName)
                .addParams(Table.UserBank.BANK_USER_NAME.name(), bankName)
                .addParams(Table.UserBank.BANK_MOBILE.name(), bankPhone));
        List<Map<String,Object>> attachs=baseDao.queryByProsInTab(Table.FQ + Table.USER_ATTACH, ParamsMap.newMap(Table.UserAttach.USER_ID.name(), userId));
        Map<String, Object> resultMap = AliUtil.cert(ParamsMap.newMap("bankcard",paramsVo.getParams().get(Table.UserBank.BANK_CARD_NO.name())).addParams("idcard",cardNo).addParams("mobile",bankPhone).addParams("realname",bankName));         //实名认证接口
        ParamsMap paramsMap = ParamsMap.newMap(Table.User.UP_ID.name(), userId).addParams(Table.User.UTIME.name(), new Date());
        String status= (String) resultMap.get("status");
        String verifyCode= "1";     // 0一致，1不一致
        String errMsg="";
        if ("0".equals(status)) {
            Map<String, Object> result = (Map<String, Object>) resultMap.get("result");
            verifyCode= (String) result.get("verifystatus");
                logger.info(JSON.toJSONString(resultMap));
            errMsg = (String) result.get("verifymsg");
            if ("0".equals(verifyCode)) {
                paramsMap.addParams(Table.User.CERT_STATUS.name(), "1");
                Constants.setCacheOnExpire(CacheKey.U_CERT_STATUS_Prefix + phone, "1", certStatusExpire);
            } else {
                paramsMap.addParams(Table.User.CERT_STATUS.name(), "2");
                Constants.setCacheOnExpire(CacheKey.U_CERT_STATUS_Prefix + phone, "2", certStatusExpire);
            }
        } else errMsg = (String) resultMap.get("msg");
            baseDao.updateByProsInTab(Table.FQ + Table.USER,paramsMap);
        return "0".equals(verifyCode) ? new BaseResult(ReturnCode.OK) : new BaseResult(Integer.parseInt(verifyCode),errMsg);
    }

}
