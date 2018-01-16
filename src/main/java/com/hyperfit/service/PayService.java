package com.hyperfit.service;

import com.hyperfit.entity.TOrder;
import com.hyperfit.util.ApiModel;

import java.util.Map;
import java.util.SortedMap;

/**
 * <p> 支付service</p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/11/23 19:30
 */
public interface PayService {

    /**
     * 新增订单
     */
    int order(TOrder order, ApiModel apiModel) throws Exception;

    /**
     * 退课
     */
    int dropCourse(Map<String, Object> param);

    /**
     * 微信支付回调成功-更新相关数据
     */
    int updatePayData(SortedMap<Object, Object> map);

    /**
     * 微信退款通知-更新相关数据
     */
    int updateRefundData(Map<String, String> map);
}
