package com.hyperfit.service.impl;

import com.hyperfit.dao.TCouponMapper;
import com.hyperfit.entity.TCoupon;
import com.hyperfit.service.CouponService;
import com.hyperfit.util.PageEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Service("couponServiceImpl")
public class CouponServiceImpl implements CouponService {

    @Resource(name = "tCouponMapperImpl")
    private TCouponMapper tCouponMapper;

    @Override
    public List<TCoupon> getUserCoupon(PageEntity pageEntity) {
        return tCouponMapper.getUserCoupon(pageEntity);
    }

    @Override
    public int addUserCoupon(Map<String, Object> param) {
        String userIds = (String) param.get("userIds");
        String couponId = (String) param.get("couponId");
        String[] ids = userIds.split(",");
        for (String userId : ids) {
            TCoupon temp = new TCoupon();
            temp.setUserId(Integer.parseInt(userId));
            temp.setCouponId(Integer.parseInt(couponId));
            tCouponMapper.addUserCoupon(temp);
        }
        return 1;
    }

    @Override
    public List<TCoupon> getCouponList(PageEntity pageEntity) {
        return tCouponMapper.getCouponList(pageEntity);
    }

    @Override
    public List<TCoupon> getCoupons(Map<String, Object> param) {
        return tCouponMapper.getCoupons(param);
    }

    @Override
    public TCoupon getCouponInfo(Map<String, Object> param) {
        return tCouponMapper.getCouponInfo(param);
    }

    @Override
    public int insertCoupon(TCoupon record) {
        return tCouponMapper.insertCoupon(record);
    }

    @Override
    public Integer updateCouponInfo(Map<String, Object> param) {
        return tCouponMapper.updateCouponInfo(param);
    }
}
