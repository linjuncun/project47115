package com.hyperfit.dao.impl;

import com.hyperfit.dao.TCourseMapper;
import com.hyperfit.entity.TCourse;
import com.hyperfit.util.PageEntity;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p> 课程dao </p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/11/20 19:57
 */
@Repository("tCourseMapperImpl")
public class TCourseMapperImpl extends SqlSessionDaoSupport implements TCourseMapper {
    @Override
    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public List<TCourse> getCourseList(PageEntity pageEntity) {
        return getSqlSession().selectList("TCourseMapper.getCourse",
                pageEntity.getMap(), new RowBounds(pageEntity.getPageIndex(), pageEntity.getPageSize()));
    }

    @Override
    public List<TCourse> getCourses(Map<String, Object> param) {
        return getSqlSession().selectList("TCourseMapper.getCourse", param);
    }

    @Override
    public List<TCourse> getUserCourse(PageEntity pageEntity) {
        return getSqlSession().selectList("TCourseMapper.getUserCourse",
                pageEntity.getMap(), new RowBounds(pageEntity.getPageIndex(), pageEntity.getPageSize()));
    }

    @Override
    public TCourse getCourseInfo(Map<String, Object> param) {
        return getSqlSession().selectOne("TCourseMapper.getCourse", param);
    }

    @Override
    public int insertCourse(TCourse record) {
        return getSqlSession().insert("TCourseMapper.insertCourse", record);
    }

    @Override
    public Integer updateCourseInfo(Map<String, Object> param) {
        return getSqlSession().update("TCourseMapper.updateCourse", param);
    }

    @Override
    public List<TCourse> getCourseNoRepeat(Map<String, Object> param) {
        return getSqlSession().selectList("TCourseMapper.getCourseNoRepeat", param);
    }
}