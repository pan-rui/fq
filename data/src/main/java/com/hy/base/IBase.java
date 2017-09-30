package com.hy.base;

import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by lenovo on 2014/12/8.
 */
public interface IBase {
    //    @Resource
//    protected JedisPool jedisPool;
//    protected Logger logger = Logger.getLogger(this.getClass());
//    public static ApplicationContext applicationContext;
//    public static String webPath;
//    public static ApplicationContext otherApplicationContext;
//    public static String jsp_classpath;
//    public static ApplicationContext getApplicationContext(HttpServletRequest request) {
////        return applicationContext==null?applicationContext=WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext()):applicationContext;
//        return Base.applicationContext;
//    }
    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    default BaseResult getFormatError(BindingResult result) {
        List<FieldError> fields = result.getFieldErrors();
        StringBuffer errors = new StringBuffer();
        for (FieldError fieldError : fields) {
            if (!StringUtils.isEmpty(fieldError.getDefaultMessage()))
                errors.append(MessageFormat.format(fieldError.getDefaultMessage(), fieldError.getField(), fieldError.getRejectedValue()));
        }
        return new BaseResult(1, errors.toString());
    }
    //日期格式化为常用格式
/*    static String formatDate(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

    static String formatDateTime(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(date);
    }*/


}
