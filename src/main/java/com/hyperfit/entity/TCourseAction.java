package com.hyperfit.entity;

/**
 * t_course_action表实体类
 */
public class TCourseAction {
    /**
     * 动作id
     */
    private Long actionId;

    /**
     * 课程id
     */
    private Long courseId;

    /**
     * 动作
     */
    private String actions;

    /**
     * 负荷次数
     */
    private String times;

    /**
     * 间歇
     */
    private String intervals;

    /**
     * 备注
     */
    private String remark;

    /** 动作id */
    public Long getActionId() {
        return actionId;
    }

    /** 动作id */
    public void setActionId(Long actionId) {
        this.actionId = actionId;
    }

    /** 课程id */
    public Long getCourseId() {
        return courseId;
    }

    /** 课程id */
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    /** 负荷次数 */
    public String getTimes() {
        return times;
    }

    /** 负荷次数 */
    public void setTimes(String times) {
        this.times = times;
    }

    /** 备注 */
    public String getRemark() {
        return remark;
    }

    /** 备注 */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    public String getIntervals() {
        return intervals;
    }

    public void setIntervals(String intervals) {
        this.intervals = intervals;
    }
}