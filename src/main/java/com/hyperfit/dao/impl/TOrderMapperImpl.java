package com.hyperfit.dao.impl;

import com.hyperfit.dao.TOrderMapper;
import com.hyperfit.entity.TOrder;
import com.hyperfit.util.PageEntity;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository("tOrderMapperImpl")
public class TOrderMapperImpl extends SqlSessionDaoSupport implements TOrderMapper {
    @Override
    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public List<TOrder> getOrderList(PageEntity pageEntity) {
        return getSqlSession().selectList("TOrderMapper.getOrder",
                pageEntity.getMap(), new RowBounds(pageEntity.getPageIndex(), pageEntity.getPageSize()));
    }

    @Override
    public TOrder getOrderInfo(Map<String, Object> param) {
        return getSqlSession().selectOne("TOrderMapper.getOrder", param);
    }

    @Override
    public int insertOrder(TOrder record) {
        return getSqlSession().insert("TOrderMapper.insertOrder", record);
    }

    @Override
    public Integer updateOrderInfo(Map<String, Object> param) {
        return getSqlSession().update("TOrderMapper.updateOrder", param);
    }
}