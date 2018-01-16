package com.hyperfit.dao;

import com.hyperfit.entity.SysUser;
import com.hyperfit.util.PageEntity;

import java.util.List;
import java.util.Map;

/**
 * <p> 系统用户dao </p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/11/24 15:35
 */
public interface SysUserMapper {
    /**
     * 查询系统用户分页列表
     */
    List<SysUser> getSysUserList(PageEntity pageEntity);

    /**
     * 查询系统用户
     */
    List<SysUser> getSysUsers(Map<String, Object> param);

    /**
     * 查询系统用户信息
     */
    SysUser getSysUserInfo(Map<String, Object> param);

    /**
     * 新增系统用户
     */
    int insertSysUser(SysUser record);

    /**
     * 更新系统用户信息
     */
    int updateSysUserInfo(Map<String, Object> param);
}