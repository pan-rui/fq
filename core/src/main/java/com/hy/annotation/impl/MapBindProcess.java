package com.hy.annotation.impl;

import com.hy.annotation.MapBind;
import com.hy.core.Page;
import com.hy.core.ParamsMap;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2015/10/16.
 */
public class MapBindProcess implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterAnnotation(MapBind.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        MapBind beanBind = methodParameter.getParameterAnnotation(MapBind.class);
        String prefix = beanBind.value();Class clazz=beanBind.clazz();
        Page<Map<String, Object>> page = new Page();
        Map<String, Object> params = new ParamsMap<>();
        Map<String, String> orderMap = new HashMap<>();
        Iterator<String> itNames = nRequest.getParameterNames();
        while (itNames.hasNext()) {
            String name = itNames.next();
            if (name.startsWith(prefix))
                params.put(name.substring(name.lastIndexOf(".") + 1), nRequest.getParameter(name));
        }
        if(clazz.isInstance(params)) return params;
//        if (prefix.startsWith("page")) {
        String pageNo = nRequest.getParameter("page");
        if (!StringUtils.isEmpty(pageNo))
            page.setPageNo(Integer.parseInt(pageNo));
        String pageSize = nRequest.getParameter("rows");
        if (!StringUtils.isEmpty(pageSize))
            page.setPageSize(Integer.parseInt(pageSize));
//        }
        String sort = nRequest.getParameter("sort");
        String order = nRequest.getParameter("order");
        if (!StringUtils.isEmpty(sort)) {
            String[] sorts = sort.split(",");
            String[] orders = order.split(",");
            for (int i = 0; i < sorts.length; i++)
                orderMap.put(sorts[i], orders[i]);
        }
        page.setOrderMap(orderMap);
        page.setParams(params);
        return page;
    }
}
