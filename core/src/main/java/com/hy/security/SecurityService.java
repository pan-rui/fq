package com.hy.security;

import org.springframework.stereotype.Service;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-03-27 14:12)
 * @version: \$Rev: 3596 $
 * @UpdateAuthor: \$Author: panrui $
 * @UpdateDateTime: \$Date: 2017-07-16 00:27:43 +0800 (周日, 16 7月 2017) $
 */
@Service
public class SecurityService {
/*
    @Autowired
    private BaseImpl baseImpl;
    @Autowired
    private BaseDao baseDao;
    @Autowired
    private AuthDao authDao;

    *//**
     * 查询用户功能角色
     *
     * @param ddBB
     * @param principals 用户名
     * @return
     *//*
//    @Cacheable(value = "auth", cacheManager = "cacheManager", key = "'roles$'+#ddBB+'-'+#principals", sync = true)
    public List<Map<String, Object>> findFuncRoles(String ddBB, String principals) {
        return authDao.queryFuncRolesByUserTab(ddBB + Table.SEPARATE + Table.USER_FUNC_ROLE_RELATE, ddBB + Table.SEPARATE + Table.USER,
                ddBB + Table.SEPARATE + Table.FUNC_ROLE, principals);
    }

    *//**
     * 根据角色列表(不能为空)查询用户权限
     *
     * @param ddBB
     * @param roles
     * @return
     *//*
    public List<Map<String, Object>> findFuncPermissByRoles(String ddBB, List<String> roles) {
        if (roles == null || roles.isEmpty()) return Collections.emptyList();
        return authDao.queryFuncPermissByRolesTab(ddBB + Table.SEPARATE + Table.FUNC_ROLE_PRIVILEGES_RELATE, ddBB + Table.SEPARATE + Table.FUNC_PRIVILEGES, roles);
    }

    *//**
     * @param ddBB
     * @param roleId
     * @return
     *//*
    @Cacheable(value = "auth", cacheManager = "cacheManager", key = "'permissByRole$'+#ddBB+'-'+#roleId")
    public List<Map<String, Object>> findFuncPermissByRole(String ddBB, String roleId) {
        return authDao.queryFuncPermissByRoleTab(ddBB + Table.SEPARATE + Table.FUNC_ROLE_PRIVILEGES_RELATE, ddBB + Table.SEPARATE + Table.FUNC_PRIVILEGES, roleId);
    }

    *//**
     * @param ddBB
     * @param userId
     * @return
     *//*
    @Cacheable(value = "auth", cacheManager = "cacheManager", key = "'permiss$'+#ddBB+'-'+#userId", sync = true)
    public List<Map<String, Object>> findFuncPermissByUser(String ddBB, String userId) {
        return baseDao.queryByProsInTab(ddBB + Table.SEPARATE + Table.USER_FUNC_PRIVILEGES_RELATE, ParamsMap.newMap("USER_ID", userId));
    }

    *//**
     * 查询租户功能角色
     *
     * @param ddBB
     * @param principals 租户ID
     * @return
     *//*
//    @Cacheable(value = "auth", cacheManager = "cacheManager", key = "'roles$'+#ddBB+'-'+#principals", sync = true)
    public List<Map<String, Object>> findTenantRoles(String ddBB, String principals) {
        return authDao.queryTenantRoles(ddBB, principals);
    }

    *//**
     * 查询租户功能权限
     *
     * @param ddBB
     * @param tenantId 租户ID
     * @param roles 租户角色
     * @return
     *//*
//    @Cacheable(value = "auth", cacheManager = "cacheManager", key = "'permiss$'+#ddBB+'-'+#tenantId", sync = true)
    public List<Map<String, Object>> queryTenantPermissByRoles(String ddBB, String tenantId,List<String> roles) {
        return authDao.queryTenantPermissByRoles(ddBB, tenantId,roles);
    }*/
}
