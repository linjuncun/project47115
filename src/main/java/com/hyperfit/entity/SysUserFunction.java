package com.hyperfit.entity;

/**
 * sys_user_function表实体类
 */
public class SysUserFunction {
    /**
     * 功能id
     */
    private Integer functionId;

    /**
     * 系统用户id
     */
    private Integer sysUserId;

    /**
     * 功能id
     */
    public Integer getFunctionId() {
        return functionId;
    }

    /**
     * 功能id
     */
    public void setFunctionId(Integer functionId) {
        this.functionId = functionId;
    }

    public Integer getSysUserId() {
        return sysUserId;
    }

    public void setSysUserId(Integer sysUserId) {
        this.sysUserId = sysUserId;
    }
}