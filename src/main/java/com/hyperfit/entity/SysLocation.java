package com.hyperfit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * sys_location表实体类
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SysLocation {
    /**
     * 位置信息id
     */
    private Integer locationId;

    /**
     * 地区编码
     */
    private String code;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String district;

    /**
     * 父id
     */
    private Integer parent;

    /**
     * 省份的locationId
     */
    private Integer provinceId;

    /**
     * 是否是权重最高健身房所在位置  0：否  1：是
     */
    private Integer isMaxWeight;

    /**
     * 子地区集合
     */
    private List<SysLocation> subLoction;

    /**
     * 位置信息id
     */
    public Integer getLocationId() {
        return locationId;
    }

    /**
     * 位置信息id
     */
    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    /**
     * 地区编码
     */
    public String getCode() {
        return code;
    }

    /**
     * 地区编码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 省
     */
    public String getProvince() {
        return province;
    }

    /**
     * 省
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * 市
     */
    public String getCity() {
        return city;
    }

    /**
     * 市
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * 区
     */
    public String getDistrict() {
        return district;
    }

    /**
     * 区
     */
    public void setDistrict(String district) {
        this.district = district;
    }

    /**
     * 父id
     */
    public Integer getParent() {
        return parent;
    }

    /**
     * 父id
     */
    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public List<SysLocation> getSubLoction() {
        return subLoction;
    }

    public void setSubLoction(List<SysLocation> subLoction) {
        this.subLoction = subLoction;
    }

    public Integer getIsMaxWeight() {
        return isMaxWeight;
    }

    public void setIsMaxWeight(Integer isMaxWeight) {
        this.isMaxWeight = isMaxWeight;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }
}