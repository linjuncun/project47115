package com.hyperfit.dao.impl;

import com.hyperfit.dao.TBalanceItemMapper;
import com.hyperfit.entity.TBalanceItem;
import com.hyperfit.util.PageEntity;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("tBalanceItemMapperImpl")
public class TBalanceItemMapperImpl extends SqlSessionDaoSupport implements TBalanceItemMapper {
    @Override
    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public List<TBalanceItem> getBalanceItem(PageEntity pageEntity) {
        return getSqlSession().selectList("TBalanceItemMapper.getBalanceItem",
                pageEntity.getMap(), new RowBounds(pageEntity.getPageIndex(), pageEntity.getPageSize()));
    }

    @Override
    public int insertBalanceItem(TBalanceItem record) {
        return getSqlSession().insert("TBalanceItemMapper.insertBalanceItem", record);
    }

}