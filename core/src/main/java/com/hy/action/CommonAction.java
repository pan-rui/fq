package com.hy.action;

import com.alibaba.fastjson.JSONArray;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.google.gson.JsonObject;
import com.hy.annotation.EncryptProcess;
import com.hy.base.BaseResult;
import com.hy.base.IBase;
import com.hy.base.ReturnCode;
import com.hy.core.CacheKey;
import com.hy.core.ColumnProcess;
import com.hy.core.Constants;
import com.hy.core.Page;
import com.hy.core.ParamsMap;
import com.hy.core.Table;
import com.hy.dao.BaseDao;
import com.hy.dao.CommonDao;
import com.hy.dao.UserDao;
import com.hy.service.CommonService;
import com.hy.service.OrderService;
import com.hy.service.UserService;
import com.hy.util.AliUtil;
import com.hy.util.ImageCode;
import com.hy.util.ImgUtil;
import com.hy.util.JPushUtil;
import com.hy.util.JTUtil;
import com.hy.util.SendMail;
import com.hy.vo.ParamsVo;
import org.apache.ibatis.binding.MapperMethod;
import org.aspectj.apache.bcel.generic.RET;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
    private UserDao userDao;
    @Autowired
    private CommonService commonService;
    @Autowired
    private OrderService orderService;
    @Value("#{config['sessionExpireTime']}")
    private int sessionExpire;
    @Value("#{config['adminEmail']}")
    private String adminEmail;
    @Autowired
    private SendMail sendMail;
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
        JSONArray jsonArray = new JSONArray();
        String fileType = request.getParameter("fileType");
        boolean flag=StringUtils.isEmpty(fileType);
        List<Map<String, Object>> attachs = new ArrayList<>();
        for (int i = 0; i < fileMap.size(); i++) {
            MultipartFile file = fileMap.get("file" + i).get(0);
            if(flag)
                fileType = request.getParameter("fileType" + i);
            String relativelyPath = ImgUtil.pathMap.get(fileType);
            new File(ImgUtil.BASE_PATH + relativelyPath).mkdirs();
            String fileName = file.getOriginalFilename();
            String path = relativelyPath + UUID.randomUUID().toString() + "." + fileName.split("\\.")[1];
            File newFile = new File(ImgUtil.BASE_PATH + path);
            file.transferTo(newFile);
            jsonArray.add(path);
            attachs.add(ParamsMap.newMap(Table.UserAttach.ATTACH_TYPE.name(), fileType).addParams(Table.UserAttach.URL.name(), path).addParams(Table.UserAttach.ATTACH_LEN.name(), newFile.length())
                    .addParams(Table.UserAttach.UP_ID.name(), userId).addParams(Table.UserAttach.USER_ID.name(), userId).addParams(Table.UserAttach.IS_ENABLE.name(), 1));
        }
        if(flag)
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
        return "OK".equals(sendSmsResponse.getCode()) ? new BaseResult(ReturnCode.OK) : (sendSmsResponse.getCode().equals("isv.BUSINESS_LIMIT_CONTROL") ? new BaseResult(1235, "短信发送频繁,请稍后再试") : new BaseResult(10000001, sendSmsResponse.getMessage()));
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
        int count = baseDao.updateByProsInTab(userTable, paramsVo.getParams().addParams(Table.User.BANK_MOBILE.name(), bankPhone).addParams(Table.ID, paramsVo.getParams().remove("USER_ID")));
        return count > 0 ? new BaseResult(ReturnCode.OK) : new BaseResult(ReturnCode.FAIL);
    }

    @PostMapping("certS2")
    public BaseResult certStep2(@RequestHeader(Constants.USER_ID) String userId, @RequestHeader(Constants.USER_PHONE) String phone, @RequestParam("cardUp") MultipartFile cardUp,
                                @RequestParam("cardDown") MultipartFile cardDown, @RequestParam("card") MultipartFile card) throws IOException, ServletException {
//        if (StringUtils.isEmpty(phone)) return new BaseResult(ReturnCode.HEADER_PARAMS_VERIFY_ERROR);
        return new BaseResult(ReturnCode.OK, userService.updateCert(userId, phone, cardUp, cardDown, card));
    }

    @PostMapping("certS3")
    public BaseResult certStep3(@RequestHeader(Constants.USER_ID) String uId, @RequestHeader(Constants.USER_PHONE) String phone, @EncryptProcess ParamsVo paramsVo) {
        return userService.submitCert(phone, paramsVo);
    }

    @PostMapping("certS4")
    public BaseResult certStep4(@RequestHeader(Constants.USER_ID) String userId, @RequestHeader(Constants.USER_PHONE) String phone) {
        String status = "3";
        //TODO:通知审核人员,审核成功后推送给用户,
//        BaseResult baseResult = JTUtil.cert(userId, phone);
//        if (baseResult.getCode() == 0) {            //,,,,默认审核通过
//        status = "3";
        String certUserId = Constants.getSystemStringValue("CERT_USER_ID");
        String appMeta = Constants.hgetCache(CacheKey.APP_META, JPushUtil.SALE_APP + certUserId);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", userId);
        JPushUtil.submitTask(() -> JPushUtil.pushByRegId(JPushUtil.SALE_APP, "有一项用户分期资质审核任务待完成", "用户号码:" + phone, jsonObject, appMeta.split(Table.SEPARATE_SPLIT)[0]));      //TODO:AppMeta
//        }
/*            status = "4";
        } else status = "3";*/
        int cou = baseDao.updateByProsInTab(userTable, ParamsMap.newMap(Table.User.CERT_STATUS.name(), status).addParams(Table.User.UP_ID.name(), userId).addParams(Table.User.UTIME.name(), new Date()).addParams(Table.ID, userId));
        Constants.setCacheOnExpire(CacheKey.U_CERT_STATUS_Prefix + phone, status, UserService.certStatusExpire);
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
    public BaseResult userInfo(@RequestHeader(value = Constants.USER_ID, required = false) String uId, @RequestParam(required = false) Long userId, @RequestParam(required = false) String openId) {
        return new BaseResult(ReturnCode.OK, commonDao.queryUserInfoMul(userId, openId));
    }

    @GetMapping("updateApp")
    public BaseResult updateApp(@RequestHeader(Constants.APP_VER) String appVer) {
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
    public Object updateUser(@RequestHeader(Constants.USER_ID) Long uId, @RequestHeader(Constants.APP_VER) String appVer, @EncryptProcess ParamsVo params) {
        String tableName = appVer.startsWith(Constants.USER) ? userTable : saleTable;
        ParamsMap<String, Object> map = params.getParams();
        Object id = params.getParams().remove(Table.ID);
        if (id == null)
            params.getParams().put(Table.ID, uId);
        else params.getParams().put(Table.ID, id);
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
    public BaseResult billInfo(@RequestHeader(Constants.USER_ID) String uId, @PathVariable Long userId) {
        List<Map<String, Object>> resultList = commonDao.queryBillMul(userId);
        return new BaseResult(0, resultList);
    }

    /**
     * @param uId
     * @param repayId    还款计划ID
     * @param repayId    payType
     * @param bankCardNo
     * @return
     */
    @PostMapping("repay/{repayId:[0-9]+}")
    public BaseResult repay(@RequestHeader(Constants.USER_ID) String uId, @PathVariable Long repayId, @RequestParam String payType, @RequestParam(required = false) String bankCardNo) {
        Map<String, Object> repayMap = baseDao.queryByIdInTab(Table.FQ + Table.PLAN_REPAYMENT, repayId);
        BaseResult baseResult = orderService.repay(payType, repayMap);       //支付接口
        return baseResult;
    }

    /**
     * @param uId
     * @param userId
     * @param date   年-月    (amount<=当月应还总金额)
     * @param amount
     * @return
     */
    @PostMapping("freeRepay")
    public BaseResult freeRepay(@RequestHeader(Constants.USER_ID) String uId, @RequestParam Long userId, @RequestParam String date, @RequestParam String amount, @RequestParam String payType, @RequestParam(required = false) String bankCardNo) {
        BaseResult baseResult = orderService.freeRepay(payType, ParamsMap.newMap("userId", userId).addParams("amount", amount).addParams("date", date));          //TODO:支付接口
        return baseResult;
    }

    @PostMapping("help")
    public BaseResult help(@RequestHeader(Constants.APP_VER) String appVer, @RequestHeader(Constants.USER_ID) String uId, @EncryptProcess Page page) {
        if (appVer.startsWith(Constants.USER))
            page.getParams().put("ht_APP_TYPE", "1");
        else if (appVer.startsWith(Constants.SALE)) {
            page.getParams().put("ht_APP_TYPE", "2");
        }
        commonDao.queryHelpPageMul(page);
        return new BaseResult(ReturnCode.OK, page);
    }

    @PostMapping("feedback")
    public BaseResult feedback(@RequestHeader(Constants.USER_ID) Long uId, @RequestHeader(Constants.USER_PHONE) String phone, @RequestParam(required = true) String ask) throws UnsupportedEncodingException {
        baseDao.insertByProsInTab(Table.FQ + Table.FEEDBACK, ParamsMap.newMap(Table.Feedback.ASK.name(), URLDecoder.decode(ask, IBase.DEF_CHATSET)).addParams(Table.Feedback.USER_ID.name(), uId).addParams(Table.Feedback.USER_PHONE.name(), phone).addParams(Table.Feedback.STATUS.name(), "0"));
        return new BaseResult(ReturnCode.OK);
    }

    @GetMapping("userBanks")
    public BaseResult userBanks(@RequestHeader(Constants.USER_ID) Long uId) {
        List<Map<String, Object>> resultList = baseDao.queryByProsInTab(Table.FQ + Table.USER_BANK, ParamsMap.newMap(Table.USER_ID, uId));
        return new BaseResult(ReturnCode.OK, resultList);
    }

    @DeleteMapping("userBanks/{id}")
    public BaseResult delUserBanks(@RequestHeader(Constants.USER_ID) Long uId, @PathVariable Long id) {
        int i = baseDao.deleteByProsInTab(Table.FQ + Table.USER_BANK, ParamsMap.newMap(Table.ID, id));
        return i > 0 ? new BaseResult(ReturnCode.OK) : new BaseResult(ReturnCode.FAIL);
    }

    @PostMapping("bindBank")
    public BaseResult bindBank(@RequestHeader(Constants.APP_VER) Long appVer, @EncryptProcess ParamsVo paramsVo) {
        return userService.bindBank(paramsVo);
    }

    @GetMapping("repayRecord")
    public BaseResult repayRecord(@RequestHeader(Constants.USER_ID) Long uId, @RequestParam Long userId, @RequestParam String date) {
        return new BaseResult(0, commonDao.queryRepayRecordTab(userId, date));
    }

    @GetMapping("allClassify")
    public BaseResult allClassify() {
        return new BaseResult(0, commonService.allMultilLevel(Table.FQ + Table.CLASSIFY, ParamsMap.newMap(Table.IS_ENABLE, 1), "children"));
    }

    @GetMapping("hotClassify")
    public BaseResult hotClassify() {
/*        Page page = new Page();
        page.setPageSize(4);
        page.setParams(ParamsMap.newMap(Table.IS_ENABLE, 1));
        page.setOrderMap(ParamsMap.newMap(Table.SEQ, Table.ASC));*/
        return new BaseResult(0, baseDao.queryListSufInTab(Table.FQ + Table.CLASSIFY, ParamsMap.newMap(Table.IS_ENABLE, 1), ParamsMap.newMap(Table.SEQ, Table.ASC), "LIMIT 4"));
    }

    @GetMapping("hotBrand")
    public BaseResult hotBrand(@RequestHeader(Constants.APP_VER) String hyAV, @RequestParam Long classifyId) {
        Page page = new Page();
        page.setPageSize(4);
        page.setParams(ParamsMap.newMap(Table.IS_ENABLE, 1));
        if (classifyId != null)
            page.setMatchs(ParamsMap.newMap(Table.Brand.CLASSIFY_LIST_ID.name(), classifyId));
        return new BaseResult(0, baseDao.queryPageInTab(Table.FQ + Table.BRAND, page));
    }

    @PostMapping("certOne")
    public Object certOne(@RequestHeader(Constants.USER_ID) String uId, @RequestHeader(Constants.USER_PHONE) String phone, @EncryptProcess ParamsVo paramsVo) throws Exception {
        int count = baseDao.updateByProsInTab(userTable, paramsVo.getParams().addParams(Table.ID, uId));
        if (count <= 0) return new BaseResult(ReturnCode.FAIL);
        paramsVo.getParams().addParams(Table.UserBank.BANK_CARD_NO.name(), paramsVo.getParams().get(Table.User.BANK_CARD.name()));
        paramsVo.getParams().addParams(Table.USER_ID, uId);
        BaseResult baseResult = userService.submitCert((String) paramsVo.getParams().get(Table.User.BANK_MOBILE.name()), paramsVo);
        if (baseResult.getCode() == 0)
            certStep4(uId, phone);
        return baseResult;
    }

    @GetMapping("sms")
    public BaseResult checkSmsCode(@RequestParam String code, @RequestParam String phone) {
        if (StringUtils.isEmpty(code) || !code.equals(Constants.getCache(CacheKey.U_SMS_Prefix + phone)))
            return new BaseResult(103, "短信验证码错误");
        return new BaseResult(ReturnCode.OK);
    }

    @PostMapping("coupons")
    public BaseResult getCoupons(@RequestHeader(Constants.USER_ID) String uId, @RequestHeader(Constants.APP_VER) String appVer, @EncryptProcess Page page) {
        return new BaseResult(ReturnCode.OK, commonDao.queryCouponPageMul(page));
    }

    @GetMapping("coupons")
    public BaseResult getValidCoupons(@RequestHeader(Constants.USER_ID) String uId, @RequestHeader(Constants.APP_VER) String appVer) {
        return new BaseResult(ReturnCode.OK, commonDao.queryValidCoupon(uId));
    }

    /**
     * 限定ID值的保险单
     *
     * @param appVer
     * @param ids
     * @return
     */
    @GetMapping("insureList")
    public BaseResult geProducttInsure(@RequestHeader(Constants.APP_VER) String appVer, @RequestParam String ids) {
        List<Map<String, Object>> insureList = baseDao.queryBySql("select * from " + Table.FQ + Table.PRODUCT_INSURANCE + " where ID in (" + ids + ")");
        return new BaseResult(ReturnCode.OK, insureList);
    }

    @PostMapping("userInsurance")
    public BaseResult userInsurancePage(@RequestHeader(Constants.APP_VER) String appVer, @RequestHeader(Constants.USER_ID) String uId, @EncryptProcess Page page) {
        return new BaseResult(ReturnCode.OK, commonDao.queryUserInsurancdPageMul(page));
    }

    @GetMapping("remainTime")
    public BaseResult remainTime(@RequestHeader(Constants.APP_VER) String appVer, String tag) {
        List<Map<String, Object>> couponDicts = baseDao.queryByProsInTab(Table.FQ + Table.COUPON_DICT, ParamsMap.newMap(Table.CouponDict.COUPON_NAME.name(), tag));
        if (CollectionUtils.isEmpty(couponDicts)) return new BaseResult(10999, "没有找到该活动");
//        Date expireDate = (Date) couponDicts.get(0).get("expireDate");
        Date ctime = (Date) couponDicts.get(0).get("ctime");
        return new BaseResult(ReturnCode.OK, ctime.getTime() - System.currentTimeMillis());
    }

    @GetMapping("notifyShip")
    public BaseResult notifyShip(@RequestHeader(Constants.APP_VER) String appVer, @RequestHeader(Constants.USER_ID) Object uId, @RequestHeader(Constants.USER_PHONE) String uPhone, @RequestParam String oId) {
        sendMail.sendEmail(adminEmail, uPhone + "----提醒发货,订单ID:" + oId, "恒雍.");
        return new BaseResult(ReturnCode.OK);
    }

    @GetMapping("certAttach/{userId:[0-9]+}")
    public BaseResult getCertAttach(@RequestHeader(Constants.APP_VER) String appVer, @RequestHeader(Constants.USER_ID) Object uId, @PathVariable Long userId) {
        return new BaseResult(ReturnCode.OK, userDao.queryUserCertTab(userId));
    }

    @PostMapping("laborCert")
    public BaseResult laborCert(@RequestHeader(Constants.APP_VER) String appVer, @RequestHeader(Constants.USER_ID) Object uId, @EncryptProcess ParamsVo paramsVo) throws Exception {
        String status = (String) paramsVo.getParams().get(Table.User.CERT_STATUS.name());
        Object userId = paramsVo.getParams().remove(Table.USER_ID);
        String phone = (String) paramsVo.getParams().remove(Table.User.PHONE.name());
        if ("5".equals(status)) {   //一级审核通过
            BaseResult baseResult = JTUtil.cert(userId, phone);
            if (baseResult.getCode() == 0) {            //,,,,默认(二级)审核通过
                status = "7";
            } else status = "6";
        }
        baseDao.updateByProsInTab(userTable, ParamsMap.newMap(Table.User.CERT_STATUS.name(), status).addParams(Table.User.UP_ID.name(), uId).addParams(Table.User.UTIME.name(), new Date()).addParams(Table.ID, userId));
        Constants.setCacheOnExpire(CacheKey.U_CERT_STATUS_Prefix + phone, status, UserService.certStatusExpire);
        return new BaseResult(ReturnCode.OK);
    }

    @GetMapping("properties")
    public BaseResult getConfigProperties(@RequestHeader(Constants.APP_VER) String appVer, @RequestParam(required = true) String pors) {
        Map<String, Object> prosMap = new HashMap<>();
        for (String pro : pors.split(Table.SEPARATE_SPLIT)) {
            prosMap.put(pro, Constants.getSystemStringValue(ColumnProcess.decryptVal(pro)));
        }
        return new BaseResult(ReturnCode.OK, prosMap);
    }

    @PostMapping("productDiscuss")
    public BaseResult getProductDiscuss(@RequestHeader(Constants.APP_VER) String appVer, @EncryptProcess Page page) {
        commonDao.queryProductDiscussPageMul(page);
        return new BaseResult(0, page);
    }

    @PostMapping("productDiscuss/{id:[0-9]+}/{operate:[01234]}")
    public BaseResult OperateProductDiscuss(@RequestHeader(Constants.APP_VER) String appVer,@RequestHeader(Constants.USER_ID) Long uId,@PathVariable Long id,@PathVariable int operate,@RequestParam(defaultValue = "1000000")Integer index) {
        int count = commonDao.operateProductDiscuss(operate, uId, id,index);
        String fieldKey = operate == 0 ? Table.ProductDiscuss.OPPOSE_SIZE.name() : (operate == 1 ? Table.ProductDiscuss.APPROVED_SIZE.name() : null);
        Object data=null;
        if(!StringUtils.isEmpty(fieldKey))
            data = baseDao.queryBySOne("select " + fieldKey + " size from fq.PRODUCT_DISCUSS where ID=" + id);
//            data=baseDao.queryJsonSize(Table.FQ+Table.PRODUCT_DISCUSS,fieldKey,"$", ParamsMap.newMap(Table.ID,id));
        return new BaseResult(ReturnCode.OK,data);
    }

}
