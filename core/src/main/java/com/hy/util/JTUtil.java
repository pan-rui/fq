package com.hy.util;

import com.alibaba.fastjson.JSON;
import com.hy.base.BaseResult;
import com.hy.base.IBase;
import com.hy.base.ReturnCode;
import com.hy.core.Constants;
import com.hy.core.ParamsMap;
import com.hy.core.Table;
import com.hy.dao.BaseDao;
import com.hy.dao.CommonDao;
import com.hy.util.DateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JTUtil {
    public static final String DEF_CHATSET = "UTF-8";
    public static final String DEF_SECRET = "thKjfwFEnBQliQAK";
    public static final String DEF_URL = "https://phjr.jswre.com:6372/transaction/body/request";
    public static final String DEF_CHANNEL = "PLA";
    public static final String COOPER = "2017120100000001";
    public static final int DEF_CONN_TIMEOUT = 30000;
    public static final int DEF_READ_TIMEOUT = 30000;
    public static String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";
    private final static Logger logger = LogManager.getLogger(JTUtil.class);
    private static Method zhdesMethod;
    private static Object zhdes;
    @Value("#{config['FTP_HOST']}")
    private String ftpHost;
    @Value("#{config['FTP_PORT']}")
    private int ftpPort;
    @Value("#{config['FTP_USER']}")
    private String ftpUser;
    @Value("#{config['FTP_PWD']}")
    private String ftpPwd;

    static {
        getZhdesMethod();
    }

    public static Method getZhdesMethod() {
        try {
            if (zhdes == null) zhdes = Class.forName("ZhptDes").newInstance();
            return zhdesMethod == null ? (zhdesMethod = zhdes.getClass().getMethod("JtDes", int.class, String.class, String.class)) : zhdesMethod;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return zhdesMethod;
    }


    public static String net(String strUrl, Map<String, Object> params) throws IOException {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String rs = null;
        try {
            StringBuffer sb = new StringBuffer();
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("User-agent", userAgent);
            conn.setUseCaches(false);
            conn.setConnectTimeout(DEF_CONN_TIMEOUT);
            conn.setReadTimeout(DEF_READ_TIMEOUT);
            conn.setInstanceFollowRedirects(false);

            conn.connect();
            try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
                String jsonParams = JSON.toJSONString(params);
                String length = String.format("%06d", jsonParams.length());
                out.writeBytes(length + jsonParams);
            }
            InputStream is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sb.append(strRead);
            }
            rs = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rs;
    }

    public static Map<String, Object> getSecret(String serialNo) throws Exception {         //获取keyvlue
        ParamsMap reqMap = ParamsMap.newMap("body", "");
        reqMap.addParams("head", ParamsMap.newMap("transcode", "810002").addParams("transdate", IBase.dateSdf2.format(new Date())).addParams("transchannel", DEF_CHANNEL).addParams("transserialno", serialNo));
        String resultStr = net(DEF_URL, reqMap);
        return verifyAndParse(DEF_SECRET, resultStr);        //若尾部有乱码还需截取
    }

    public static Map<String, Object> sendUserInfo(final Map<String, Object> maps, final String serialNo, final String keyStr) throws Exception {
        Calendar calendar = Calendar.getInstance();
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> map = (Map<String, Object>) maps.get("user");
        Map<String, Object> companyMap = (Map<String, Object>) maps.get("company");
        Map<String, Object> userDetailMap = (Map<String, Object>) maps.get("userDetail");
        params.put("CUSTOMERNAME", map.get("name"));       //姓名
        params.put("CUSTOMERTYPE", "030");           //类型
        params.put("CERTTYPE", "0");           //0:身份证
        String cardNo = (String) map.get("cardNo");
        params.put("CERTID", cardNo);           //身份证号
        params.put("SEX", (cardNo.charAt(16)&1)==1 ? "010" : "020");           //性别
        String yearStr = cardNo.substring(6, 10);
        String monthStr = cardNo.substring(10, 12);
        String dayStr = cardNo.substring(12, 14);
        params.put("BIRTHDAY", yearStr + "/" + monthStr + "/" + dayStr);           //生日
        int age = calendar.get(Calendar.YEAR) - Integer.parseInt(yearStr);
        params.put("AGE", String.valueOf(age));           //年龄
        params.put("NATIONALITY", "001");           //民族
        params.put("MOBILETELEPHONE", map.get("phone"));           //手机号
        params.put("MARRIAGE", "010");           //婚姻状况
        params.put("POPULATION", "4");           //家庭人口数
        params.put("EDUEXPERIENCE", "30");           //最高学历
        params.put("EDUDEGREE", "9");           //学位
        params.put("INDUSTRYTYPE", "S94");           //所属职业
        params.put("TEAMNO", "00018501");           //镇村组代码
        params.put("TEAMNAME", "一组");           //镇村名称
        params.put("FAMILYADD", "广东省深圳市");           //家庭住址
        params.put("FAMILYZIP", "518000");           //家庭地址邮编
//        params.put("COMMADD", "S94");           //所属职业
        params.put("COMMZIP", "518000");           //通讯地址邮编
        String companyName=CollectionUtils.isEmpty(companyMap)?null:(String)companyMap.get("companyName");
        params.put("WORKCORP", StringUtils.isEmpty(companyName)?"深圳恒雍":companyName);           //所在公司
        params.put("WORKBEGINDATE", "2016");           //入职时间
        params.put("SELFMONTHINCOME", "7500");           //工资收入
        //TEAMNAME,FAMILYADD,FAMILYZIP,COMMZIP,EDUDEGREE,WORKCORP,INDUSTRYTYPE,WORKBEGINDATE
        if ((boolean) map.get("containsJt")) {
            params.put("ISBANKCARD", "1");           //是否有九台卡
            params.put("RELATEDACCOUNT", map.get("bankCard"));
        } else {
            params.put("ISBANKCARD", "2");           //是否有九台卡
            params.put("NOBANKCARD", map.get("bankCard"));
        }
        ParamsMap reqMap = ParamsMap.newMap("body", zhdesMethod.invoke(zhdes, 0, keyStr, JSON.toJSONString(params)));
        reqMap.addParams("head", ParamsMap.newMap("transcode", "810000").addParams("transdate", IBase.dateSdf2.format(calendar.getTime())).addParams("transchannel", DEF_CHANNEL).addParams("transserialno", serialNo));
        String resultStr = net(DEF_URL, reqMap);
        return verifyAndParse(keyStr, resultStr);        //若尾部有乱码还需截取
    }

    public static BaseResult check(final Map map, String serialNo, String keyStr, String customId) {
        Calendar calendar = Calendar.getInstance();
        Map<String, Object> params = new HashMap<>();
        params.put("CustomerID", customId);
        params.put("BUSINESSTYPE", "200005");
        params.put("BUSINESSCURRENCY", "01");
        params.put("BUSINESSSUM", 5000);
        params.put("TERMMONTH", 12);                //分期月数
        params.put("RATEKIND", "010");
        params.put("INTERESTRATECHANGE", "3");
        params.put("OCCURDATE", IBase.dateSdf2.format(calendar.getTime()));
        params.put("CORPUSPAYMETHOD", "010");
        params.put("PAYSOURCE", "工资收入");
        params.put("ICTYPE", "1");
        params.put("VOUCHTYPE", "005");
        params.put("BELONGCHANNEL", COOPER);            //所属合作方
        params.put("CERTIDIDCARDFILE1", map.get("0"));
        params.put("CERTIDIDCARDFILE2", map.get("1"));
        params.put("CERTIDCARDFILE", map.get("9"));
        try {
            ParamsMap reqMap = ParamsMap.newMap("body", zhdesMethod.invoke(zhdes, 0, keyStr, JSON.toJSONString(params)));
            reqMap.addParams("head", ParamsMap.newMap("transcode", "810001").addParams("transdate", IBase.dateSdf2.format(calendar.getTime())).addParams("transchannel", DEF_CHANNEL).addParams("transserialno", serialNo));
            String resultStr = net(DEF_URL, reqMap);
//            Map<String,Object> resultMap=verifyAndParse(keyStr, resultStr);        //若尾部有乱码还需截取
            return new BaseResult(ReturnCode.OK);               //TODO:返回为空
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("九台资质审核接口调用出错......");
        }
        return new BaseResult(ReturnCode.FAIL);
    }

    public static Map<String, Object> verifyAndParse(String secret, String resultStr) throws Exception {
        if (Integer.parseInt(resultStr.substring(0, 6)) != resultStr.length() - 6)
            return null;
        Map<String, Object> resultMap = JSON.parseObject(resultStr.substring(6), Map.class);

        String bodyStr = (String) zhdesMethod.invoke(zhdes, 1, secret, (String) resultMap.get("body"));
        int index = bodyStr.lastIndexOf("}");
        return JSON.parseObject(bodyStr.substring(0, index + 1), Map.class);
    }

    public static BaseResult cert(Object userId, String phone) throws Exception {
        BaseDao baseDao = (BaseDao) Constants.applicationContext.getBean("baseDao");
        CommonDao commonDao = (CommonDao) Constants.applicationContext.getBean("commonDao");
        Map<String, Object> userMap = commonDao.queryUserInfoMul(userId,null);
        String serialNo = "CPI"+IBase.sdf2.format(new Date())+((Map)userMap.get("user")).get("phone") ;
        Map<String, Object> secretMap = getSecret(serialNo);
        if (CollectionUtils.isEmpty(secretMap)) return null;
        String secret = (String) secretMap.get("keyvlue");
        if (StringUtils.isEmpty(secret)) return null;
        Map<String, Object> userReturn = sendUserInfo(userMap, serialNo, secret);         //CustomerID
        String customId = (String) userReturn.get("CustomerID");
        if (StringUtils.isEmpty(customId)) return null;
        List<Map<String, Object>> userAttachs = baseDao.queryByS("select URL,ATTACH_TYPE from fq.USER_ATTACH where ATTACH_TYPE in ('0','1','9') and USER_ID=" + userId);
        String cardUpPath = ImgUtil.JT_FTP_PATH + phone + "/sfzzm_" + customId, cardDownPath = ImgUtil.JT_FTP_PATH + phone + "/sfzfm_" + customId, photoPath = ImgUtil.JT_FTP_PATH + phone + "/scsfz_" + customId;
//        int indexOf = (ImgUtil.USER_CERT_PATH + phone).length();
        FtpUtil ftpUtil = new FtpUtil("UTF-8");
        ftpUtil.connect((String) Constants.config.get("FTP_HOST"), Integer.parseInt(String.valueOf(Constants.config.get("FTP_PORT"))), (String) Constants.config.get("FTP_USER"), (String) Constants.config.get("FTP_PWD"));
        ftpUtil.setTimeOut(60, 60, 60);
        ftpUtil.makeDirectory(ImgUtil.JT_FTP_PATH + phone);
        for (Map attach : userAttachs) {
            String pp = String.valueOf(attach.get("URL"));
            switch (String.valueOf(attach.get("ATTACH_TYPE"))) {
                case "0":
                    cardUpPath += pp.substring((pp.lastIndexOf(Table.SEPARATE)));
                    ftpUtil.upload(cardUpPath, new File(ImgUtil.BASE_PATH + pp));
                    userMap.put("0", cardUpPath);
                    break;
                case "1":
                    cardDownPath += pp.substring((pp.lastIndexOf(Table.SEPARATE)));
                    ftpUtil.upload(cardDownPath, new File(ImgUtil.BASE_PATH + pp));
                    userMap.put("1", cardDownPath);
                    break;
                case "9":
                    photoPath += pp.substring((pp.lastIndexOf(Table.SEPARATE)));
                    ftpUtil.upload(photoPath, new File(ImgUtil.BASE_PATH + pp));
                    userMap.put("9", photoPath);
                    break;
            }
        }
        return check(userMap, serialNo, secret, customId);
    }
}
