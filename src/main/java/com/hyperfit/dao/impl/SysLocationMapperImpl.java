package com.hyperfit.dao.impl;

import com.hyperfit.dao.SysLocationMapper;
import com.hyperfit.entity.SysLocation;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository("sysLocationMapperImpl")
public class SysLocationMapperImpl extends SqlSessionDaoSupport implements SysLocationMapper {
    @Override
    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public List<SysLocation> getGymLocationList(Map<String, Object> param) {
        return getSqlSession().selectList("SysLocationMapper.getGymLocationList", param);
    }

    @Override
    public List<SysLocation> getLocation(Map<String, Object> param) {
        return getSqlSession().selectList("SysLocationMapper.getLocation", param);
    }
}