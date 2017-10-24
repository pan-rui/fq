package com.hy.action;

import com.hy.base.BaseImpl;
import com.hy.base.BaseResult;
import com.hy.base.IBase;
import com.hy.base.ReturnCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xmlbeans.impl.regex.Match;
import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lenovo on 2014/12/6.
 */
@ControllerAdvice
public class BaseAction implements IBase, ServletContextAware {
	// @Autowired
	// protected javax.validation.Validator validator;
	protected javax.validation.Validator validator = Validation.byProvider(HibernateValidator.class).configure()
			.buildValidatorFactory().getValidator();
	protected ServletContext servletContext;
	protected String webPath;
	@Autowired
	protected BaseImpl baseImpl;
    private Logger logger = LogManager.getLogger(BaseAction.class);
    private Pattern tableMatch=Pattern.compile("`?fq`?\\.`?([\\w]+)`?");
    private Pattern foreignKey=Pattern.compile("FOREIGN KEY\\s\\(`(\\w+)`\\)");
    private Pattern duplicateKey=Pattern.compile("Duplicate entry\\s\\'(\\w+)\\'");
    private Pattern dataColumn=Pattern.compile("for column\\s\\'(\\w+)\\'");
    // TODO:待完善
	/*
	 * public String getMessage(HttpServletRequest request, String key,
	 * Object... objs) { Locale locale = RequestContextUtils.getLocale(request);
	 * return baseImpl.applicationContext.getMessage(key, objs, locale); }
	 */

	@ExceptionHandler
	@ResponseBody
	public BaseResult exp(HttpServletRequest request, HttpServletResponse response, Exception ex) {
//		ex.printStackTrace();
		if (ex instanceof HttpMessageNotReadableException) {
			logger.error(ex.getMessage());
			return new BaseResult(ReturnCode.FAIL);
		} else if (ex instanceof ServletRequestBindingException || ex instanceof IllegalArgumentException || ex instanceof ClassCastException) {
			logger.error(ex.getMessage());
			return new BaseResult(ReturnCode.REQUEST_PARAMS_VERIFY_ERROR);
		} else if (ex instanceof DuplicateKeyException) {
			String errMsg="";
			Matcher matcher = duplicateKey.matcher(ex.getMessage());
			if(matcher.find(30)) {
				errMsg = matcher.group(1);
			}
			logger.error(ex.getMessage());
			return new BaseResult(2202, "数据不可重复，请检查:"+errMsg);
		} else if (ex instanceof DataIntegrityViolationException) {
			String errMsg="";
			Matcher matcher = foreignKey.matcher(ex.getMessage());
			Matcher tMatch=tableMatch.matcher(ex.getMessage());
			String fieldName = "数据";
			if(matcher.find(30))
				errMsg = matcher.group(1);
			if (StringUtils.isEmpty(errMsg)) {
				Matcher matcher2 = dataColumn.matcher(ex.getMessage());
				if(matcher2.find(30))
					errMsg = matcher2.group(1);
			}
			if(tMatch.find(80)) {
				Map<String,String> fieldMap=BaseImpl.getFqMap().get(tMatch.group(1));
				if(!CollectionUtils.isEmpty(fieldMap))
				fieldName = fieldMap.get(errMsg);
			}
			return new BaseResult(2203, fieldName+"有误，请确认无误后重试." );
		}
			logger.error(ex.getMessage());
		return new BaseResult(ReturnCode.SYSTEM_ERROR);
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
		this.webPath = servletContext.getRealPath("/");
	}

	public <E> BaseResult validAndReturn(E t) {
		Set<ConstraintViolation<E>> errs = validator.validate(t);
		if (errs.size() > 0) {
			StringBuffer sb = new StringBuffer();
			for (ConstraintViolation<E> cons : errs)
				sb.append(cons.getPropertyPath() + ":" + cons.getInvalidValue() + "===>" + cons.getMessage() + "\r\n");
			return new BaseResult(1, sb.toString());
		} else
			return new BaseResult(ReturnCode.OK);
	}

	public String getCookie(String key, HttpServletRequest request) {
		Cookie mycookies[] = request.getCookies();
		if (mycookies != null) {
			for (int i = 0; i < mycookies.length; i++) {
				if (key.equalsIgnoreCase(mycookies[i].getName())) {
					try {
						return URLDecoder.decode(mycookies[i].getValue(), "utf-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return "";
	}


	/*
	 * @RequestMapping("/imageCode")
	 * 
	 * @ResponseBody public void test(HttpServletRequest
	 * request,HttpServletResponse response) { try {
	 * ImageCode.generatorImg(request,response); } catch (ServletException e) {
	 * e.printStackTrace(); } }
	 */

	/**
	 * 获取访问用户的客户端IP（适用于公网与局域网）.
	 */
	protected String getIpAddr(final HttpServletRequest request) {
		Assert.notNull(request, "getIpAddr method HttpServletRequest Object is null");
		String ipString = request.getHeader("x-forwarded-for");
		if (StringUtils.isEmpty(ipString) || "unknown".equalsIgnoreCase(ipString)) {
			ipString = request.getHeader("Proxy-Client-IP");
		}
		if (StringUtils.isEmpty(ipString) || "unknown".equalsIgnoreCase(ipString)) {
			ipString = request.getHeader("WL-Proxy-Client-IP");
		}
		if (StringUtils.isEmpty(ipString) || "unknown".equalsIgnoreCase(ipString)) {
			ipString = request.getRemoteAddr();
		}

		// 多个路由时，取第一个非unknown的ip
		final String[] arr = ipString.split(",");
		for (final String str : arr) {
			if (!"unknown".equalsIgnoreCase(str)) {
				ipString = str;
				break;
			}
		}
		return ipString;
	}

	@ModelAttribute("baseUrl")
	public String setConstans(HttpServletRequest request, ModelMap model) {

		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
				+ request.getContextPath();
	}

}
