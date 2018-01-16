package com.hyperfit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.List;

/**
 * t_course表实体类
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TCourse {
    /**
     * 课程id
     */
    private Integer courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程类型  1：团课   2：私教
     */
    private Integer courseType;
    private String courseTypeStr;//用于数据导出

    /**
     * 模版id
     */
    private Integer templateId;
    private String templateName;

    /**
     * 健身房id
     */
    private Integer gymId;

    /**
     * 教练用户id
     */
    private Integer userId;
    /**
     * 教练openid
     */
    private String openid;

    /**
     * 课程价格
     */
    private Integer price;

    /**
     * 课程实际支付价格
     */
    private Integer amount;

    /**
     * 课程标签,多个以英文逗号","分隔
     */
    private String courseLabel;

    /**
     * 课程介绍
     */
    private String intro;

    /**
     * 注意事项
     */
    private String notes;

    /**
     * 课程状态   1：正常  2：关闭  3：删除
     */
    private Integer status;

    /**
     * 教练状态 1:启用 2 :停用 3:删除
     */
    private Integer userStatus;

    /**
     * 是否预订 0：未预订 1：已预订
     */
    private Integer isOrder;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 上课时间
     */
    private Date classHour;
    private String classHourStr;//用于数据导出

    /**
     * 时长（分钟）
     */
    private Integer minutes;

    /**
     * 最少开课人数
     */
    private Integer minNum;

    /**
     * 上课人数上限
     */
    private Integer maxNum;

    /**
     * 课程预约人数
     */
    private Integer orderNum;

    /**
     * 教练头像
     */
    private String headimgurl;

    /**
     * 教练姓名
     */
    private String name;

    /**
     * 教练介绍
     */
    private String coachIntro;

    /**
     * 教练标签,多个以英文逗号","分隔
     */
    private String coachLabel;

    /**
     * 课程图片,多张以英文逗号","分隔
     */
    private String images;

    /**
     * 订单id
     */
    private Integer orderId;

    /**
     * 订单状态  订单状态 1：待付款 2：购买中 3：已完成 4：申请退款 5：已退款 6：退款异常
     */
    private Integer orderStatus;

    /**
     * 健身房名称
     */
    private String gymName;
    /**
     * 长期目标
     */
    private String longGoal;
    /**
     * 近期目标
     */
    private String shortGoal;
    /**
     * 任务
     */
    private String task;
    /**
     * 教练小结
     */
    private String summary;

    /**
     * 是否停止预约 0：未停止 1：已停止
     */
    private Integer stopOrder;

    /**
     * 类型 1：普通课程 2：活动课程
     */
    private Integer type;
    /**
     * 活动销售状态 1：在售 2：下线
     */
    private Integer saleStatus;
    /**
     * 活动报名人数上限
     */
    private Integer maxPeople;
    /**
     * 广告语
     */
    private String solgan;
    /**
     * 活动门店id，多个以英文逗号","分隔，0表示所有门店
     */
    private String gymIds;
    /**
     * 活动图片,多张以英文逗号","分隔
     */
    private String imgs;
    /**
     * 参与活动的门店list
     */
    private List<Object> supportGym;

    /**
     * 课程id
     */
    public Integer getCourseId() {
        return courseId;
    }

    /**
     * 课程id
     */
    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    /**
     * 课程名称
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * 课程名称
     */
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    /**
     * 课程类型  1：团课   2：私教
     */
    public Integer getCourseType() {
        return courseType;
    }

    /**
     * 课程类型  1：团课   2：私教
     */
    public void setCourseType(Integer courseType) {
        this.courseType = courseType;
    }

    /**
     * 模版id
     */
    public Integer getTemplateId() {
        return templateId;
    }

    /**
     * 模版id
     */
    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    /**
     * 健身房id
     */
    public Integer getGymId() {
        return gymId;
    }

    /**
     * 健身房id
     */
    public void setGymId(Integer gymId) {
        this.gymId = gymId;
    }

    /**
     * 教练用户id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 教练用户id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 课程价格
     */
    public Integer getPrice() {
        return price;
    }

    /**
     * 课程价格
     */
    public void setPrice(Integer price) {
        this.price = price;
    }

    /**
     * 课程标签,多个以英文逗号","分隔
     */
    public String getCourseLabel() {
        return courseLabel;
    }

    /**
     * 课程标签,多个以英文逗号","分隔
     */
    public void setCourseLabel(String courseLabel) {
        this.courseLabel = courseLabel;
    }

    /**
     * 课程介绍
     */
    public String getIntro() {
        return intro;
    }

    /**
     * 课程介绍
     */
    public void setIntro(String intro) {
        this.intro = intro;
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
     * 课程状态   1：正常  2：关闭  3：删除
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 课程状态   1：正常  2：关闭  3：删除
     */
    public void setStatus(Integer status) {
        this.status = status;
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

    public Date getClassHour() {
        return classHour;
    }

    public void setClassHour(Date classHour) {
        this.classHour = classHour;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public Integer getMinNum() {
        return minNum;
    }

    public void setMinNum(Integer minNum) {
        this.minNum = minNum;
    }

    public Integer getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(Integer maxNum) {
        this.maxNum = maxNum;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getCoachIntro() {
        return coachIntro;
    }

    public void setCoachIntro(String coachIntro) {
        this.coachIntro = coachIntro;
    }

    public String getCoachLabel() {
        return coachLabel;
    }

    public void setCoachLabel(String coachLabel) {
        this.coachLabel = coachLabel;
    }

    public Integer getIsOrder() {
        return isOrder;
    }

    public void setIsOrder(Integer isOrder) {
        this.isOrder = isOrder;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getGymName() {
        return gymName;
    }

    public void setGymName(String gymName) {
        this.gymName = gymName;
    }

    public String getLongGoal() {
        return longGoal;
    }

    public void setLongGoal(String longGoal) {
        this.longGoal = longGoal;
    }

    public String getShortGoal() {
        return shortGoal;
    }

    public void setShortGoal(String shortGoal) {
        this.shortGoal = shortGoal;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Integer getStopOrder() {
        return stopOrder;
    }

    public void setStopOrder(Integer stopOrder) {
        this.stopOrder = stopOrder;
    }

    public String getCourseTypeStr() {
        return courseTypeStr;
    }

    public void setCourseTypeStr(String courseTypeStr) {
        this.courseTypeStr = courseTypeStr;
    }

    public String getClassHourStr() {
        return classHourStr;
    }

    public void setClassHourStr(String classHourStr) {
        this.classHourStr = classHourStr;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getMaxPeople() {
        return maxPeople;
    }

    public void setMaxPeople(Integer maxPeople) {
        this.maxPeople = maxPeople;
    }

    public String getSolgan() {
        return solgan;
    }

    public void setSolgan(String solgan) {
        this.solgan = solgan;
    }

    public String getGymIds() {
        return gymIds;
    }

    public void setGymIds(String gymIds) {
        this.gymIds = gymIds;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }

    public Integer getSaleStatus() {
        return saleStatus;
    }

    public void setSaleStatus(Integer saleStatus) {
        this.saleStatus = saleStatus;
    }

    public List<Object> getSupportGym() {
        return supportGym;
    }

    public void setSupportGym(List<Object> supportGym) {
        this.supportGym = supportGym;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Integer getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Integer userStatus) {
        this.userStatus = userStatus;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
}