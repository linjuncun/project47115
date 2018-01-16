package com.hyperfit.service;

import com.hyperfit.entity.TOrder;
import com.hyperfit.util.PageEntity;

import java.util.List;
import java.util.Map;

/**
 * <p> 订单service</p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/11/24 18:25
 */
public interface OrderService {

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
