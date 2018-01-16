package com.hyperfit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.List;

/**
 * t_user表实体类
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TUser {

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 用户类型  1：普通用户  2：教练
     */
    private Integer userType;

    /**
     * 微信openid
     */
    private String openid;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 性别 0：未知 1：男 2：女
     */
    private String sex;

    /**
     * 头像
     */
    private String headimgurl;

    /**
     * 自我介绍
     */
    private String intro;

    /**
     * 状态  1:启用  2 :停用  3:删除
     */
    private Integer status;
    private String statusStr;

    /**
     * 教练图片路径，多张以英文逗号","分隔
     */
    private String coachImages;

    /**
     * 教练类型 1：全职  2：兼职
     */
    private Integer coachType;
    private String coachTypeStr;

    /**
     * 教练权重
     */
    private Integer weight;

    /**
     * 教练标签,多个以英文逗号","分隔
     */
    private String coachLabel;

    /**
     * 创建时间
     */
    private Date createTime;
    private String createTimeStr;

    /**
     * 余额（元）
     */
    private Integer balance;

    /**
     * 订单id
     */
    private Integer orderId;
    /**
     * 订单课程id
     */
    private Integer courseId;
    /**
     * 支付方式 1：微信支付 2：会员卡支付（包括储值和次卡）
     */
    private Integer payType;
    /**
     * 会员卡类型 1：储值卡 2：次卡
     */
    private Integer cardType;
    private String cardTypeStr;
    /**
     * 预约的课程是否已上课  0：否  1：是
     */
    private Integer isStart;
    /**
     * 订单状态 1：待付款 2：购买中 3：已完成 4：申请退款 5：已退款 6：退款异常
     */
    private Integer orderStatus;
    /**
     * 下单时间
     */
    private Date orderTime;

    /**
     * 教练所属健身房列表
     */
    private List<TGym> gymList;
    private String gyms;//用于数据导出

    /**
     * 用户会员卡列表
     */
    private List<TClubCard> cardList;

    /**  */
    public Integer getUserId() {
        return userId;
    }

    /**  */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 用户类型  1：普通用户  2：教练
     */
    public Integer getUserType() {
        return userType;
    }

    /**
     * 用户类型  1：普通用户  2：教练
     */
    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    /**
     * 微信openid
     */
    public String getOpenid() {
        return openid;
    }

    /**
     * 微信openid
     */
    public void setOpenid(String openid) {
        this.openid = openid;
    }

    /**
     * 用户姓名
     */
    public String getName() {
        return name;
    }

    /**
     * 用户姓名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 手机号
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 手机号
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 性别 0：未知 1：男 2：女
     */
    public String getSex() {
        return sex;
    }

    /**
     * 性别 0：未知 1：男 2：女
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * 头像
     */
    public String getHeadimgurl() {
        return headimgurl;
    }

    /**
     * 头像
     */
    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    /**
     * 自我介绍
     */
    public String getIntro() {
        return intro;
    }

    /**
     * 自我介绍
     */
    public void setIntro(String intro) {
        this.intro = intro;
    }

    /**
     * 状态  1:启用  2 :停用
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 状态  1:启用  2 :停用
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 教练图片路径，多张以英文逗号","分隔
     */
    public String getCoachImages() {
        return coachImages;
    }

    /**
     * 教练图片路径，多张以英文逗号","分隔
     */
    public void setCoachImages(String coachImages) {
        this.coachImages = coachImages;
    }

    /**
     * 教练类型 1：全职  2：兼职
     */
    public Integer getCoachType() {
        return coachType;
    }

    /**
     * 教练类型 1：全职  2：兼职
     */
    public void setCoachType(Integer coachType) {
        this.coachType = coachType;
    }

    /**
     * 教练标签
     */
    public String getCoachLabel() {
        return coachLabel;
    }

    /**
     * 教练标签
     */
    public void setCoachLabel(String coachLabel) {
        this.coachLabel = coachLabel;
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

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Integer getCardType() {
        return cardType;
    }

    public void setCardType(Integer cardType) {
        this.cardType = cardType;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public Integer getIsStart() {
        return isStart;
    }

    public void setIsStart(Integer isStart) {
        this.isStart = isStart;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public List<TGym> getGymList() {
        return gymList;
    }

    public void setGymList(List<TGym> gymList) {
        this.gymList = gymList;
    }

    public List<TClubCard> getCardList() {
        return cardList;
    }

    public void setCardList(List<TClubCard> cardList) {
        this.cardList = cardList;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public String getCoachTypeStr() {
        return coachTypeStr;
    }

    public void setCoachTypeStr(String coachTypeStr) {
        this.coachTypeStr = coachTypeStr;
    }

    public String getCardTypeStr() {
        return cardTypeStr;
    }

    public void setCardTypeStr(String cardTypeStr) {
        this.cardTypeStr = cardTypeStr;
    }

    public String getGyms() {
        return gyms;
    }

    public void setGyms(String gyms) {
        this.gyms = gyms;
    }

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }
}