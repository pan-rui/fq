package com.hy.security;

import com.hy.base.BaseImpl;
import com.hy.core.ParamsMap;
import com.hy.core.Table;
import com.hy.dao.BaseDao;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class UserRealm extends AuthorizingRealm {

    @Autowired
    private BaseDao baseDao;
    @Autowired
    private BaseImpl baseImpl;
    @Autowired
    private SecurityService securityService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Map<String,Object> principal = (Map<String, Object>) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
/*        List<Map<String, Object>> roles = securityService.findFuncRoles((String)principal.get("ddBB") , (String)principal.get("username"));
        authorizationInfo.setRoles(roles.stream().map(role -> {
            return (String) role.get("roleCode");
        }).collect(Collectors.toSet()));
        List<Map<String, Object>> perms = securityService.findFuncPermissByRoles((String) principal.get("ddBB"), roles.stream().map(role -> {
            return (String) role.get("id");
        }).collect(Collectors.toList()));
        authorizationInfo.setStringPermissions(perms.stream().map(perm -> {
            return (String) perm.get("privilegesCode");
        }).collect(Collectors.toSet()));*/
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = (String)token.getPrincipal();
        List<Map<String, Object>> userList = baseDao.queryByProsInTab(SecurityUtils.getSubject().getSession().getAttribute("ddBB") + Table.SEPARATE + Table.USER, ParamsMap.newMap("PHONE", username));

        if (userList == null && !userList.isEmpty()) {
            throw new UnknownAccountException();//没找到帐号
        }
        Map<String, Object> user = userList.get(0);
        if ("1".equals(user.get("isSealed"))) {
            throw new LockedAccountException(); //帐号锁定
        }
        SecurityUtils.getSubject().getSession().setAttribute("user", user);
        //交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配，如果觉得人家的不好可以自定义实现
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                username, //用户名
                user.get("pwd"), //密码
//                ByteSource.Util.bytes(user.get("PHONE")),//salt=username+salt
                getName()  //realm name
        );
        return authenticationInfo;
    }

    @Override
    public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }

    @Override
    public void clearCachedAuthenticationInfo(PrincipalCollection principals) {
        super.clearCachedAuthenticationInfo(principals);
    }

    @Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }

    public void clearAllCachedAuthorizationInfo() {
        getAuthorizationCache().clear();
    }

    public void clearAllCachedAuthenticationInfo() {
        getAuthenticationCache().clear();
    }

    public void clearAllCache() {
        clearAllCachedAuthenticationInfo();
        clearAllCachedAuthorizationInfo();
    }

    @Override
    protected Object getAuthorizationCacheKey(PrincipalCollection principals) {
        Assert.notNull(principals);
        Map<String,Object> principal = (Map<String, Object>) principals.getPrimaryPrincipal();
        return principal.get("ddBB")+ Table.SEPARATE_CACHE+principal.get("username");
    }

    @Override
    protected Object getAuthenticationCacheKey(AuthenticationToken token) {
        return super.getAuthenticationCacheKey(token);
    }
}
