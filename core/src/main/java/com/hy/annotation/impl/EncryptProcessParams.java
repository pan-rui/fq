package com.hy.annotation.impl;

import com.alibaba.fastjson.JSON;
import com.hy.annotation.EncryptProcess;
import com.hy.base.BaseResult;
import com.hy.annotation.DecryptResponse;
import com.hy.core.ColumnProcess;
import com.hy.core.Page;
import com.hy.vo.ParamsVo;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: ${Description}
 * Author: 潘锐 (2017-03-27 14:21)
 * version: \$Rev: 1166 $
 * UpdateAuthor: \$Author: panrui $
 * UpdateDateTime: \$Date: 2017-04-18 16:48:03 +0800 (周二, 18 4月 2017) $
 */
public class EncryptProcessParams extends RequestResponseBodyMethodProcessor {
    private static final Logger logger = LogManager.getLogger(EncryptProcessParams.class);
    private static Pattern pattern = Pattern.compile("\\\"(\\w+)\\\":");

    public EncryptProcessParams(List<HttpMessageConverter<?>> converters) {
        super(converters);
    }

    public static byte[] getRequestPostBytes(HttpServletRequest request)
            throws IOException {
        int contentLength = request.getContentLength();
        byte buffer[] = new byte[contentLength];
        for (int i = 0; i < contentLength; ) {
            int readlen = request.getInputStream().read(buffer, i, contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        return buffer;
    }

    public static String jsonStrProcess(final String original, List<String> ignoreProperty) {
        Matcher matcher = pattern.matcher(original);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String replaceStr = matcher.group(1);
            if (!ignoreProperty.contains(replaceStr))
                replaceStr = ColumnProcess.decryptVal(matcher.group(1));
            matcher.appendReplacement(sb, "\"" + replaceStr + "\":");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterAnnotation(EncryptProcess.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
//        EncryptProcess encryptProcess = parameter.getParameterAnnotation(EncryptProcess.class);
//        boolean required = encryptProcess.required();
//        if(!required) return null;
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        if (request.getContentLength() < 1) return null;
        String body = new String(getRequestPostBytes(request), StringUtils.isEmpty(request.getCharacterEncoding()) ? "utf-8" : request.getCharacterEncoding());
        Object result = null;
        if (body.contains("pageNo")) {
            String jsonStr = jsonStrProcess(body, Arrays.asList("pageNo", "pageSize","params", "orderMap","matchs"));
            result = JSON.parseObject(jsonStr, Page.class);
        } else {
            String jsonStr = jsonStrProcess(body, Arrays.asList("datas", "params","reqData"));
            result = JSON.parseObject(jsonStr, ParamsVo.class);
        }
        return result;
    }

/*    public Map<String, Object> reflectAll(Object pObj) {
        Map<String, Object> params = (Map<String, Object>) pObj;
        Map<String, Object> newMap = new ParamsMap();
        if (params != null && !params.isEmpty()) {
            Object ID = params.remove("id");
            params.forEach((k, v) -> {
                Object newVal = null;

                if (v != null && v instanceof Map) {
                    newVal = reflectAll(v);
                } else {
                    newVal = v;
                }

                newMap.put(ColumnProcess.decryptVal(k), newVal);
            });
            if (Objects.nonNull(ID))
                newMap.put("ID", ID);
        }

        return newMap;
    }*/

    @Override
    public boolean supportsReturnType(MethodParameter methodParameter) {
        return (methodParameter.hasMethodAnnotation(DecryptResponse.class) || AnnotatedElementUtils.hasAnnotation(methodParameter.getContainingClass(), DecryptResponse.class));
    }

    @Override
    public void handleReturnValue(Object o, MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest) {
        DecryptResponse anno = methodParameter.getMethodAnnotation(DecryptResponse.class);
        modelAndViewContainer.setRequestHandled(true);
        if (!(o instanceof BaseResult)) throw new RuntimeException("返回对象不正确");
        BaseResult baseResult = (BaseResult) o;
        if (!(baseResult.getData() instanceof Map)) throw new RuntimeException("返回对象数据不正确");
        Map<String, Object> newMap = new HashMap();
        ((Map<String, Object>) baseResult.getData()).forEach((k, v) -> {
            newMap.put(ColumnProcess.encryptVal(k), v);
        });
        baseResult.setData(newMap);
        String type = anno.value();
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
        response.setContentType(type);
        try (PrintWriter out = response.getWriter()) {
            out.write(JSON.toJSONString(o));
            out.flush();
        } catch (IOException e) {
            logger.error("返回结果加密封闭异常");
            e.printStackTrace();
        }
    }
}
