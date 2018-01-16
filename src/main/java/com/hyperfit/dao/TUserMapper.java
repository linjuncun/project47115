package com.hyperfit.dao;

import com.hyperfit.entity.TUser;
import com.hyperfit.util.PageEntity;

import java.util.List;
import java.util.Map;

public interface TUserMapper {
    /**
     * 查询用户信息
     */
    TUser getUserInfo(Map<String, Object> param);

    /**
     * 更新用户信息
     */
    Integer updateUserInfo(Map<String, Object> param);

    /**
     * 新增用户
     */
    int insertUser(TUser param);

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
     * 查询课程预约用户列表
     */
    List<TUser> getCourseUser(PageEntity pageEntity);
}