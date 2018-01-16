package com.hyperfit.entity;

/**
 * sys_config表实体类
 */
public class SysConfig {
    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 协议名称
     */
    private String agreementName;

    /**
     * 会员协议内容
     */
    private String agreement;

    /**
     * 课程可预约时间（新建课程后多少小时可预约）
     */
    private Integer onOrderTime;

    /**
     * 课程截止预约时间（开课前多少小时不能预约）
     */
    private Integer offOrderTime;

    /**
     * 储值卡名称
     */
    private String valueCardName;
    /**
     * 储值卡简介，每行简介以<br>换行符号分隔
     */
    private String valueCardIntro;
    /**
     * 次卡名称
     */
    private String numberCardName;
    /**
     * 次卡简介，每行简介以<br>换行符号分隔
     */
    private String numberCardIntro;


    /**
     * 品牌名称
     */
    public String getBrandName() {
        return brandName;
    }

    /**
     * 品牌名称
     */
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    /**
     * 协议名称
     */
    public String getAgreementName() {
        return agreementName;
    }

    /**
     * 协议名称
     */
    public void setAgreementName(String agreementName) {
        this.agreementName = agreementName;
    }

    /**
     * 会员协议内容
     */
    public String getAgreement() {
        return agreement;
    }

    /**
     * 会员协议内容
     */
    public void setAgreement(String agreement) {
        this.agreement = agreement;
    }

    /**
     * 课程可预约时间（新建课程后多少小时可预约）
     */
    public Integer getOnOrderTime() {
        return onOrderTime;
    }

    /**
     * 课程可预约时间（新建课程后多少小时可预约）
     */
    public void setOnOrderTime(Integer onOrderTime) {
        this.onOrderTime = onOrderTime;
    }

    /**
     * 课程截止预约时间（开课前多少小时不能预约）
     */
    public Integer getOffOrderTime() {
        return offOrderTime;
    }

    /**
     * 课程截止预约时间（开课前多少小时不能预约）
     */
    public void setOffOrderTime(Integer offOrderTime) {
        this.offOrderTime = offOrderTime;
    }

    public String getValueCardName() {
        return valueCardName;
    }

    public void setValueCardName(String valueCardName) {
        this.valueCardName = valueCardName;
    }

    public String getValueCardIntro() {
        return valueCardIntro;
    }

    public void setValueCardIntro(String valueCardIntro) {
        this.valueCardIntro = valueCardIntro;
    }

    public String getNumberCardName() {
        return numberCardName;
    }

    public void setNumberCardName(String numberCardName) {
        this.numberCardName = numberCardName;
    }

    public String getNumberCardIntro() {
        return numberCardIntro;
    }

    public void setNumberCardIntro(String numberCardIntro) {
        this.numberCardIntro = numberCardIntro;
    }
}