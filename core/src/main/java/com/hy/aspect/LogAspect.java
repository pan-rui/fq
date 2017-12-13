package com.hy.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hy.action.BaseAction;
import com.hy.core.Constants;
import com.hy.core.DataSourceHolder;
import com.hy.core.Page;
import com.hy.core.ParamsMap;
import com.hy.core.Table;
import com.hy.core.DataSource;
import com.hy.annotation.OperationLog;
import com.hy.dao.BaseDao;
import com.hy.util.SendMail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2014/12/18. 用户操作记录和管理员操作记录
 */
@Component
@Aspect
public class LogAspect extends BaseAction {
    @Autowired
    private BaseDao baseDao;
    /*
     *
     * @Autowired private UserActionLogDao userActionLogDaoImpl;
     */
    private Logger logger = LogManager.getLogger(LogAspect.class);
    @Autowired
    private SendMail sendMail;
    @Value("#{config[adminEmail]}")
    private String adminEmail;

    private ThreadLocal<String> method = new ThreadLocal<>();

    @Pointcut("@annotation(com.hy.annotation.TradeLog)")
    public void TradeLog() {
    }

    @Pointcut("@annotation(com.hy.annotation.OperationLog))")
    public void operationLog() {
    }

    // @Pointcut("@annotation(DataSource)")
    @Pointcut("execution(* com.hy.dao..*Dao*.*(..)) && @annotation(com.hy.core.DataSource)")
    public void dataSource() {
    }

    @Pointcut("execution(* com.hy.service..*.get*(..)) || execution(* com.hy.service..*.query*(..))")
    public void serviceSource() {
    }

    @Pointcut("execution(* com.hy.dao..*Dao.*PageMul(..))")
    public void paramsConvert() {
    }

/*	@Pointcut("")
    public void serviceSource2() {
	}*/

    @Before("TradeLog()")
    public void doBefore(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        Object user = request.getSession().getAttribute("user");
        Integer userId = null;
        if (user == null) {
            Object[] obj = joinPoint.getArgs();
            if (obj.length != 1 || !(obj[0] instanceof Map))
                return;
            Map params = (Map) obj[0];
            userId = Integer.parseInt(
                    String.valueOf(params.get("userId") == null ? params.get("user_id") : params.get("userId")));// 获取当前用户ID
        }
        // else userId= Integer.parseInt(String.valueOf(((AccountUserDo)
        // user).getId()));
        String ip = getIpAddr(request);
		/*
		 * 根据访问路径确定是用户操作记录还是管理员操作记录 SystemLog log = new SystemLog();
		 * log.setDescription(getMethodDescription(joinPoint,0));
		 * log.setMethod(joinPoint.getTarget().getClass().getName() + "." +
		 * joinPoint.getSignature().getName() + "()");
		 * log.setLog_type(Byte.valueOf("0")); log.setRequest_ip(ip);
		 * log.setException_code(null); log.setException_detail(null);
		 * log.setUser_id(userId);
		 * log.setParams(JSON.toJSONString(joinPoint.getArgs()));
		 * log.setCtime(Calendar.getInstance().getTime());
		 * systemLogDao.save(log);
		 */
    }

    @AfterThrowing(pointcut = "TradeLog()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable e) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        Object user = request.getSession().getAttribute("user");
        Integer userId = null;
        if (user == null) {
            Object[] obj = joinPoint.getArgs();
            if (obj.length != 1 || !(obj[0] instanceof Map))
                return;
            Map params = (Map) obj[0];
            userId = Integer.parseInt(
                    String.valueOf(params.get("userId") == null ? params.get("user_id") : params.get("userId")));// 获取当前用户ID
        }
        // else userId= Integer.parseInt(String.valueOf(((AccountUserDo)
        // user).getId()));
        String ip = getIpAddr(request);
        String params = JSONArray.toJSONString(joinPoint.getArgs());
		/*
		 * SystemLog log = new SystemLog();
		 * log.setDescription(getMethodDescription(joinPoint,1));
		 * log.setMethod(joinPoint.getTarget().getClass().getName() + "." +
		 * joinPoint.getSignature().getName() + "()");
		 * log.setLog_type(Byte.valueOf("1")); log.setRequest_ip(ip);
		 * log.setException_code(e.getClass().getName());
		 * log.setException_detail(e.getMessage()); log.setUser_id(userId);
		 * log.setParams(JSON.toJSONString(joinPoint.getArgs()));
		 * log.setCtime(Calendar.getInstance().getTime());
		 * systemLogDao.save(log);
		 */
        logger.error(MessageFormat.format("异常方法:{}异常代码:{}异常信息:{}参数:{}",
                joinPoint.getTarget().getClass().getName() + joinPoint.getSignature().getName(), e.getClass().getName(),
                e.getMessage(), params));
    }

