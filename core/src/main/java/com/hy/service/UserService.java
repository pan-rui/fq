package com.hy.service;

import com.alibaba.fastjson.JSON;
import com.hy.base.BaseResult;
import com.hy.base.IBase;
import com.hy.base.ReturnCode;
import com.hy.core.CacheKey;
import com.hy.core.Constants;
import com.hy.core.ParamsMap;
import com.hy.core.SerializeUtil;
import com.hy.core.Table;
import com.hy.dao.BaseDao;
import com.hy.unionpay.sdk.AcpService;
import com.hy.unionpay.sdk.SDKConfig;
import com.hy.util.AliUtil;
import com.hy.util.ImageCode;
import com.hy.util.ImgUtil;
import com.hy.vo.ParamsVo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.util.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("#{config['MERID']}")
    private String merId;
    @Autowired
    private BaseDao baseDao;
    public static int certStatusExpire=14400;      //2天
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
        attachs.add(ParamsMap.newMap(Table.UserAttach.ATTACH_TYPE.name(), "1").addParams(Table.UserAttach.URL.name(), cardDownPath).addParams(Table.UserAttach.ATTACH_LEN.name(), cardUpFile.length())
                .addParams(Table.UserAttach.UP_ID.name(), userId).addParams(Table.UserAttach.USER_ID.name(),userId).addParams(Table.UserAttach.IS_ENABLE.name(), 1));
        String cardFileName = card.getOriginalFilename();
        String cardPath = ImgUtil.USER_CERT_PATH+phone+"/"+UUID.randomUUID().toString() + Table.SEPARATE + cardFileName.split("\\.")[1];
        File cardFile = new File(ImgUtil.BASE_PATH + cardPath);
        card.transferTo(cardFile);
        pathMap.put("card",cardPath);
        attachs.add(ParamsMap.newMap(Table.UserAttach.ATTACH_TYPE.name(), "9").addParams(Table.UserAttach.URL.name(), cardPath).addParams(Table.UserAttach.ATTACH_LEN.name(), cardFile.length())
                .addParams(Table.UserAttach.UP_ID.name(), userId).addParams(Table.UserAttach.USER_ID.name(),userId).addParams(Table.UserAttach.IS_ENABLE.name(), 1));
        baseDao.insertUpdateBatchByProsInTab(Table.FQ + Table.USER_ATTACH, attachs);
        return pathMap;
    }

    @Transactional(rollbackFor = Exception.class)
    public BaseResult submitCert(String phone,ParamsVo paramsVo) {
        String certCache = Constants.getCache(CacheKey.U_BANK_Prefix + phone);
        String bankName="",bankPhone="",cardNo="";
//        Long userId = Long.parseLong(String.valueOf(paramsVo.getParams().get(Table.USER_ID)));
        Object userId = paramsVo.getParams().get(Table.USER_ID);
        if (StringUtils.isEmpty(certCache)) {
            Map<String, Object> user = baseDao.queryByIdInTab(Table.FQ + Table.USER, userId);
            bankName= (String) user.get("name");
            bankPhone= (String) user.get("phone");      //本人手机号码
            cardNo = (String) user.get("cardNo");
        }else{
            String[] arry = certCache.split(Table.FIELD_INTERVAL);
            bankName = arry[0];
            cardNo = arry[1];
            bankPhone = arry[2];
        }
        String bankCardNo = (String) paramsVo.getParams().get(Table.UserBank.BANK_CARD_NO.name());
        //        List<Map<String,Object>> attachs=baseDao.queryByProsInTab(Table.FQ + Table.USER_ATTACH, ParamsMap.newMap(Table.UserAttach.USER_ID.name(), userId));
        Map<String, Object> resultMap = AliUtil.cert(ParamsMap.newMap("bankcard",bankCardNo).addParams("idcard",cardNo).addParams("mobile",bankPhone).addParams("realname",bankName));         //实名认证接口
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
                paramsMap.addParams(Table.User.CERT_STATUS.name(), "1").addParams(Table.User.BANK_CARD.name(),bankCardNo);
                Constants.setCacheOnExpire(CacheKey.U_CERT_STATUS_Prefix + phone, "1", certStatusExpire);
            } else {
                paramsMap.addParams(Table.User.CERT_STATUS.name(), "2");
                Constants.setCacheOnExpire(CacheKey.U_CERT_STATUS_Prefix + phone, "2", certStatusExpire);
            }
        } else errMsg = (String) resultMap.get("msg");
            baseDao.updateByProsInTab(Table.FQ + Table.USER,paramsMap.addParams(Table.ID,userId));
        return "0".equals(verifyCode) ? new BaseResult(ReturnCode.OK) : new BaseResult(Integer.parseInt(verifyCode),errMsg);
    }

    public BaseResult bindBank(ParamsVo paramsVo) {
        //TODO:银联绑定接口
        ParamsMap paramsMap=ParamsMap.newMap("version", SDKConfig.getConfig().getVersion()).addParams("encoding", IBase.DEF_CHATSET).addParams("signMethod", SDKConfig.getConfig().getSignMethod())
                .addParams("txnType","72").addParams("txnSubType","11").addParams("bizType", "000501").addParams("channelType", "07").addParams("merId", merId).addParams("accessType", "0")
                .addParams("orderId",String.valueOf(paramsVo.getParams().get(Table.USER_ID))+paramsVo.getParams().get(Table.UserBank.BANK_MOBILE.name()))
                .addParams("txnTime",IBase.sdf2.format(new Date())).addParams("accType", "01");
        ParamsMap userInfo = ParamsMap.newMap("customerNm", paramsVo.getParams().get(Table.UserBank.BANK_USER_NAME.name())).addParams("phoneNo", paramsVo.getParams().get(Table.UserBank.BANK_MOBILE.name()));
        paramsMap.addParams("accNo", AcpService.encryptData((String) paramsVo.getParams().get(Table.UserBank.BANK_CARD_NO.name()), IBase.DEF_CHATSET)).addParams("encryptCertId", AcpService.getEncryptCertId())
                .addParams("customerInfo", AcpService.getCustomerInfoWithEncrypt(userInfo, null, IBase.DEF_CHATSET));
        Map<String, String> reqData = AcpService.sign(paramsMap,IBase.DEF_CHATSET);
        Map<String, String> rspData = AcpService.post(reqData,SDKConfig.getConfig().getBackRequestUrl(),IBase.DEF_CHATSET);
        if(!rspData.isEmpty()){
            if(AcpService.validate(rspData, IBase.DEF_CHATSET)){
                String respCode = rspData.get("respCode") ;
                if(("00").equals(respCode)){
                    //成功
                if("2".equals(paramsVo.getParams().get(Table.UserBank.STATUS.name()))){
                    baseDao.updateByMapInTab(Table.FQ + Table.USER_BANK, ParamsMap.newMap(Table.UserBank.STATUS.name(), "1"),ParamsMap.newMap(Table.USER_ID, paramsVo.getParams().get(Table.USER_ID)).addParams(Table.UserBank.STATUS.name(),"2"));
                }
                    baseDao.insertUpdateByProsInTab(Table.FQ+Table.USER_BANK,paramsVo.getParams());
                    return new BaseResult(ReturnCode.OK);
                }else{
                    //其他应答码为失败请排查原因或做失败处理
                    return new BaseResult(7778, rspData.get("respMsg"));
                }
            }else{
                //TODO 检查验证签名失败的原因
                logger.warn("====================银联绑卡验证签名失败...."+rspData);
            }
        }else{
            //未返回正确的http状态
            logger.error("====================银联绑卡出现网络故障...."+rspData);
        }
        return new BaseResult(ReturnCode.FAIL);
    }

}
