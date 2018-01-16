package com.hyperfit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.List;

/**
 * t_gym表实体类
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TGym {
    /**
     * 健身房id
     */
    private Integer gymId;

    /**
     * 健身房类型 1：常规店 2：共享店
     */
    private Integer gymType;
    private String gymTypeStr;//用于导出数据

    /**
     * 店长的系统用户id
     */
    private Integer sysUserId;
    /**
     * 店长名字
     */
    private String truename;

    /**
     * 健身房名称
     */
    private String gymName;

    /**
     * 省、市、区位置id
     */
    private Integer locationId;

    /**
     * 具体地址
     */
    private String address;

    /**
     * 健身房介绍
     */
    private String intro;

    /**
     * 健身房图片
     */
    private String images;

    /**
     * 权重
     */
    private Integer weight;

    /**
     * 健身房所在省
     */
    private String province;
    private Integer provinceId;
    /**
     * 健身房所在市
     */
    private String city;
    private Integer cityId;
    /**
     * 健身房所在区
     */
    private String district;
    private Integer districtId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 状态 1：启用 2：停用
     */
    private Integer status;

    /**
     * 是否是权重最高的健身房  0：否  1：是
     */
    private Integer isMaxWeight;

    /**
     * 教练列表
     */
    private List<TUser> coachList;
    private String coachFullTime;//全职教练姓名，用于导出数据
    private String coachPartTime;//兼职教练姓名，用于导出数据

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
     * 店长的系统用户id
     */
    public Integer getSysUserId() {
        return sysUserId;
    }

    /**
     * 店长的系统用户id
     */
    public void setSysUserId(Integer sysUserId) {
        this.sysUserId = sysUserId;
    }

    /**
     * 健身房名称
     */
    public String getGymName() {
        return gymName;
    }

    /**
     * 健身房名称
     */
    public void setGymName(String gymName) {
        this.gymName = gymName;
    }

    /**
     * 省、市、区位置id
     */
    public Integer getLocationId() {
        return locationId;
    }

    /**
     * 省、市、区位置id
     */
    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    /**
     * 具体地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 具体地址
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 健身房介绍
     */
    public String getIntro() {
        return intro;
    }

    /**
     * 健身房介绍
     */
    public void setIntro(String intro) {
        this.intro = intro;
    }

    /**
     * 健身房图片
     */
    public String getImages() {
        return images;
    }

    /**
     * 健身房图片
     */
    public void setImages(String images) {
        this.images = images;
    }

    /**
     * 权重
     */
    public Integer getWeight() {
        return weight;
    }

    /**
     * 权重
     */
    public void setWeight(Integer weight) {
        this.weight = weight;
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

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<TUser> getCoachList() {
        return coachList;
    }

    public void setCoachList(List<TUser> coachList) {
        this.coachList = coachList;
    }

    public Integer getGymType() {
        return gymType;
    }

    public void setGymType(Integer gymType) {
        this.gymType = gymType;
    }

    public Integer getIsMaxWeight() {
        return isMaxWeight;
    }

    public void setIsMaxWeight(Integer isMaxWeight) {
        this.isMaxWeight = isMaxWeight;
    }

    public String getTruename() {
        return truename;
    }

    public void setTruename(String truename) {
        this.truename = truename;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Integer districtId) {
        this.districtId = districtId;
    }

    public String getGymTypeStr() {
        return gymTypeStr;
    }

    public void setGymTypeStr(String gymTypeStr) {
        this.gymTypeStr = gymTypeStr;
    }

    public String getCoachFullTime() {
        return coachFullTime;
    }

    public void setCoachFullTime(String coachFullTime) {
        this.coachFullTime = coachFullTime;
    }

    public String getCoachPartTime() {
        return coachPartTime;
    }

    public void setCoachPartTime(String coachPartTime) {
        this.coachPartTime = coachPartTime;
    }
}