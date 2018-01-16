package com.hyperfit.entity;

/**
 * sys_function表实体类
 */
public class SysFunction {
    /**
     * 功能id
     */
    private Integer functionId;

    /**
     * 父功能id
     */
    private Integer parentId;

    /**
     * 功能编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 功能菜单URL
     */
    private String url;

    /**
     * 类型   1：模块  2：页面 3：按钮
     */
    private Integer type;

    /**
     * 排序，值越小越靠前
     */
    private Integer orderIndex;

    /** 功能id */
    public Integer getFunctionId() {
        return functionId;
    }

    /** 功能id */
    public void setFunctionId(Integer functionId) {
        this.functionId = functionId;
    }

    /** 父功能id */
    public Integer getParentId() {
        return parentId;
    }

    /** 父功能id */
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    /** 功能编码 */
    public String getCode() {
        return code;
    }

    /** 功能编码 */
    public void setCode(String code) {
        this.code = code;
    }

    /** 名称 */
    public String getName() {
        return name;
    }

    /** 名称 */
    public void setName(String name) {
        this.name = name;
    }

    /** 功能菜单URL */
    public String getUrl() {
        return url;
    }

    /** 功能菜单URL */
    public void setUrl(String url) {
        this.url = url;
    }

    /** 类型   1：模块  2：页面 3：按钮 */
    public Integer getType() {
        return type;
    }

    /** 类型   1：模块  2：页面 3：按钮 */
    public void setType(Integer type) {
        this.type = type;
    }

    /** 排序，值越小越靠前 */
    public Integer getOrderIndex() {
        return orderIndex;
    }

    /** 排序，值越小越靠前 */
    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}