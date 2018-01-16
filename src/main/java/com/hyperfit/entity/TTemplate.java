package com.hyperfit.entity;

import java.util.Date;

/**
 * t_template表实体类
 */
public class TTemplate {
    /**
     * 模版id
     */
    private Integer templateId;

    /**
     * 模版名称
     */
    private String templateName;

    /**
     * 课程类型  1：团课   2：私教
     */
    private Integer courseType;

    /**
     * 时长（分钟）
     */
    private Integer minutes;

    /**
     * 最少开课人数
     */
    private Integer minNum;

    /**
     * 上课人数上限（若是私教，表示私教类型,值是几人私教的意思，最多三人私教，如 1:单人私教  2:两人私教  3:三人私教）
     */
    private Integer maxNum;

    /**
     * 介绍
     */
    private String intro;

    /**
     * 注意事项
     */
    private String notes;

    /**
     * 课程图片,多张以英文逗号","分隔
     */
    private String images;

    /**
     * 模版状态   1：正常  2：删除
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /** 模版id */
    public Integer getTemplateId() {
        return templateId;
    }

    /** 模版id */
    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    /** 模版名称 */
    public String getTemplateName() {
        return templateName;
    }

    /** 模版名称 */
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    /** 课程类型  1：团课   2：私教 */
    public Integer getCourseType() {
        return courseType;
    }

    /** 课程类型  1：团课   2：私教 */
    public void setCourseType(Integer courseType) {
        this.courseType = courseType;
    }

    /** 时长（分钟） */
    public Integer getMinutes() {
        return minutes;
    }

    /** 时长（分钟） */
    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    /** 最少开课人数 */
    public Integer getMinNum() {
        return minNum;
    }

    /** 最少开课人数 */
    public void setMinNum(Integer minNum) {
        this.minNum = minNum;
    }

    /** 上课人数上限（若是私教，表示私教类型,值是几人私教的意思，最多三人私教，如 1:单人私教  2:两人私教  3:三人私教） */
    public Integer getMaxNum() {
        return maxNum;
    }

    /** 上课人数上限（若是私教，表示私教类型,值是几人私教的意思，最多三人私教，如 1:单人私教  2:两人私教  3:三人私教） */
    public void setMaxNum(Integer maxNum) {
        this.maxNum = maxNum;
    }

    /** 介绍 */
    public String getIntro() {
        return intro;
    }

    /** 介绍 */
    public void setIntro(String intro) {
        this.intro = intro;
    }

    /** 注意事项 */
    public String getNotes() {
        return notes;
    }

    /** 注意事项 */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /** 课程图片,多张以英文逗号","分隔 */
    public String getImages() {
        return images;
    }

    /** 课程图片,多张以英文逗号","分隔 */
    public void setImages(String images) {
        this.images = images;
    }

    /** 模版状态   1：正常  2：删除 */
    public Integer getStatus() {
        return status;
    }

    /** 模版状态   1：正常  2：删除 */
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
}