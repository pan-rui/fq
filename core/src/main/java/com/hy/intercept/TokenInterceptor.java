package com.hy.intercept;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hy.base.BaseImpl;
import com.hy.base.BaseResult;
import com.hy.base.ReturnCode;
import com.hy.core.CacheKey;
import com.hy.core.Constants;
import com.hy.core.ParamsMap;
import com.hy.core.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class TokenInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private BaseImpl baseImpl;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String reqAppVer = request.getHeader(Constants.APP_VER);
        String reqToken = request.getHeader(Constants.USER_TOKEN);
        String reqPhone = request.getHeader(Constants.USER_PHONE);
        String reqUserId = request.getHeader(Constants.USER_ID);
        String reqClientSn = request.getHeader(Constants.CLIENT_SN);

        if (StringUtils.isEmpty(reqToken) || StringUtils.isEmpty(reqPhone) || StringUtils.isEmpty(reqUserId) || StringUtils.isEmpty(reqClientSn)||StringUtils.isEmpty(reqAppVer)) {
            returnMsg(response, new BaseResult(ReturnCode.HEADER_PARAMS_VERIFY_ERROR));
            return false;
        }
        // String token = (String)
        // SecurityUtils.getSubject().getSession().getAttribute("token");
        String token = Constants.getCache(CacheKey.U_TOKEN_Prefix + reqPhone);
        if (!reqToken.equals(token)) {
            returnMsg(response, new BaseResult(ReturnCode.TOKEN_VERIFY_ERROR));
            return false;
        }
        String clientSn = Constants.getCache(CacheKey.U_SN_Prefix + reqPhone);
        if (StringUtils.isEmpty(clientSn)) {
            Map<String, Object> personMap = baseImpl.getBaseDao().queryByIdInTab(Table.FQ +(reqAppVer.startsWith(Constants.USER)?Table.USER:Table.EMPLOYEE), reqUserId);
            clientSn = (String) personMap.get("clientSn");
        }
        if (!reqClientSn.equals(clientSn)) {
            returnMsg(response, new BaseResult(ReturnCode.ONLY_LIMIT_CLIENT));
            return false;
        }
        return true;
    }

    private void returnMsg(HttpServletResponse response, BaseResult result) throws IOException {
        response.setContentType(Constants.APPLICATION_JSON);
        response.setStatus(200);
        response.getOutputStream().write(JSON.toJSONBytes(result, SerializerFeature.WriteEnumUsingToString));
        response.getOutputStream().close();
    }

}