package com.hy.security;

import org.apache.shiro.web.filter.PathMatchingFilter;
import com.hy.base.BaseImpl;
import org.springframework.util.Assert;
import redis.clients.jedis.JedisPool;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SysUserFilter extends PathMatchingFilter {

    private BaseImpl baseImpl;
//    private CustomDefaultFilterChainManager filterChainManager;
    private JedisPool jedisPool;
    private String excludeReg;

    public void setBaseImpl(BaseImpl baseImpl) {
        this.baseImpl = baseImpl;
    }

    public void setExcludeReg(String excludeReg) {
        this.excludeReg = excludeReg;
    }

/*    public void setFilterChainManager(CustomDefaultFilterChainManager filterChainManager) {
        this.filterChainManager = filterChainManager;
    }*/

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    protected boolean preHandle(ServletRequest request1, ServletResponse response1) throws Exception {
           HttpServletRequest request = (HttpServletRequest) request1;
        HttpServletResponse response = (HttpServletResponse) response1;
  /*      String uri=request.getRequestURI().substring( request.getContextPath().length());
        if (!filter(uri)) {
            String sessionId = (String) SecurityUtils.getSubject().getSession().getId();
            boolean isFail = false;
            String tenB = Constants.getCache(sessionId);
            String token =  request.getHeader(Constants.USER_TOKEN);
            Map<String, Object> userMap = StringUtils.isEmpty(token) ? null : Constants.getCacheValue("token", "quality-user-" + token, ParamsMap.class);
            if (userMap != null) {
                String ddBB = (String) userMap.get(Constants.DDBB);
                if (!StringUtils.isEmpty(ddBB)) {
                    request.setAttribute(Constants.DDBB, ddBB);
     //               filterChainManager.setFilterChains(filterChainManager.getGlobalFilterChains().get(ddBB));
                } else isFail = true;
            } else if (!StringUtils.isEmpty(tenB)) {
                String[] tenBArr = tenB.split(Table.SEPARATE_SPLIT);
                request.setAttribute(Constants.DDBB, tenBArr[1]);
     //           filterChainManager.setFilterChains(filterChainManager.getGlobalFilterChains().get(tenBArr[1]));
            } else isFail = true;
            //访问日志统计
            try {
                baseImpl.getBaseDao().insertByProsInTab("dems.ACCESS_STATISTICS", genLog(request).addParams("URI", uri));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isFail) return returnMsg(response);
        }*/
        return super.preHandle(request, response);
    }

    public boolean filter(String uri) {
       return uri.matches(excludeReg);
    }

/*    public ParamsMap genLog(HttpServletRequest request) {
        String userToken = request.getHeader(Constants.USER_TOKEN);
        Map<String,String> userMap= baseImpl.getCacheOfValue("token", "quality-user-" + userToken ,Map.class);
        String username = userMap!=null?userMap.get(Constants.USER_PHONE):null;
        String userId = userMap!=null?userMap.get(Constants.USER_ID):null;
        String tenantId=(String) request.getHeader(Constants.TENANT_ID);
        String fromUrl = request.getHeader("Referer");
        String appVersion = request.getHeader(Constants.APP_VERSION);
        String ip = getIpAddr(request);
        return ParamsMap.newMap("REFER", fromUrl).addParams("USER_ID", userId).addParams("USER_NAME", username).addParams("TENANT_ID", tenantId).addParams("REQ_IP", ip)
                .addParams("CLIENT_TYPE", appVersion).addParams("ACCESS_TIME", new Date());
    }*/

    protected String getIpAddr(final HttpServletRequest request) {
        Assert.notNull(request, "getIpAddr method HttpServletRequest Object is null");
        String ipString = request.getHeader("x-forwarded-for");
        if (org.springframework.util.StringUtils.isEmpty(ipString) || "unknown".equalsIgnoreCase(ipString)) {
            ipString = request.getHeader("Proxy-Client-IP");
        }
        if (org.springframework.util.StringUtils.isEmpty(ipString) || "unknown".equalsIgnoreCase(ipString)) {
            ipString = request.getHeader("WL-Proxy-Client-IP");
        }
        if (org.springframework.util.StringUtils.isEmpty(ipString) || "unknown".equalsIgnoreCase(ipString)) {
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
/*    private boolean returnMsg(ServletResponse response) throws IOException {
        response.setContentType(Constants.APPLICATION_JSON);
        response.getWriter().write(JSON.toJSONString(new BaseResult(ReturnCode.TOKEN_VERIFY_ERROR)));
        response.getWriter().close();
        return false;
    }*/
}
