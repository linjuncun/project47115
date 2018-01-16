package com.hyperfit.dao;

import com.hyperfit.entity.TCoupon;
import com.hyperfit.util.PageEntity;

import java.util.List;
import java.util.Map;

/**
 * <p> 优惠券dao </p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/11/22 14:48
 */
public interface TCouponMapper {

    /**
     * 微信-查询用户优惠券列表
     */
    List<TCoupon> getUserCoupon(PageEntity pageEntity);

    /**
     * 新增用户优惠券
     */
    int addUserCoupon(TCoupon record);

    /**
     * 更新用户优惠券
     */
    Integer updateUserCoupon(Map<String, Object> param);

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