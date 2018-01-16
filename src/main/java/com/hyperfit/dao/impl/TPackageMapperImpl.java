package com.hyperfit.dao.impl;

import com.hyperfit.dao.TPackageMapper;
import com.hyperfit.entity.TPackage;
import com.hyperfit.util.PageEntity;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository("tPackageMapperImpl")
public class TPackageMapperImpl extends SqlSessionDaoSupport implements TPackageMapper {
    @Override
    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public List<TPackage> getPackageList(PageEntity pageEntity) {
        return getSqlSession().selectList("TPackageMapper.getPackage",
                pageEntity.getMap(), new RowBounds(pageEntity.getPageIndex(), pageEntity.getPageSize()));
    }

    @Override
    public TPackage getPackageInfo(Map<String, Object> param) {
        return getSqlSession().selectOne("TPackageMapper.getPackage", param);
    }

    @Override
    public List<Object> getCardNumber(Map<String, Object> param) {
        return getSqlSession().selectList("TPackageMapper.getCardNumber", param);
    }

    @Override
    public int insertPackage(TPackage record) {
        return getSqlSession().insert("TPackageMapper.insertPackage", record);
    }

    @Override
    public Integer updatePackageInfo(Map<String, Object> param) {
        return getSqlSession().update("TPackageMapper.updatePackage", param);
    }
}