package com.hyperfit.entity;

import java.util.Date;

/**
 * t_coupon表实体类
 */
public class TCoupon {
    /**
     * 优惠券id
     */
    private Integer couponId;

    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 发放方式  1：人工发放  2：系统发放
     */
    private Integer sendType;

    /**
     * 面值（元）
     */
    private Integer couponValue;

    /**
     * 有效期（天）
     */
    private Integer indate;

    /**
     * 状态 1：正常 2：删除
     */
    private Integer couponStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 用户优惠券有效日期
     */
    private Date deadline;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 用户优惠券状态  1：未使用 2：已使用
     */
    private Integer status;

    /**
     * 用户优惠券id
     */
    private Integer userCouponId;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 优惠券id
     */
    public Integer getCouponId() {
        return couponId;
    }

    /**
     * 优惠券id
     */
    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    /**
     * 优惠券名称
     */
    public String getCouponName() {
        return couponName;
    }

    /**
     * 优惠券名称
     */
    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    /**
     * 发放方式  1：人工发放  2：系统发放
     */
    public Integer getSendType() {
        return sendType;
    }

    /**
     * 发放方式  1：人工发放  2：系统发放
     */
    public void setSendType(Integer sendType) {
        this.sendType = sendType;
    }

    /**
     * 面值（元）
     */
    public Integer getCouponValue() {
        return couponValue;
    }

    /**
     * 面值（元）
     */
    public void setCouponValue(Integer couponValue) {
        this.couponValue = couponValue;
    }

    /**
     * 有效期（天）
     */
    public Integer getIndate() {
        return indate;
    }

    /**
     * 有效期（天）
     */
    public void setIndate(Integer indate) {
        this.indate = indate;
    }

    /**
     * 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getUserCouponId() {
        return userCouponId;
    }

    public void setUserCouponId(Integer userCouponId) {
        this.userCouponId = userCouponId;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCouponStatus() {
        return couponStatus;
    }

    public void setCouponStatus(Integer couponStatus) {
        this.couponStatus = couponStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}