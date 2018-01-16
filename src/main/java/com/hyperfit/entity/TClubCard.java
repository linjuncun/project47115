package com.hyperfit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.List;

/**
 * t_club_card表实体类
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TClubCard {
    /**
     * 会员卡id
     */
    private Integer cardId;

    /**
     * 用户id
     */
    private Integer userId;
    private String openid;
    private String userName;
    private String userPhone;

    /**
     * 副卡用户id
     */
    private Integer viceUserId;
    private String viceUserName;
    private String viceUserPhone;

    /**
     * 套餐id
     */
    private Integer packageId;

    /**
     * 余额（元/次）
     */
    private Integer balance;
    /**
     * 当前套餐到账金额
     */
    private Integer packageMoney;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 有效日期
     */
    private Date deadline;
    /**
     * 老套餐的有效日期（仅当用户用此卡充值了新套餐时此字段才有值,用于判断老套餐余额什么时候失效）
     */
    private Date oldDeadline;

    /**
     * 会员卡类型 1：储值卡 2：次卡
     */
    private Integer cardType;

    /**
     * 是否为返现活动 1：是 2：否
     */
    private Integer isReturnCash;

    /**
     * 会员卡支持门店id，多个以英文逗号","分隔
     */
    private String gymIds;
    private Integer isSupportGym;//是否支持门店 0:不支持  1：支持，用于预约课程支付时判断
    /**
     * 会员卡支持课程id，多个以英文逗号","分隔
     */
    private String courseIds;
    private Integer isSupportCourse;//是否支持课程  0：不支持  1：支持，用于预约课程支付时判断

    /**
     * 会员卡号
     */
    private String cardNo;

    /**
     * 已用多少钱（元，用于计算是否达到返现活动条件）
     */
    private Integer moneyUsed;
    /**
     * 已用多少次（用于计算是否达到返现活动条件）
     */
    private Integer timesUsed;
    /**
     * 是否已返现 0：否 1：是 2: 已失效
     */
    private Integer returnCash;

    /**
     * 离返现截止日期还差多少天
     */
    private Integer requiredDays;

    /**
     * 状态 1：正常 2：停用
     */
    private Integer status;

    /**
     * 销售人员系统用户id
     */
    private Integer sysUserId;
    private String sysUserName;

    /**
     * 用户性别
     */
    private Integer sex;

    /**
     * 套餐返现条件list
     */
    private List<TReturnCashCondition> returnCashCondition;

    /**
     * 会员卡id
     */
    public Integer getCardId() {
        return cardId;
    }

    /**
     * 会员卡id
     */
    public void setCardId(Integer cardId) {
        this.cardId = cardId;
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
     * 副卡用户id
     */
    public Integer getViceUserId() {
        return viceUserId;
    }

    /**
     * 副卡用户id
     */
    public void setViceUserId(Integer viceUserId) {
        this.viceUserId = viceUserId;
    }

    /**
     * 套餐id
     */
    public Integer getPackageId() {
        return packageId;
    }

    /**
     * 套餐id
     */
    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }

    /**
     * 余额（元/次）
     */
    public Integer getBalance() {
        return balance;
    }

    /**
     * 余额（元/次）
     */
    public void setBalance(Integer balance) {
        this.balance = balance;
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

    public Integer getCardType() {
        return cardType;
    }

    public void setCardType(Integer cardType) {
        this.cardType = cardType;
    }

    public String getGymIds() {
        return gymIds;
    }

    public void setGymIds(String gymIds) {
        this.gymIds = gymIds;
    }

    public String getCourseIds() {
        return courseIds;
    }

    public void setCourseIds(String courseIds) {
        this.courseIds = courseIds;
    }

    public Integer getIsSupportGym() {
        return isSupportGym;
    }

    public void setIsSupportGym(Integer isSupportGym) {
        this.isSupportGym = isSupportGym;
    }

    public Integer getIsSupportCourse() {
        return isSupportCourse;
    }

    public void setIsSupportCourse(Integer isSupportCourse) {
        this.isSupportCourse = isSupportCourse;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Integer getIsReturnCash() {
        return isReturnCash;
    }

    public void setIsReturnCash(Integer isReturnCash) {
        this.isReturnCash = isReturnCash;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public Integer getMoneyUsed() {
        return moneyUsed;
    }

    public void setMoneyUsed(Integer moneyUsed) {
        this.moneyUsed = moneyUsed;
    }

    public Integer getTimesUsed() {
        return timesUsed;
    }

    public void setTimesUsed(Integer timesUsed) {
        this.timesUsed = timesUsed;
    }

    public Integer getReturnCash() {
        return returnCash;
    }

    public void setReturnCash(Integer returnCash) {
        this.returnCash = returnCash;
    }

    public List<TReturnCashCondition> getReturnCashCondition() {
        return returnCashCondition;
    }

    public void setReturnCashCondition(List<TReturnCashCondition> returnCashCondition) {
        this.returnCashCondition = returnCashCondition;
    }

    public Integer getRequiredDays() {
        return requiredDays;
    }

    public void setRequiredDays(Integer requiredDays) {
        this.requiredDays = requiredDays;
    }

    public Integer getSysUserId() {
        return sysUserId;
    }

    public void setSysUserId(Integer sysUserId) {
        this.sysUserId = sysUserId;
    }

    public Date getOldDeadline() {
        return oldDeadline;
    }

    public void setOldDeadline(Date oldDeadline) {
        this.oldDeadline = oldDeadline;
    }

    public Integer getPackageMoney() {
        return packageMoney;
    }

    public void setPackageMoney(Integer packageMoney) {
        this.packageMoney = packageMoney;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getViceUserName() {
        return viceUserName;
    }

    public void setViceUserName(String viceUserName) {
        this.viceUserName = viceUserName;
    }

    public String getSysUserName() {
        return sysUserName;
    }

    public void setSysUserName(String sysUserName) {
        this.sysUserName = sysUserName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getViceUserPhone() {
        return viceUserPhone;
    }

    public void setViceUserPhone(String viceUserPhone) {
        this.viceUserPhone = viceUserPhone;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }
}