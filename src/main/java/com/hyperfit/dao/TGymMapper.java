package com.hyperfit.dao;

import com.hyperfit.entity.TGym;
import com.hyperfit.entity.TGymCoach;
import com.hyperfit.util.PageEntity;

import java.util.List;
import java.util.Map;

/**
 * <p> 健身房dao </p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/11/18 15:08
 */
public interface TGymMapper {
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
    int insertGym(TGym record);

    /**
     * 更新健身房信息
     */
    Integer updateGymInfo(Map<String, Object> param);

    /**
     * 删除健身房教练
     */
    int delGymCoaches(Map<String, Object> param);

    /**
     * 关联健身房教练
     */
    int addGymCoaches(List<TGymCoach> list);


}