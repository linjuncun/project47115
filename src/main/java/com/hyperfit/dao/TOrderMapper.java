package com.hyperfit.dao;

import com.hyperfit.entity.TOrder;
import com.hyperfit.util.PageEntity;

import java.util.List;
import java.util.Map;

public interface TOrderMapper {
    /**
     * 查询订单列表
     */
    List<TOrder> getOrderList(PageEntity pageEntity);

    /**
     * 查询订单信息
     */
    TOrder getOrderInfo(Map<String, Object> param);

    /**
     * 新增订单
     */
    int insertOrder(TOrder record);

    /**
     * 更新订单信息
     */
    Integer updateOrderInfo(Map<String, Object> param);

}