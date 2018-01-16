package com.hyperfit.entity;

import java.util.Date;

/**
 * sys_user表实体类
 */
public class SysUser {
    /**
     * 系统用户id
     */
    private Integer sysUserId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String truename;

    /**
     * 密码
     */
    private String password;

    /**
     * 状态  1:启用  2 :停用  3:删除
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后登录时间
     */
    private Date lastLoginTime;

    /** 系统用户id */
    public Integer getSysUserId() {
        return sysUserId;
    }

    /** 系统用户id */
    public void setSysUserId(Integer sysUserId) {
        this.sysUserId = sysUserId;
    }

    /** 用户名 */
    public String getUsername() {
        return username;
    }

    /** 用户名 */
    public void setUsername(String username) {
        this.username = username;
    }

    /** 真实姓名 */
    public String getTruename() {
        return truename;
    }

    /** 真实姓名 */
    public void setTruename(String truename) {
        this.truename = truename;
    }

    /** 密码 */
    public String getPassword() {
        return password;
    }

    /** 密码 */
    public void setPassword(String password) {
        this.password = password;
    }

    /** 状态 1:启用 2 :停用 */
    public Integer getStatus() {
        return status;
    }

    /** 状态 1:启用 2 :停用 */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /** 创建时间 */
    public Date getCreateTime() {
        return createTime;
    }

    /** 创建时间 */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /** 最后登录时间 */
    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    /** 最后登录时间 */
    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
}