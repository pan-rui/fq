package com.hy.security;

import org.springframework.stereotype.Service;

@Service
public class UrlFilterService {
    /*
    @Autowired
    private BaseDao baseDao;
    @Autowired
    private AuthDao authDao;
    @Autowired
    private BaseImpl baseImpl;
    @Autowired
    private ShiroSessionDao shiroSessionDao;

    @Autowired
    private CustomDefaultFilterChainManager filterChainManager;
    private Map<String, NamedFilterList> defaultFilterChains;
    @Value("#{config['shiroSessionPrefix']}")
    private String sessionPrefix;
    @Value("#{config['AUTHOR_CACHE_NAME']}")
    private String authorCache;

    *//**
     * 修改功能资源
     *
     * @param params
     * @param ddBB
     * @return
     *//*
    public int updateFuncPermiss(Map<String, Object> params, String ddBB) {
        Assert.notNull(params);
        int result = baseDao.updateByProsInTab(ddBB + Table.SEPARATE + Table.FUNC_PRIVILEGES, params);
        if (result > 0) {
            filterChainManager.setFilterChains(filterChainManager.getGlobalFilterChains().get(ddBB));
            initFilterChains(ddBB, baseDao.queryByProsInTab(ddBB + Table.SEPARATE + Table.FUNC_PRIVILEGES, ParamsMap.newMap("IS_SEALED", "0").addParams("IS_VALID", "0").addParams(Table.TENANT_ID,params.get(Table.TENANT_ID))));
        }
        return result;
    }

    *//**
     * 删除功能资源
     *
     * @param params
     * @param ddBB
     * @return
     *//*
    public int deleteFuncPermiss(Map<String, Object> params, String ddBB) {
        Assert.notNull(params);
        int result = baseDao.deleteByProsInTab(ddBB + Table.SEPARATE + Table.FUNC_PRIVILEGES, params);
        if (result > 0) {
            filterChainManager.setFilterChains(filterChainManager.getGlobalFilterChains().get(ddBB));
            initFilterChains(ddBB, baseDao.queryByProsInTab(ddBB + Table.SEPARATE + Table.FUNC_PRIVILEGES, ParamsMap.newMap("IS_SEALED", "0").addParams("IS_VALID", "0").addParams(Table.TENANT_ID,params.get(Table.TENANT_ID))));
        }
        return result;
    }

    *//**
     * 添加功能资源
     *
     * @param params
     * @param ddBB
     * @return
     *//*
    public int addFuncPermiss(Map<String, Object> params, String ddBB) {
        Assert.notNull(params);
        params.put("ID", UUID.randomUUID().toString().replace("-", ""));
        int result = baseDao.insertByProsInTab(ddBB + Table.SEPARATE + Table.FUNC_PRIVILEGES, params);
        if (result > 0) {
            filterChainManager.setFilterChains(filterChainManager.getGlobalFilterChains().get(ddBB));
            initFilterChains(ddBB, baseDao.queryByProsInTab(ddBB + Table.SEPARATE + Table.FUNC_PRIVILEGES, ParamsMap.newMap("IS_SEALED", "0").addParams("IS_VALID", "0").addParams(Table.TENANT_ID,params.get(Table.TENANT_ID))));
        }
        return result;
    }

    *//**
     * 增加用户权限
     *
     * @param params
     * @param ddBB
     * @return
     *//*
    public int insertFuncUserPermiss(Map<String, Object> params, String ddBB) {
        Assert.notNull(params);
        params.put("ID", UUID.randomUUID().toString().replace("-", ""));
        int result = baseDao.insertByProsInTab(ddBB + Table.SEPARATE + Table.USER_FUNC_PRIVILEGES_RELATE, params);
        if (result > 0) {
//            filterChainManager.setFilterChains(filterChainManager.getGlobalFilterChains().get(SecurityUtils.getSubject().getSession().getAttribute("tenantId")));
//            insertFilterChain(ddBB, params, "USER");
//            SecurityUtils.getSecurityManager().getSession(sessionKey)
*//*            List<String> permList = (List<String>) SecurityUtils.getSubject().getSession().getAttribute("perms");
            permList.add((String) params.get(Table.FuncRolePrivilegesRelate.FUNC_PRIVILEGE_ID.name()));
            SecurityUtils.getSubject().getSession().setAttribute("perms",permList);*//*
            refreshSessionByUser(params, ddBB);
        }
        return result;
    }

    *//**
     * 删除用户权限
     *
     * @param params
     * @param ddBB
     * @return
     *//*
    public int deleteFuncUserPermiss(Map<String, Object> params, String ddBB) {
        Assert.notNull(params);
        Map<String, Object> userPerms = baseDao.queryByIdInTab(ddBB + Table.SEPARATE + Table.USER_FUNC_PRIVILEGES_RELATE, (String) params.get("ID"));
        int result = baseDao.deleteByProsInTab(ddBB + Table.SEPARATE + Table.USER_FUNC_PRIVILEGES_RELATE, params);
        if (result > 0) {
//            filterChainManager.setFilterChains(filterChainManager.getGlobalFilterChains().get(SecurityUtils.getSubject().getSession().getAttribute("tenantId")));
//            deleteFilerChains(ddBB, userPerms, "USER");
            refreshSessionByUser(params, ddBB);
        }
        return result;
    }

    *//**
     * 增加角色权限
     *
     * @param params
     * @param ddBB
     * @return
     *//*
    public int inserFuncRolePermiss(Map<String, Object> params, String ddBB) {
        Assert.notNull(params);
        params.put("ID", UUID.randomUUID().toString().replace("-", ""));
        int result = baseDao.insertByProsInTab(ddBB + Table.SEPARATE + Table.FUNC_ROLE_PRIVILEGES_RELATE, params);
        if (result > 0) {
            filterChainManager.setFilterChains(filterChainManager.getGlobalFilterChains().get(ddBB));
            insertFilterChain(ddBB, params, "ROLE");
//            SecurityUtils.getSecurityManager().getSession(sessionKey)
*//*            List<String> permList = (List<String>) SecurityUtils.getSubject().getSession().getAttribute("perms");
            permList.add((String) params.get(Table.FuncRolePrivilegesRelate.FUNC_PRIVILEGE_ID.name()));
            SecurityUtils.getSubject().getSession().setAttribute("perms",permList);*//*
            refreshSessionByRole(ddBB);
        }
        return result;
    }

    *//**
     * 删除角色权限
     *
     * @param params
     * @param ddBB
     * @return
     *//*
    public int deleteFuncRolePermiss(Map<String, Object> params, String ddBB) {
        Assert.notNull(params);
        Map<String, Object> rolePerms = baseDao.queryByIdInTab(ddBB + Table.SEPARATE + Table.FUNC_ROLE_PRIVILEGES_RELATE, (String) params.get("ID"));
        int result = baseDao.deleteByProsInTab(ddBB + Table.SEPARATE + Table.FUNC_ROLE_PRIVILEGES_RELATE, params);
        if (result > 0) {
            filterChainManager.setFilterChains(filterChainManager.getGlobalFilterChains().get(ddBB));
            deleteFilerChains(ddBB, rolePerms, "ROLE");
            refreshSessionByRole(ddBB);
        }
        return result;
    }

    *//**
     * 添加用户角色
     *
     * @param params
     * @param ddBB
     *//*
    public int addUserFuncRole(Map<String, Object> params, String ddBB) {
        Assert.notNull(params);
        params.put("ID", UUID.randomUUID().toString().replace("-", ""));
        int result = baseDao.insertByProsInTab(ddBB + Table.SEPARATE + Table.USER_FUNC_ROLE_RELATE, params);
        if (result > 0) {
            refreshSessionByUser(params, ddBB);
        }
        return result;
    }

    *//**
     * 删除用户角色
     *
     * @param params
     * @param ddBB
     * @return
     *//*
    public int deleteUserFuncRole(Map<String, Object> params, String ddBB) {
        Assert.notNull(params);
        Map<String, Object> userRole = baseDao.queryByIdInTab(ddBB + Table.SEPARATE + Table.USER_FUNC_ROLE_RELATE, (String) params.get("ID"));
        int result = baseDao.deleteByProsInTab(ddBB + Table.SEPARATE + Table.USER_FUNC_ROLE_RELATE, params);
        if (result > 0)
            refreshSessionByUser(userRole, ddBB);
        return result;
    }

    public void initFilterChains(String ddBB, List<Map<String, Object>> funcPs) {
        //1、首先删除以前老的filter chain并注册默认的
        filterChainManager.getFilterChains().clear();
        if (defaultFilterChains != null) {
            filterChainManager.getFilterChains().putAll(defaultFilterChains);
        }
        funcPs.forEach(funcP -> {
            String url = (String) funcP.get("privilegesUrl");
            //注册角色
            List<Map<String, Object>> roles = authDao.queryRoleByFunPermissTab(ddBB + Table.SEPARATE + Table.FUNC_ROLE, ddBB + Table.SEPARATE + Table.FUNC_ROLE_PRIVILEGES_RELATE, (String) funcP.get("id"));
            StringBuffer sb = new StringBuffer();
            roles.forEach(role -> {
                sb.append(Table.SEPARATE_SPLIT).append(role.get("roleCode"));
            });
            if (!StringUtils.isEmpty(sb.toString()))
                filterChainManager.addToChain(url, "roles", sb.toString().substring(1));
*//*            List<Map<String, Object>> permiss = baseDao.queryByProsInTab(ddBB + Table.SEPARATE + Table.USER_FUNC_PRIVILEGES_RELATE, ParamsMap.newMap("FUNC_PRIVILEGE_ID", funcP.get("id")));
            StringBuffer sbb = new StringBuffer();
            permiss.forEach(perm -> {
                sb.append(Table.SEPARATE_SPLIT).append(perm.get("id"));
            });*//*
//            if (!StringUtils.isEmpty(sbb.toString()))
//                filterChainManager.addToChain(url, "perms", sbb.toString().substring(1));
            filterChainManager.addToChain(url, "perms", (String) funcP.get("privilegesCode"));
        });
    }

    @PostConstruct
    public void initFilterChain() {
        defaultFilterChains = new HashMap<String, NamedFilterList>(filterChainManager.getFilterChains());
        List<Map<String, Object>> tenants = Constants.getCacheValue("system", "dems-" + Table.TENANT, List.class);
        if (tenants != null)
            tenants.forEach(tenant -> {
                String ddBB = (String) tenant.get("dbName");
                filterChainManager.setFilterChains(filterChainManager.getGlobalFilterChains().get(ddBB));
                //载入URL资源
                initFilterChains(String.valueOf(tenant.get("dbName")), baseDao.queryByProsInTab(String.valueOf(tenant.get("dbName")) + Table.SEPARATE + Table.FUNC_PRIVILEGES, ParamsMap.newMap("IS_SEALED", "0").addParams("IS_VALID", "0").addParams(Table.FuncPrivileges.TENANT_ID.name(), tenant.get("id"))));
            });
    }

    public void insertFilterChain(String ddBB, Map<String, Object> funcMap, String tabN) {
        if (tabN.equalsIgnoreCase("ROLE")) {
            String sId = (String) funcMap.get(Table.FuncRolePrivilegesRelate.FUNC_PRIVILEGE_ID.name());
            Map<String, Object> funcPerm = baseDao.queryByIdInTab(ddBB + Table.SEPARATE + Table.FUNC_PRIVILEGES, sId);
            //注册角色
            List<Map<String, Object>> roles = authDao.queryRoleByFunPermissTab(ddBB + Table.SEPARATE + Table.FUNC_ROLE, ddBB + Table.SEPARATE + Table.FUNC_ROLE_PRIVILEGES_RELATE, sId);
            StringBuffer sb = new StringBuffer();
            roles.forEach(role -> {
                sb.append(Table.SEPARATE_SPLIT).append(role.get("roleCode"));
            });
            filterChainManager.addToChain((String) funcPerm.get("privilegesUrl"), "roles", sb.toString().substring(1));
        }
*//*        else if (tabN.equalsIgnoreCase("USER")) {
            String sId = (String) funcMap.get(Table.UserFuncPrivilegesRelate.FUNC_PRIVILEGE_ID.name());
            Map<String, Object> funcPerm = baseDao.queryByIdInTab(ddBB + Table.SEPARATE + Table.FUNC_PRIVILEGES, sId);
            //注册用户
            List<Map<String, Object>> permiss = baseDao.queryByProsInTab(ddBB + Table.SEPARATE + Table.USER_FUNC_PRIVILEGES_RELATE, ParamsMap.newMap("FUNC_PRIVILEGE_ID", sId));
            StringBuffer sbb = new StringBuffer();
            permiss.forEach(perm -> {
                sbb.append(Table.SEPARATE_SPLIT).append(perm.get("id"));
            });
            filterChainManager.addToChain((String) funcPerm.get("privilegesUrl"), "perms", sbb.toString().substring(1));
        }*//*
    }

    public void deleteFilerChains(String ddBB, Map<String, Object> funcMap, String tabN) {
        if (tabN.equalsIgnoreCase("ROLE")) {
            String sId = (String) funcMap.get(Table.FuncRolePrivilegesRelate.FUNC_PRIVILEGE_ID.name());
            Map<String, Object> funcPerm = baseDao.queryByIdInTab(ddBB + Table.SEPARATE + Table.FUNC_PRIVILEGES, sId);
            //注册角色
            List<Map<String, Object>> roles = authDao.queryRoleByFunPermissTab(ddBB + Table.SEPARATE + Table.FUNC_ROLE, ddBB + Table.SEPARATE + Table.FUNC_ROLE_PRIVILEGES_RELATE, sId);
            StringBuffer sb = new StringBuffer();
            roles.forEach(role -> {
                sb.append(Table.SEPARATE_SPLIT).append(role.get("roleCode"));
            });
            filterChainManager.getFilterChains().remove(funcPerm.get("privilegesUrl"));
            filterChainManager.addToChain((String) funcPerm.get("privilegesUrl"), "roles", sb.toString().substring(1));
        }
*//*        else if (tabN.equalsIgnoreCase("USER")) {
            String sId = (String) funcMap.get(Table.UserFuncPrivilegesRelate.FUNC_PRIVILEGE_ID.name());
            Map<String, Object> funcPerm = baseDao.queryByIdInTab(ddBB + Table.SEPARATE + Table.FUNC_PRIVILEGES, sId);
            //注册用户
            List<Map<String, Object>> permiss = baseDao.queryByProsInTab(ddBB + Table.SEPARATE + Table.USER_FUNC_PRIVILEGES_RELATE, ParamsMap.newMap("FUNC_PRIVILEGE_ID", sId));
            StringBuffer sbb = new StringBuffer();
            permiss.forEach(perm -> {
                sbb.append(Table.SEPARATE_SPLIT).append(perm.get("id"));
            });
            filterChainManager.getFilterChains().remove(funcPerm.get("privilegesUrl"));
            filterChainManager.addToChain((String) funcPerm.get("privilegesUrl"), "perms", sbb.toString().substring(1));
        }*//*
    }

    public void refreshSessionByUser(Map<String, Object> params, String ddBB) {
        UserRealm realm = (UserRealm) ((RealmSecurityManager) SecurityUtils.getSecurityManager()).getRealms().iterator().next();
//       List<Session> sessions= (List<Session>) shiroSessionDao.getActiveSessions();
*//*        PrincipalCollection principalCollection = new SimplePrincipalCollection(ParamsMap.newMap("ddBB", ddBB).addParams("username", params.get("PHONE")), realm.getName());
        realm.clearCachedAuthorizationInfo(principalCollection);
        realm.doGetAuthorizationInfo(principalCollection);*//*
        Constants.cacheManager.getCache(authorCache).evict(ddBB + Table.SEPARATE_CACHE + params.get("PHONE"));
    }

    public void refreshSessionByRole(String ddBB) {
//        UserRealm realm = (UserRealm) ((RealmSecurityManager) SecurityUtils.getSecurityManager()).getRealms().iterator().next();
        List<Session> sessions = (List<Session>) shiroSessionDao.getActiveSessions();
        sessions.forEach(session -> {
            String tenB = Constants.getCache(session.getId().toString());
            String[] tenBArr = tenB.split(Table.SEPARATE_SPLIT);
            if (ddBB.equals(tenBArr[1])) {
                Constants.cacheManager.getCache(authorCache).evict(ddBB + Table.SEPARATE_CACHE + tenBArr[2]);
            }
        });
//        PrincipalCollection principalCollection = new SimplePrincipalCollection(ParamsMap.newMap("ddBB", ddBB).addParams("username", userMap.get("phone")), realm.getName());
    }
*/
}
