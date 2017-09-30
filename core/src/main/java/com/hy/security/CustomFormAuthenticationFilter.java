package com.hy.security;

import org.apache.shiro.web.filter.PathMatchingFilter;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-03-31 16:59)
 * @version: \$Rev: 1158 $
 * @UpdateAuthor: \$Author: panrui $
 * @UpdateDateTime: \$Date: 2017-04-18 15:53:47 +0800 (周二, 18 4月 2017) $
 */
public class CustomFormAuthenticationFilter extends PathMatchingFilter {
    /*
    private BaseImpl baseImpl;
    private UserService userService;
    private String loginUrl = "/login";
    private String successUrl = "/";
    private String usernameParam;
    private String passwordParam;
    private boolean rememberMeParam;

    public void setUsernameParam(String usernameParam) {
        this.usernameParam = usernameParam;
    }

    public void setPasswordParam(String passwordParam) {
        this.passwordParam = passwordParam;
    }

    public void setRememberMeParam(boolean rememberMeParam) {
        this.rememberMeParam = rememberMeParam;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    @Override
    protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
*//*        if (SecurityUtils.getSubject().isAuthenticated()) {
            return true;//已经登录过
        }*//*
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        if (isLoginRequest(req, resp)) {
*//*            if (req.getAttribute("ddBB")==null) {
                return false;
            }*//*
            boolean loginSuccess = login(req); //登录
            if (loginSuccess) {
                return redirectToSuccessUrl(req, resp);
            }
            return true;//继续过滤器链
        } else {//保存当前地址并重定向到登录界面
            saveRequestAndRedirectToLogin(req, resp);
            return false;
        }
    }

    private boolean redirectToSuccessUrl(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpServletResponse httpServletResponse = resp;
        String phone = req.getParameter("phone");
        String tenantId = req.getParameter("tenantId");
        Map<String, Object> map = userService.getUserByPhone(phone, tenantId, (String) req.getAttribute("ddBB"));
        if (map != null) {
//            String tokenStr = tokenService.getToken(phone, tenantId);
            String tokenStr = UUID.randomUUID().toString().replace("-", "");
            SecurityUtils.getSubject().getSession().setAttribute("token", tokenStr);
            map.put("token", tokenStr);
        }
        httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletResponse.getWriter().write(JSON.toJSONString(map));
        httpServletResponse.getWriter().close();
        return false;
    }

    private void saveRequestAndRedirectToLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        WebUtils.saveRequest(req);
        WebUtils.issueRedirect(req, resp, loginUrl);
    }

    private boolean login(HttpServletRequest req) {
        String username = req.getParameter(usernameParam);
        String password = req.getParameter(passwordParam);
        try {
            SecurityUtils.getSubject().login(new UsernamePasswordToken(username, password, rememberMeParam));
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("shiroLoginFailure", e.getClass());
            return false;
        }
        return true;
    }


    private boolean isLoginRequest(HttpServletRequest req, HttpServletResponse response) throws IOException {
        boolean isLoginRequest = pathsMatch(loginUrl, WebUtils.getPathWithinApplication(req));
        if (isLoginRequest) {
            List<Map<String, Object>> tentants = baseImpl.getSystemValue("dems-" + Table.TENANT, List.class);
            Optional<Map<String, Object>> tentant = tentants.stream().filter(map -> {
                return map.get("id").equals(req.getParameter("tenantId"));
            }).findFirst();
            if (tentant.isPresent()) {
                req.setAttribute("ddBB", tentant.get().get("dbName"));
                SecurityUtils.getSubject().getSession(true).setAttribute("ddBB", tentant.get().get("dbName"));
                SecurityUtils.getSubject().getSession(true).setAttribute("tenantId", tentant.get().get("id"));
            } else {
                response.setContentType("text/plain;charset=utf-8");
                response.getWriter().write("鹏城欢迎您!");
                response.getWriter().close();
            }
        }
        return isLoginRequest;
    }

    public void setBaseImpl(BaseImpl baseImpl) {
        this.baseImpl = baseImpl;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
*/
    /*    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        String username = getUsername(request);
        String password = getPassword(request);
        boolean rememberMe = isRememberMe(request);
        String host = getHost(request);
        return new UsernamePasswordToken(username, password, rememberMe, host);
    }*/

/*    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String token = httpServletRequest.getHeader(Constants.USER_TOKEN);
//            if()              //TODO:     token登录
        return super.executeLogin(request, response);
    }*/

/*    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject,
                                     ServletRequest request, ServletResponse response) throws Exception {
//        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String phone = request.getParameter("phone");
        String tenantId = request.getParameter("tenantId");
        Map<String, Object> map = userService.getUserByPhone(phone, tenantId, (String) request.getAttribute("ddBB"));
//        Map<String, Object> map = (Map<String, Object>) baseDao.queryByProsInTab((String)(request.getAttribute("ddBB"))+Table.SEPARATE+Table.USER,ParamsMap.newMap("PHONE",phone).addParams("TENANT_ID",tenantId)).get(0);
        if (map != null) {
            String tokenStr = tokenService.getToken(phone, tenantId);
            map.put("token", tokenStr);
        }
        httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletResponse.getWriter().write(JSON.toJSONString(map));
        httpServletResponse.getWriter().close();
        return true;
    }*/
}
