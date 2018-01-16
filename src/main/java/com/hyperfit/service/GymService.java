package com.hyperfit.service;

import com.hyperfit.entity.SysLocation;
import com.hyperfit.entity.TGym;
import com.hyperfit.util.PageEntity;

import java.util.List;
import java.util.Map;

/**
 * <p> 健身房service</p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/11/18 15:19
 */
public interface GymService {

    /**
     * 查询健身房分区地区列表
     */
    List<SysLocation> getGymLocationList(Map<String, Object> param);

    /**
     * 查询健身房分页列表
     */
    List<TGym> getGymList(PageEntity pageEntity);

    /**
     * 查询健身房列表
     */
    List<TGym> getGyms(Map<String, Object> param);

    /**
     * 查询健身房信息
     */
    TGym getGymInfo(Map<String, Object> param);

    /**
     * 新增健身房
     */
    int insertGym(Map<String, Object> param);

    /**
     * 更新健身房信息
     */
    Integer updateGymInfo(Map<String, Object> param);

}
