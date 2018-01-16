package com.hyperfit.dao;

import com.hyperfit.entity.TReturnCashCondition;

import java.util.List;
import java.util.Map;

public interface TReturnCashConditionMapper {
    /**
     * 查询返现条件列表
     */
    List<TReturnCashCondition> getConditionList(Map<String, Object> param);

    /**
     * 新增返现条件
     */
    int addCondition(TReturnCashCondition record);

    /**
     * 删除返现条件
     */
    int delCondition(Map<String, Object> param);


}