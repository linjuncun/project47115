package com.hyperfit.dao.impl;

import com.hyperfit.dao.TCouponMapper;
import com.hyperfit.entity.TCoupon;
import com.hyperfit.util.PageEntity;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository("tCouponMapperImpl")
public class TCouponMapperImpl extends SqlSessionDaoSupport implements TCouponMapper {
    @Override
    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public List<TCoupon> getUserCoupon(PageEntity pageEntity) {
        return getSqlSession().selectList("TCouponMapper.getUserCoupon",
                pageEntity.getMap(), new RowBounds(pageEntity.getPageIndex(), pageEntity.getPageSize()));
    }

    @Override
    public int addUserCoupon(TCoupon record) {
        return getSqlSession().insert("TCouponMapper.addUserCoupon", record);
    }

    @Override
    public Integer updateUserCoupon(Map<String, Object> param) {
        return getSqlSession().update("TCouponMapper.updateUserCoupon", param);
    }

    @Override
    public List<TCoupon> getCouponList(PageEntity pageEntity) {
        return getSqlSession().selectList("TCouponMapper.getCoupon",
                pageEntity.getMap(), new RowBounds(pageEntity.getPageIndex(), pageEntity.getPageSize()));
    }

    @Override
    public List<TCoupon> getCoupons(Map<String, Object> param) {
        return getSqlSession().selectList("TCouponMapper.getCoupon", param);
    }

    @Override
    public TCoupon getCouponInfo(Map<String, Object> param) {
        return getSqlSession().selectOne("TCouponMapper.getCoupon", param);
    }

    @Override
    public int insertCoupon(TCoupon record) {
        return getSqlSession().insert("TCouponMapper.insertCoupon", record);
    }

    @Override
    public Integer updateCouponInfo(Map<String, Object> param) {
        return getSqlSession().update("TCouponMapper.updateCoupon", param);
    }
}