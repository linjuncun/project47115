package com.hyperfit.dao.impl;

import com.hyperfit.dao.TTemplateMapper;
import com.hyperfit.entity.TTemplate;
import com.hyperfit.util.PageEntity;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository("tTemplateMapperImpl")
public class TTemplateMapperImpl extends SqlSessionDaoSupport implements TTemplateMapper {
    @Override
    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public List<TTemplate> getTemplateList(PageEntity pageEntity) {
        return getSqlSession().selectList("TTemplateMapper.getTemplate",
                pageEntity.getMap(), new RowBounds(pageEntity.getPageIndex(), pageEntity.getPageSize()));
    }

    @Override
    public List<TTemplate> getTemplates(Map<String, Object> param) {
        return getSqlSession().selectList("TTemplateMapper.getTemplate", param);
    }

    @Override
    public TTemplate getTemplateInfo(Map<String, Object> param) {
        return getSqlSession().selectOne("TTemplateMapper.getTemplate", param);
    }

    @Override
    public int insertTemplate(TTemplate record) {
        return getSqlSession().insert("TTemplateMapper.insertTemplate", record);
    }

    @Override
    public Integer updateTemplateInfo(Map<String, Object> param) {
        return getSqlSession().update("TTemplateMapper.updateTemplate", param);
    }

}