package com.hyperfit.service;

import com.hyperfit.entity.*;
import com.hyperfit.util.PageEntity;

import java.util.List;
import java.util.Map;

/**
 * <p> 用户service </p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/11/17 17:34
 */
public interface UserService {

    /**
     * 查询用户信息
     */
    TUser getUserInfo(Map<String, Object> param);

    /**
     * 新增用户
     */
    int insertUser(Map<String, Object> param);

    /**
     * 修改用户信息
     */
    int updateUserInfo(Map<String, Object> param);

    /**
     * 删除用户
     */
    int delUser(Map<String, Object> param);

    /**
     * 查询用户分页列表
     */
    List<TUser> getUserList(PageEntity pageEntity);

    /**
     * 查询用户列表
     */
    List<TUser> getUsers(Map<String, Object> param);

    /**
     * 查询会员卡列表
     */
    List<TClubCard> getClubCardList(Map<String, Object> param) throws Exception;

    /**
     * 查询会员卡
     */
    List<TClubCard> getClubCards(Map<String, Object> param);

    /**
     * 查询会员卡详情
     */
    TClubCard getClubCardInfo(Map<String, Object> param);

    /**
     * 查询用户余额明细列表
     */
    List<TBalanceItem> getBalanceItem(PageEntity pageEntity);

    /**
     * 查询系统用户
     */
    List<SysUser> getSysUsers(Map<String, Object> param);

    /**
     * 查询系统用户分页列表
     */
    List<SysUser> getSysUserList(PageEntity pageEntity);

    /**
     * 查询系统用户详情
     */
    SysUser getSysUserInfo(Map<String, Object> param);

    /**
     * 新增系统用户
     */
    int insertSysUser(SysUser param);

    /**
     * 更新系统用户信息
     */
    int updateSysUserInfo(Map<String, Object> param);

    /**
     * 查询系统用户功能权限列表
     */
    List<SysFunction> getUserFunctions(Map<String, Object> param);

    /**
     * 更新系统用户权限
     */
    int addSysUserFunctions(Map<String, Object> param);

    /**
     * 更新会员卡信息
     */
    Integer updateClubCardInfo(Map<String, Object> param);

}
