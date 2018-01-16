package com.hyperfit.dao.impl;

import com.hyperfit.dao.TCourseActionMapper;
import com.hyperfit.entity.TCourseAction;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository("tCourseActionMapperImpl")
public class TCourseActionMapperImpl extends SqlSessionDaoSupport implements TCourseActionMapper {
    @Override
    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public List<TCourseAction> getCourseActions(Map<String, Object> param) {
        return getSqlSession().selectList("TCourseActionMapper.getCourseAction", param);
    }

    @Override
    public int addCourseAction(TCourseAction record) {
        return getSqlSession().insert("TCourseActionMapper.addCourseAction", record);
    }

    @Override
    public Integer updateCourseAction(TCourseAction record) {
        return getSqlSession().update("TCourseActionMapper.updateCourseAction", record);
    }

    @Override
    public int delCourseAction(Map<String, Object> param) {
        return getSqlSession().delete("TCourseActionMapper.delCourseAction", param);
    }

}