    /*	@AfterReturning(pointcut = "operationLog()", returning = "returnObj")
        public void doAfterReturn(JoinPoint joinPoint, Object returnObj) {*/
    @Before("operationLog()")
    public void logBefore(JoinPoint joinPoint) {
        MethodSignature sig = (MethodSignature) joinPoint.getSignature();
        Method method = sig.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();
        HttpServletRequest request = (HttpServletRequest) args[0];
        String uri = request.getRequestURI();
        Map<String, Object> argMap = new LinkedHashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (!(args[i] instanceof ServletRequest) && !(args[i] instanceof ServletResponse))
                argMap.put(parameters[i].getName(), args[i]);
        }
        List<String> descriptions = Arrays.asList(method.getAnnotation(OperationLog.class).value());
        List<String> tables = Arrays.asList(method.getAnnotation(OperationLog.class).table());
//		String username = (String) ((Map) SecurityUtils.getSubject().getPrincipal()).get("username");
//		String username = (String) request.getAttribute(Constants.USER_PHONE);
//		String tenantId=(String) ((Map) SecurityUtils.getSubject().getPrincipal()).get("tenantId");
//		String tenantId=(String) request.getHeader(Constants.TENANT_ID);
//		String ddBB=(String) ((Map) SecurityUtils.getSubject().getPrincipal()).get("ddBB");
//		String ddBB=(String) request.getAttribute(Constants.DDBB);
        String fromUrl = request.getHeader("Referer");
//		String appVersion = request.getHeader(Constants.APP_VERSION);
        String ip = getIpAddr(request);
        ParamsMap paramsMap = ParamsMap.newMap("URI", uri).addParams("REFER", fromUrl).addParams("ARGS", JSON.toJSONString(argMap)).addParams("USERNAME", "").addParams("TENANT_ID", "")
                .addParams("OPERATION_TIME", new Date()).addParams("DESCRIPTION", JSON.toJSONString(descriptions)).addParams("OPERATION_TABLE", JSON.toJSONString(tables)).addParams("REQ_IP", ip).addParams("CLIENT_TYPE", "");
        try {
            baseDao.insertByProsInTab(Table.SEPARATE + "OPERATION_LOG", paramsMap);        //TODO:换表名
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("保存操作日志失败\n" + e.getMessage());
        }
    }

    public String getMethodValue(JoinPoint joinPoint, int type) {
        MethodSignature sig = (MethodSignature) joinPoint.getSignature();
        Method method = sig.getMethod();
        return method.getAnnotation(DataSource.class).value().name();
/*		switch (type) {
		case 1:
			return method.getAnnotation(TradeLog.class).value();
		case 2:
			return method.getAnnotation(OperationLog.class).value();
		case 3:
			return method.getAnnotation(DataSource.class).value().name();
		default:
			return "";
		}*/
    }

/*	public Method getMethod(JoinPoint joinPoint) {
		Object[] objs = joinPoint.getArgs();
		Class[] clazzs = new Class[objs.length];
		for (int i = 0; i < clazzs.length; i++)
			clazzs[i] = objs[i].getClass();
		Method method = null;
		try {
			method = joinPoint.getTarget().getClass().getMethod(joinPoint.getSignature().getName(), clazzs);
		} catch (NoSuchMethodException e) {
			logger.error("获取方法注解值异常......");
			logger.error(MessageFormat.format("异常信息:{}", e.getMessage().toString()));
			e.printStackTrace();
			return method;
		}
		return method;
	}*/

    @Before("dataSource()")
    @Order(1002)
    public void sqlBefore(JoinPoint joinPoint) {
        DataSourceHolder.DBType dbType = DataSourceHolder.DBType.valueOf(getMethodValue(joinPoint, 3));
        DataSourceHolder.setLocalDataSource(dbType);
    }

    @AfterReturning("serviceSource()")
    @Order(1000)
    public void serviceReturning(JoinPoint joinPoint) {
        DataSourceHolder.setLocalDataSource(DataSourceHolder.DBType.master);
    }
/*
	@AfterReturning("serviceSource2()")
	@Order(999)
	public void serviceReturning2(JoinPoint joinPoint) {
		DataSourceHolder.setLocalDataSource(DataSourceHolder.DBType.master);
	}*/

    @Before("paramsConvert()")
    @Order(1003)
    public void parameterConvert(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Assert.isInstanceOf(Page.class,args[0],"请求参数非Page类型，请检查参数！");
        Page page = (Page) args[0];
        Map<String, Object> map = new LinkedHashMap<>(page.getParams());
        map.forEach((k,v)->{
            int indexOf = k.indexOf(Table.FIELD_INTERVAL);
            page.getParams().put(k.substring(0,indexOf).toLowerCase() + Table.SEPARATE + k.substring(indexOf+1), page.getParams().remove(k));
        });
        if(!CollectionUtils.isEmpty(page.getMatchs())) {
            map = new LinkedHashMap<>(page.getMatchs());
            map.forEach((k, v) -> {
                int indexOf = k.indexOf(Table.FIELD_INTERVAL);
                page.getMatchs().put(k.substring(0,indexOf).toLowerCase() + Table.SEPARATE + k.substring(indexOf+1), page.getMatchs().remove(k));
            });
        }
        //orderMap  也可进行编码
    }

/*	@AfterThrowing(pointcut = "dataSource()", throwing = "e")
	public void connectException(JoinPoint joinPoint, MyBatisSystemException e) throws DataAccessException {
		String dbType = getMethodValue(joinPoint, 3);
		String methodInfo = joinPoint.getSignature().toString();
		if (methodInfo.equals(method.get()))
			throw e;
		method.set(methodInfo);
		DataSourceHolder.status = DataSourceHolder.DBType.valueOf(dbType).next();
		Object target = joinPoint.getTarget();
		try {
			getMethod(joinPoint).invoke(target, joinPoint.getArgs());
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
		sendMail.sendEmail(adminEmail, "数据库服务器(" + dbType + ")异常",
				"异常发生时间:" + DateUtil.convertDateTimeToString(new Date(), null) + "\r\n异常方法:\t" + methodInfo + "\r\n"
						+ e.getMessage());// TODO:可变为短信通知..
		// 定时任务
		TriggerUtil.simpleTask(null, RecoverDBType.class, DateUtil.dateAddDay(new Date(), 1));
	}*/
}
