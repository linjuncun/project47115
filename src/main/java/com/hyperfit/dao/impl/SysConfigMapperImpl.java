package com.hyperfit.dao.impl;

import com.hyperfit.dao.SysConfigMapper;
import com.hyperfit.entity.SysConfig;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository("sysConfigMapperImpl")
public class SysConfigMapperImpl extends SqlSessionDaoSupport implements SysConfigMapper {
    @Override
    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public SysConfig getSysConfig(Map<String, Object> param) {
        return getSqlSession().selectOne("SysConfigMapper.getSysConfig", param);
    }

    @Override
    public String getSessionByKey(Map<String, Object> param) {
        return getSqlSession().selectOne("SysConfigMapper.getSessionByKey", param);
    }

    @Override
    public int addSession(Map<String, Object> param) {
        return getSqlSession().insert("SysConfigMapper.addSession", param);
    }

    @Override
    public int updateSession(Map<String, Object> param) {
        return getSqlSession().update("SysConfigMapper.updateSession", param);
    }

    @Override
    public int updateSysConfig(Map<String, Object> param) {
        return getSqlSession().update("SysConfigMapper.updateSysConfig", param);
    }

    @Override
    public Object getUserView(Map<String, Object> param) {
        return getSqlSession().selectOne("SysConfigMapper.getUserView", param);
    }

    @Override
    public List<Map<String, Object>> getOrderView(Map<String, Object> param) {
        return getSqlSession().selectList("SysConfigMapper.getOrderView", param);
    }

    @Override
    public List<Map<String, Object>> getClassHourView(Map<String, Object> param) {
        return getSqlSession().selectList("SysConfigMapper.getClassHourView", param);
    }

    @Override
    public Object getTotalView(Map<String, Object> param) {
        return getSqlSession().selectOne("SysConfigMapper.getTotalView", param);
    }

    @Override
    public List<Map<String, Object>> getCardView(Map<String, Object> param) {
        return getSqlSession().selectList("SysConfigMapper.getCardView", param);
    }

    @Override
    public Object getCourseTotal(Map<String, Object> param) {
        return getSqlSession().selectOne("SysConfigMapper.getCourseTotal", param);
    }

    @Override
    public List<Map<String, Object>> getCourseNumView(Map<String, Object> param) {
        return getSqlSession().selectList("SysConfigMapper.getCourseNumView", param);
    }

    @Override
    public List<Map<String, Object>> getCourseOrderView(Map<String, Object> param) {
        return getSqlSession().selectList("SysConfigMapper.getCourseOrderView", param);
    }

    @Override
    public Object getUserCardView(Map<String, Object> param) {
        return getSqlSession().selectOne("SysConfigMapper.getUserCardView", param);
    }

    @Override
    public Object getCardStatusView(Map<String, Object> param) {
        return getSqlSession().selectOne("SysConfigMapper.getCardStatusView", param);
    }
}