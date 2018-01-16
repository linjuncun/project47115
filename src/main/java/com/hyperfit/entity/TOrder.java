package com.hyperfit.entity;

import java.util.Date;

/**
 * t_order表实体类
 */
public class TOrder {
    /**
     * 订单id
     */
    private Integer orderId;

    /**
     * 订单类型 1：购买课程  2：购买会员卡套餐  3：会员卡余额过期
     */
    private Integer orderType;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 购买课程id
     */
    private Integer courseId;

    /**
     * 购买套餐id
     */
    private Integer packageId;
    private String packageName;
    /**
     * 购买套餐会员卡类型 1：储值卡 2：次卡
     */
    private Integer buyCardType;

    /**
     * 订单金额（元）
     */
    private Integer amount;

    /**
     * 用户优惠券id
     */
    private Integer userCouponId;

    /**
     * 支付方式 1：微信支付  2：会员卡支付（包括储值和次卡）
     */
    private Integer payType;

    /**
     * 使用会员卡id
     */
    private Integer cardId;
    /**
     * 会员卡类型 1：储值卡 2：次卡
     */
    private Integer cardType;
    private String cardNo;

    /**
     * 会员套餐销售人员系统用户id
     */
    private Integer sysUserId;

    /**
     * 订单状态 1：待付款 2：购买中 3：已完成 4：申请退款 5：已退款 6：退款异常
     */
    private Integer status;

    /**
     * 交易流水号（微信内部订单号）
     */
    private String applyNo;

    /**
     * 用户openid
     */
    private String openid;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程所属门店id
     */
    private Integer gymId;

    /**
     * 请求ip(用于微信下单)
     */
    private String ip;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 课程上课时间
     */
    private Date classHour;
    /**
     * 套餐到帐金额
     */
    private Integer arrive;

    /**
     * 备注
     */
    private String remark;

    /**
     * 优惠券名称
     */
    private String couponName;
    /**
     * 优惠券金额
     */
    private Integer couponValue;
    /**
     * 用户姓名
     */
    private String name;

    /**
     * 订单id
     */
    public Integer getOrderId() {
        return orderId;
    }

    /**
     * 订单id
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    /**
     * 订单类型 1：购买课程  2：购买会员卡套餐  3：会员卡余额过期
     */
    public Integer getOrderType() {
        return orderType;
    }

    /**
     * 订单类型 1：购买课程  2：购买会员卡套餐  3：会员卡余额过期
     */
    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    /**
     * 用户id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 用户id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 购买课程id
     */
    public Integer getCourseId() {
        return courseId;
    }

    /**
     * 购买课程id
     */
    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    /**
     * 购买套餐id
     */
    public Integer getPackageId() {
        return packageId;
    }

    /**
     * 购买套餐id
     */
    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }

    /**
     * 订单金额（元）
     */
    public Integer getAmount() {
        return amount;
    }

    /**
     * 订单金额（元）
     */
    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    /**
     * 支付方式 1：微信支付  2：会员卡支付（包括储值和次卡）
     */
    public Integer getPayType() {
        return payType;
    }

    /**
     * 支付方式 1：微信支付  2：会员卡支付（包括储值和次卡）
     */
    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    /**
     * 使用会员卡id
     */
    public Integer getCardId() {
        return cardId;
    }

    /**
     * 使用会员卡id
     */
    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

    /**
     * 订单状态 1：待付款 2：购买中 3：已完成 4：申请退款 5：已退款 6：退款异常
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 订单状态 1：待付款 2：购买中 3：已完成 4：申请退款 5：已退款 6：退款异常
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 交易流水号（微信内部订单号）
     */
    public String getApplyNo() {
        return applyNo;
    }

    /**
     * 交易流水号（微信内部订单号）
     */
    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
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

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getUserCouponId() {
        return userCouponId;
    }

    public void setUserCouponId(Integer userCouponId) {
        this.userCouponId = userCouponId;
    }

    public Integer getSysUserId() {
        return sysUserId;
    }

    public void setSysUserId(Integer sysUserId) {
        this.sysUserId = sysUserId;
    }

    public Integer getCardType() {
        return cardType;
    }

    public void setCardType(Integer cardType) {
        this.cardType = cardType;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public Integer getGymId() {
        return gymId;
    }

    public void setGymId(Integer gymId) {
        this.gymId = gymId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public Integer getCouponValue() {
        return couponValue;
    }

    public void setCouponValue(Integer couponValue) {
        this.couponValue = couponValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Date getClassHour() {
        return classHour;
    }

    public void setClassHour(Date classHour) {
        this.classHour = classHour;
    }

    public Integer getArrive() {
        return arrive;
    }

    public void setArrive(Integer arrive) {
        this.arrive = arrive;
    }

    public Integer getBuyCardType() {
        return buyCardType;
    }

    public void setBuyCardType(Integer buyCardType) {
        this.buyCardType = buyCardType;
    }
}