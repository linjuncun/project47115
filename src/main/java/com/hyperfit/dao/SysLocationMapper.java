package com.hyperfit.dao;

import com.hyperfit.entity.SysLocation;

import java.util.List;
import java.util.Map;

public interface SysLocationMapper {

    /**
     * 查询健身房分区地区列表
     */
    List<SysLocation> getGymLocationList(Map<String, Object> param);
    
    /**
     * 查询地区字典
     */
    List<SysLocation> getLocation(Map<String, Object> param);
}