package com.hyperfit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.List;

/**
 * t_package表实体类
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TPackage {
    /**
     * 套餐id
     */
    private Integer packageId;

    /**
     * 套餐所属会员卡类型  1：储值卡 2：次卡
     */
    private Integer cardType;

    /**
     * 套餐名称
     */
    private String packageName;

    /**
     * 是否为返现活动   1：是  2：否
     */
    private Integer isReturnCash;

    /**
     * 充值金额（元）
     */
    private Integer recharge;

    /**
     * 到账（元/次）
     */
    private Integer arrive;

    /**
     * 适用门店id，多个以英文逗号","分隔，0表示适用于所有门店
     */
    private String gymIds;

    /**
     * 适用课程id，多个以英文逗号","分隔，0表示适用于所有课程
     */
    private String courseIds;

    /**
     * 有效期（天）
     */
    private Integer indate;

    /**
     * 注意事项
     */
    private String notes;

    /**
     * 状态 1：上线  2：下线
     */
    private Integer status;

    /**
     * 套餐办卡张数
     */
    private Integer cardNum;

    /**
     * 数据更新时间
     */
    private Date updateTime;

    /**
     * 支持的门店list
     */
    private List<Object> supportGym;

    /**
     * 支持的课程list
     */
    private List<Object> supportCourse;

    /**
     * 套餐返现条件list
     */
    private List<TReturnCashCondition> returnCashCondition;

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
     * 套餐所属会员卡类型  1：储值卡 2：次卡
     */
    public Integer getCardType() {
        return cardType;
    }

    /**
     * 套餐所属会员卡类型  1：储值卡 2：次卡
     */
    public void setCardType(Integer cardType) {
        this.cardType = cardType;
    }

    /**
     * 套餐名称
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * 套餐名称
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * 是否为返现活动   1：是  2：否
     */
    public Integer getIsReturnCash() {
        return isReturnCash;
    }

    /**
     * 是否为返现活动   1：是  2：否
     */
    public void setIsReturnCash(Integer isReturnCash) {
        this.isReturnCash = isReturnCash;
    }

    /**
     * 充值金额（元）
     */
    public Integer getRecharge() {
        return recharge;
    }

    /**
     * 充值金额（元）
     */
    public void setRecharge(Integer recharge) {
        this.recharge = recharge;
    }

    /**
     * 到账（元）
     */
    public Integer getArrive() {
        return arrive;
    }

    /**
     * 到账（元）
     */
    public void setArrive(Integer arrive) {
        this.arrive = arrive;
    }

    /**
     * 适用门店id，多个以英文逗号","分隔，0表示适用于所有门店
     */
    public String getGymIds() {
        return gymIds;
    }

    /**
     * 适用门店id，多个以英文逗号","分隔，0表示适用于所有门店
     */
    public void setGymIds(String gymIds) {
        this.gymIds = gymIds;
    }

    /**
     * 适用课程id，多个以英文逗号","分隔，0表示适用于所有课程
     */
    public String getCourseIds() {
        return courseIds;
    }

    /**
     * 适用课程id，多个以英文逗号","分隔，0表示适用于所有课程
     */
    public void setCourseIds(String courseIds) {
        this.courseIds = courseIds;
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
     * 注意事项
     */
    public String getNotes() {
        return notes;
    }

    /**
     * 注意事项
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * 状态 1：上线  2：下线
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 状态 1：上线  2：下线
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 数据更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 数据更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<Object> getSupportGym() {
        return supportGym;
    }

    public void setSupportGym(List<Object> supportGym) {
        this.supportGym = supportGym;
    }

    public List<Object> getSupportCourse() {
        return supportCourse;
    }

    public void setSupportCourse(List<Object> supportCourse) {
        this.supportCourse = supportCourse;
    }

    public List<TReturnCashCondition> getReturnCashCondition() {
        return returnCashCondition;
    }

    public void setReturnCashCondition(List<TReturnCashCondition> returnCashCondition) {
        this.returnCashCondition = returnCashCondition;
    }

    public Integer getCardNum() {
        return cardNum;
    }

    public void setCardNum(Integer cardNum) {
        this.cardNum = cardNum;
    }
}