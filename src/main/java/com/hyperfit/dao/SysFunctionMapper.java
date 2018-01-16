package com.hyperfit.dao;


import com.hyperfit.entity.SysFunction;
import com.hyperfit.entity.SysUserFunction;

import java.util.List;
import java.util.Map;

/**
 * <p> 功能权限dao </p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/12/1 22:14
 */
public interface SysFunctionMapper {

    /**
     * 查询系统用户功能权限列表
     */
    List<SysFunction> getUserFunctions(Map<String, Object> param);

    /**
     * 查询系统功能列表
     */
    List<SysFunction> getSysFunctions(Map<String, Object> param);

    /**
     * 删除用户关联的权限
     */
    int delSysUserFunctions(Map<String, Object> param);

    /**
     * 关联用户权限
     */
    int addSysUserFunctions(List<SysUserFunction> list);

}