package com.hyperfit.dao.impl;

import com.hyperfit.dao.TGymMapper;
import com.hyperfit.entity.TGym;
import com.hyperfit.entity.TGymCoach;
import com.hyperfit.util.PageEntity;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository("tGymMapperImpl")
public class TGymMapperImpl extends SqlSessionDaoSupport implements TGymMapper {
    @Override
    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public List<TGym> getGymList(PageEntity pageEntity) {
        return getSqlSession().selectList("TGymMapper.getGym",
                pageEntity.getMap(), new RowBounds(pageEntity.getPageIndex(), pageEntity.getPageSize()));
    }

    @Override
    public List<TGym> getGyms(Map<String, Object> param) {
        return getSqlSession().selectList("TGymMapper.getGym", param);
    }

    @Override
    public TGym getGymInfo(Map<String, Object> param) {
        return getSqlSession().selectOne("TGymMapper.getGym", param);
    }

    @Override
    public int insertGym(TGym record) {
        return getSqlSession().insert("TGymMapper.insertGym", record);
    }

    @Override
    public Integer updateGymInfo(Map<String, Object> param) {
        return getSqlSession().update("TGymMapper.updateGym", param);
    }

    @Override
    public int delGymCoaches(Map<String, Object> param) {
        return getSqlSession().delete("TGymMapper.delGymCoaches", param);
    }

    @Override
    public int addGymCoaches(List<TGymCoach> list) {
        return getSqlSession().insert("TGymMapper.addGymCoaches", list);
    }
}