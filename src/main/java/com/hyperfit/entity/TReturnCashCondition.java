package com.hyperfit.entity;

/**
 * t_return_cash_condition表实体类
 */
public class TReturnCashCondition {
    /**
     * 返现条件id
     */
    private Integer conditionId;

    /**
     * 套餐id
     */
    private Integer packageId;

    /**
     * 天数
     */
    private Integer days;

    /**
     * 金额（元）
     */
    private Integer money;

    /**
     * 次数
     */
    private Integer times;

    /**
     * 返现（元）
     */
    private Integer returnCash;

    /** 返现条件id */
    public Integer getConditionId() {
        return conditionId;
    }

    /** 返现条件id */
    public void setConditionId(Integer conditionId) {
        this.conditionId = conditionId;
    }

    /** 套餐id */
    public Integer getPackageId() {
        return packageId;
    }

    /** 套餐id */
    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }

    /** 天数 */
    public Integer getDays() {
        return days;
    }

    /** 天数 */
    public void setDays(Integer days) {
        this.days = days;
    }

    /** 金额（元） */
    public Integer getMoney() {
        return money;
    }

    /** 金额（元） */
    public void setMoney(Integer money) {
        this.money = money;
    }

    /** 次数 */
    public Integer getTimes() {
        return times;
    }

    /** 次数 */
    public void setTimes(Integer times) {
        this.times = times;
    }

    /** 返现（元） */
    public Integer getReturnCash() {
        return returnCash;
    }

    /** 返现（元） */
    public void setReturnCash(Integer returnCash) {
        this.returnCash = returnCash;
    }
}