package com.hyperfit.service;

import com.hyperfit.entity.TCoupon;
import com.hyperfit.util.PageEntity;

import java.util.List;
import java.util.Map;

/**
 * <p> 优惠券service</p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/11/22 15:04
 */
public interface CouponService {

    /**
     * 微信-查询用户优惠券列表
     */
    List<TCoupon> getUserCoupon(PageEntity pageEntity);

    /**
     * 添加用户优惠券
     */
    int addUserCoupon(Map<String, Object> param);

    /**
     * 查询优惠券分页列表
     */
    List<TCoupon> getCouponList(PageEntity pageEntity);

    /**
     * 查询优惠券列表
     */
    List<TCoupon> getCoupons(Map<String, Object> param);

    /**
     * 查询优惠券信息
     */
    TCoupon getCouponInfo(Map<String, Object> param);

    /**
     * 新增优惠券
     */
    int insertCoupon(TCoupon record);

    /**
     * 更新优惠券信息
     */
    Integer updateCouponInfo(Map<String, Object> param);
}
