package com.hyperfit.entity;

import java.util.Date;

/**
 * t_balance_item表实体类
 */
public class TBalanceItem {
    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 明细类型  1：返现  2：提现
     */
    private Integer itemType;

    /**
     * 返现活动所在套餐id
     */
    private Integer packageId;

    /**
     * 返现满足的条件id
     */
    private Integer conditionId;

    /**
     * 金额（元）
     */
    private Integer amount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 返现条件-天数
     */
    private Integer days;

    /**
     * 返现条件-金额（元）
     */
    private Integer money;

    /**
     * 返现条件-次数
     */
    private Integer times;

    /** 用户id */
    public Integer getUserId() {
        return userId;
    }

    /** 用户id */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /** 明细类型  1：返现  2：提现 */
    public Integer getItemType() {
        return itemType;
    }

    /** 明细类型  1：返现  2：提现 */
    public void setItemType(Integer itemType) {
        this.itemType = itemType;
    }

    /** 返现活动所在套餐id */
    public Integer getPackageId() {
        return packageId;
    }

    /** 返现活动所在套餐id */
    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }

    /** 返现满足的条件id */
    public Integer getConditionId() {
        return conditionId;
    }

    /** 返现满足的条件id */
    public void setConditionId(Integer conditionId) {
        this.conditionId = conditionId;
    }

    /** 金额（元） */
    public Integer getAmount() {
        return amount;
    }

    /** 金额（元） */
    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    /** 创建时间 */
    public Date getCreateTime() {
        return createTime;
    }

    /** 创建时间 */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }
}