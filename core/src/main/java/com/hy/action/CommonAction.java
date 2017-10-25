package com.hy.action;

import com.alibaba.fastjson.JSONArray;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.google.gson.JsonObject;
import com.hy.annotation.EncryptProcess;
import com.hy.base.BaseResult;
import com.hy.base.ReturnCode;
import com.hy.core.CacheKey;
import com.hy.core.Constants;
import com.hy.core.Page;
import com.hy.core.ParamsMap;
import com.hy.core.Table;
import com.hy.dao.BaseDao;
import com.hy.dao.CommonDao;
import com.hy.service.CommonService;
import com.hy.service.OrderService;
import com.hy.service.UserService;
import com.hy.util.AliUtil;
import com.hy.util.ImageCode;
import com.hy.util.ImgUtil;
import com.hy.util.JPushUtil;
import com.hy.vo.ParamsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class CommonAction extends BaseAction {

    @Autowired
    private BaseDao baseDao;
    @Autowired
    private UserService userService;
    @Autowired
    private CommonDao commonDao;
    @Autowired
    private CommonService commonService;
    @Autowired
    private OrderService orderService;
    @Value("#{config['sessionExpireTime']}")
    private int sessionExpire;
    private String userTable = Table.FQ + Table.USER;
    private String saleTable = Table.FQ + Table.EMPLOYEE;

    @PostMapping("/upload/file")
    public BaseResult uploadImg(@RequestHeader(Constants.USER_ID) String userId, @RequestHeader(Constants.APP_VER) String appVer, @RequestHeader(Constants.USER_PHONE) String phone, @RequestParam("file") MultipartFile file, @RequestParam("fileType") String fileType) throws IOException {
        return commonService.uploadImg(appVer, userId, phone, file, fileType);
    }

    @PostMapping("/upload/files")
    public BaseResult uploadImgs(HttpServletRequest request, String userId) throws IOException, ServletException {
        MultiValueMap<String, MultipartFile> fileMap = ((DefaultMultipartHttpServletRequest) request).getMultiFileMap();
        if (fileMap == null || fileMap.size() == 0) {
            return new BaseResult(ReturnCode.REQUEST_PARAMS_VERIFY_ERROR);
        }
        new File(ImgUtil.BASE_PATH + ImgUtil.TEMP_PATH).mkdirs();
        JSONArray jsonArray = new JSONArray();
        List<Map<String, Object>> attachs = new ArrayList<>();
        for (int i = 0; i < fileMap.size(); i++) {
            MultipartFile file = fileMap.get("file" + i).get(0);
            String fileName = file.getOriginalFilename();
            String path = ImgUtil.TEMP_PATH + UUID.randomUUID().toString() + "." + fileName.split("\\.")[1];
            File newFile = new File(ImgUtil.BASE_PATH + path);
            file.transferTo(newFile);
            jsonArray.add(path);
            attachs.add(ParamsMap.newMap(Table.UserAttach.ATTACH_TYPE.name(), request.getParameter("fileType" + i)).addParams(Table.UserAttach.URL.name(), path).addParams(Table.UserAttach.ATTACH_LEN.name(), newFile.length())
                    .addParams(Table.UserAttach.UP_ID.name(), userId).addParams(Table.UserAttach.USER_ID.name(), userId).addParams(Table.UserAttach.IS_ENABLE.name(), 1));
        }
        baseDao.insertUpdateBatchByProsInTab(Table.FQ + Table.USER_ATTACH, attachs);
        return new BaseResult(ReturnCode.OK, jsonArray);
    }

    @PostMapping("sms")
    public Object sendSms(@RequestBody ParamsVo params) throws Exception {
        String phone = (String) params.getParams().get("phone");
        String code = ImageCode.getPartDigit(6);
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code))
            return new BaseResult(ReturnCode.FAIL);
        Map<String, Object> smsTemplate = baseDao.queryByIdInTab(Table.FQ + Table.SMS_TEMPLATE, params.getParams().get("type"));
        String varJson = (String) smsTemplate.get("variables");
        if (!StringUtils.isEmpty(varJson))
            smsTemplate.put("variables", varJson.replace("random", code));
        SendSmsResponse sendSmsResponse = AliUtil.sendSms(smsTemplate, phone, null);
        Constants.setCacheOnExpire(CacheKey.U_SMS_Prefix + phone, code, 300);
        return "OK".equals(sendSmsResponse.getCode()) ? new BaseResult(ReturnCode.OK) : new BaseResult(ReturnCode.FAIL);
    }

    @GetMapping("cleanTmp")
    public BaseResult cleanTmp() {
        commonService.clearTmpCache();
        return new BaseResult(ReturnCode.OK);
    }

    @PostMapping("certS1")
    public Object certStep1(@RequestHeader(Constants.USER_ID) String userId, @RequestHeader(Constants.USER_PHONE) String phone, @EncryptProcess ParamsVo paramsVo) {
        String code = (String) paramsVo.getParams().remove("SMS_CODE");
        String bankPhone = (String) paramsVo.getParams().remove(Table.User.PHONE.name());
        if (StringUtils.isEmpty(code) || !code.equals(Constants.getCache(CacheKey.U_SMS_Prefix + bankPhone)))
            return new BaseResult(103, "短信验证码错误");
        Constants.setCacheOnExpire(CacheKey.U_BANK_Prefix + phone,
                paramsVo.getParams().get(Table.User.NAME.name()) + Table.FIELD_INTERVAL + paramsVo.getParams().get(Table.User.CARD_NO.name()) + Table.FIELD_INTERVAL + bankPhone, sessionExpire);
        int count = baseDao.updateByProsInTab(userTable, paramsVo.getParams().addParams(Table.ID, paramsVo.getParams().remove("USER_ID")));
        return count > 0 ? new BaseResult(ReturnCode.OK) : new BaseResult(ReturnCode.FAIL);
    }

    @PostMapping("certS2")
    public BaseResult certStep2(@RequestHeader(Constants.USER_ID) String userId, @RequestHeader(Constants.USER_PHONE) String phone, @RequestParam("cardUp") MultipartFile cardUp,
                                @RequestParam("cardDown") MultipartFile cardDown, @RequestParam("card") MultipartFile card) throws IOException, ServletException {
//        if (StringUtils.isEmpty(phone)) return new BaseResult(ReturnCode.HEADER_PARAMS_VERIFY_ERROR);
        return new BaseResult(ReturnCode.OK, userService.updateCert(userId, phone, cardUp, cardDown, card));
    }

    @PostMapping("certS3")
    public BaseResult certStep3(@RequestHeader(Constants.USER_ID) String userId, @RequestHeader(Constants.USER_PHONE) String phone, @EncryptProcess ParamsVo paramsVo) {
        return userService.submitCert(userId, phone, paramsVo);
    }

    @PostMapping("certS4")
    public BaseResult certStep4(@RequestHeader(Constants.USER_ID) String userId, @RequestHeader(Constants.USER_PHONE) String phone) {
        int count = baseDao.updateByProsInTab(userTable, ParamsMap.newMap(Table.User.CERT_STATUS.name(), "3").addParams(Table.User.UP_ID.name(), userId).addParams(Table.User.UTIME.name(), new Date()).addParams(Table.ID, userId));
        Constants.setCacheOnExpire(CacheKey.U_CERT_STATUS_Prefix + phone, "3", UserService.certStatusExpire);
        //TODO:通知审核人员,审核成功后推送给用户
        String certUserId = Constants.getSystemStringValue("CERT_USER_ID");
        String appMeta = Constants.hgetCache(CacheKey.APP_META_Prefix, JPushUtil.SALE_APP + certUserId);
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("userId", userId);
        JPushUtil.pushByRegId(JPushUtil.SALE_APP,"有一项用户分期资质审核任务待完成","用户号码:"+phone,jsonObject,appMeta.split(Table.SEPARATE_SPLIT)[0]);      //TODO:AppMeta
        return new BaseResult(ReturnCode.OK);
    }

    @GetMapping("certStatus")
    public BaseResult certStatus(@RequestHeader(Constants.USER_PHONE) String phone, @RequestHeader(Constants.USER_ID) String userId) {
        String status = Constants.getCache(CacheKey.U_CERT_STATUS_Prefix + phone);
        if (StringUtils.isEmpty(status)) {
            Map<String, Object> userMap = baseDao.queryByIdInTab(Table.FQ + Table.USER, userId);
            status = (String) userMap.get("certStatus");
            Constants.setCacheOnExpire(CacheKey.U_CERT_STATUS_Prefix + phone, status, UserService.certStatusExpire);
        }
        return new BaseResult(ReturnCode.OK, status);
    }

    @GetMapping("userInfo")
    public BaseResult userInfo(@RequestHeader(Constants.USER_ID) String uId, Long userId) {
        return new BaseResult(ReturnCode.OK, commonDao.queryUserInfo(userId));
    }

    @GetMapping("updateApp")
    public BaseResult updateApp(@RequestHeader(Constants.USER_ID) String userId, @RequestHeader(Constants.APP_VER) String appVer) {
        List<Map<String, Object>> resultList = baseDao.queryListInTab(Table.FQ + Table.APP_VESION, ParamsMap.newMap(Table.AppVesion.APP_TYPE.name(), "1").addParams(Table.AppVesion.IS_ENABLE.name(), 1), ParamsMap.newMap(Table.AppVesion.VERSION.name(), "DESC"));
        return new BaseResult(ReturnCode.OK, resultList.get(0));
    }

    @PostMapping(value = "uploadApp")
    public BaseResult uploadApp(@RequestHeader(Constants.USER_ID) String userId, @RequestParam MultipartFile apkFile, @RequestParam String appType, @RequestParam String appVer, @RequestParam String appContent, @RequestParam String updateCount) throws IOException {
        return commonService.uploadApk(appType, apkFile, appVer, appContent, updateCount);
    }

    @GetMapping("info")
    public Object getInfo(@RequestHeader(Constants.USER_ID) String userId, @RequestParam String id) {

        return new BaseResult(ReturnCode.OK);
    }

    @PostMapping("cjUser")
    public BaseResult cjUser(@RequestHeader(Constants.USER_ID) String userId, @EncryptProcess Page page) {
        List<Map<String, Object>> resultList = commonDao.queryCJUserPageMul(page);
        return new BaseResult(ReturnCode.OK, page);
    }

    @PostMapping("sbUser")
    public BaseResult sbUser(@RequestHeader(Constants.USER_ID) String userId, @EncryptProcess Page page) {
        List<Map<String, Object>> resultList = commonDao.querySBUserPageMul(page);
        return new BaseResult(ReturnCode.OK, page);
    }

    @PostMapping("dyhkUser")
    public BaseResult dyhkUser(@RequestHeader(Constants.USER_ID) String userId, @EncryptProcess Page page) {
        commonDao.queryDYHUserPageMul(page);
        return new BaseResult(ReturnCode.OK, page);
    }

    @PostMapping("dywhkUser")
    public BaseResult dywhkUser(@RequestHeader(Constants.USER_ID) String userId, @EncryptProcess Page page) {
        commonDao.queryDYWHUserPageMul(page);
        return new BaseResult(ReturnCode.OK, page);
    }

    @PostMapping("xzUser")
    public BaseResult xzUser(@RequestHeader(Constants.USER_ID) String userId, @EncryptProcess Page page) {
        commonDao.queryXZUserPageMul(page);
        return new BaseResult(ReturnCode.OK, page);
    }

    @PostMapping("dshUser")
    public BaseResult dshUser(@RequestHeader(Constants.USER_ID) String uId, @EncryptProcess Page page) {
        commonDao.queryDSHUserPageMul(page);
        return new BaseResult(ReturnCode.OK, page);
    }

    @PutMapping("edit")
    public Object updateUser(@RequestHeader(Constants.APP_VER) String appVer, @EncryptProcess ParamsVo params) {
        String tableName = appVer.startsWith(Constants.USER) ? userTable : saleTable;
        ParamsMap<String, Object> map = params.getParams();
        String phone = (String) map.get(Table.User.PHONE.name());
        int count = baseDao.updateByProsInTab(tableName, params.getParams());
        return count > 0 ? new BaseResult(ReturnCode.OK) : new BaseResult(ReturnCode.FAIL);
    }

    @PostMapping("passwd")
    public Object modifyPasswd(@RequestHeader(Constants.USER_ID) String uId, @RequestHeader(Constants.APP_VER) String appVer, @RequestParam String oldPasswd, @RequestParam String newPasswd) {
        String tableName = appVer.startsWith(Constants.USER) ? userTable : saleTable;
        Map<String, Object> userMap = baseDao.queryByIdInTab(tableName, uId);
        if (!StringUtils.isEmpty(newPasswd) && userMap.get("pwd").equals(oldPasswd)) {
            int count = baseDao.updateByProsInTab(tableName, ParamsMap.newMap(Table.User.PWD.name(), newPasswd).addParams(Table.ID, uId));
            return count > 0 ? new BaseResult(ReturnCode.OK) : new BaseResult(ReturnCode.FAIL);
        } else return new BaseResult(ReturnCode.FAIL);
    }

    @GetMapping("bill/{userId:[0-9]+}")
    public BaseResult billInfo(@RequestHeader(Constants.USER_ID) String uId,@PathVariable Long userId) {
        List<Map<String, Object>> resultList = commonDao.queryBillMul(userId);
        return new BaseResult(0, resultList);
    }

    /**
     *
     * @param uId
     * @param repayId       还款计划ID
     * @param periodNum     总期数
     * @return
     */
    @PostMapping("repay/{repayId:[0-9]+}")
    public BaseResult repay(@RequestHeader(Constants.USER_ID) String uId, @PathVariable Long repayId,@RequestParam(required = true) Long periodNum) {
        Map<String, Object> repayMap = baseDao.queryByIdInTab(Table.FQ + Table.PLAN_REPAYMENT, repayId);
        BaseResult baseResult = orderService.repay(repayMap);       //支付接口
        if (baseResult.getCode() == 0) {
            long diffSecond=System.currentTimeMillis()-((Date)repayMap.get("planrepayDate")).getTime();
            baseDao.updateByProsInTab(Table.FQ + Table.PLAN_REPAYMENT, ParamsMap.newMap(Table.PlanRepayment.STATUS.name(), diffSecond > 0 ? "1" : "2").addParams(Table.PlanRepayment.PAY_DATE,new Date()).addParams(Table.ID, repayId));
            String appMeta=Constants.hgetCache(CacheKey.APP_META_Prefix,JPushUtil.USER_APP+uId);
            if(periodNum==repayMap.get("repayNum")) {
                Long orderId= (Long) repayMap.get("orderId");
                baseDao.updateByProsInTab(Table.FQ + Table.ORDER, ParamsMap.newMap(Table.Order.STATE.name(), "10").addParams(Table.ID,orderId ));           //更新订单状态
                JsonObject jsonObject=new JsonObject();
                jsonObject.addProperty("orderId",orderId);
                JPushUtil.pushByRegId(JPushUtil.USER_APP,"您有一笔融宝分期订单已全部还款完成.","点击查看详情!",jsonObject,appMeta.split(Table.SEPARATE_SPLIT)[0]);
            }
            Calendar calendar=Calendar.getInstance();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("repayId",repayId);
            JPushUtil.pushByRegId(JPushUtil.USER_APP,"您"+calendar.get(Calendar.MONTH)+"月账单"+repayMap.get("planrepayMoney")+"元已还清!","点击查看详情:",jsonObject,appMeta.split(Table.SEPARATE_SPLIT)[0]);   //TODO:AppMeta
        }
        return baseResult;
    }

    @PostMapping("help")
    public BaseResult help(@RequestHeader(Constants.APP_VER)String appVer,@RequestHeader(Constants.USER_ID) String uId,@EncryptProcess Page page) {
        if(appVer.startsWith(Constants.USER))
            page.getParams().put("ht_APP_TYPE", "1");
        else if (appVer.startsWith(Constants.SALE)) {
            page.getParams().put("ht_APP_TYPE", "2");
        }
        commonDao.queryHelpPageMul(page);
        return new BaseResult(ReturnCode.OK,page);
    }

    @PostMapping("feedback")
    public BaseResult feedback(@RequestHeader(Constants.USER_ID) Long uId, @RequestHeader(Constants.USER_PHONE) String phone, @RequestParam(required = true) String ask) {
        baseDao.insertByProsInTab(Table.FQ + Table.FEEDBACK, ParamsMap.newMap(Table.Feedback.ASK.name(), ask).addParams(Table.Feedback.USER_ID.name(), uId).addParams(Table.Feedback.USER_PHONE.name(), phone).addParams(Table.Feedback.STATUS.name(), "0"));
        return new BaseResult(ReturnCode.OK);
    }

}
