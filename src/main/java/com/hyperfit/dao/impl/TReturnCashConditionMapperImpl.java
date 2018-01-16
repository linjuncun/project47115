package com.hyperfit.dao.impl;

import com.hyperfit.dao.TReturnCashConditionMapper;
import com.hyperfit.entity.TReturnCashCondition;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository("tReturnCashConditionMapperImpl")
public class TReturnCashConditionMapperImpl extends SqlSessionDaoSupport implements TReturnCashConditionMapper {
    @Override
    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public List<TReturnCashCondition> getConditionList(Map<String, Object> param) {
        return getSqlSession().selectList("TReturnCashConditionMapper.getCondition", param);
    }

    @Override
    public int addCondition(TReturnCashCondition record) {
        return getSqlSession().insert("TReturnCashConditionMapper.addCondition", record);
    }

    @Override
    public int delCondition(Map<String, Object> param) {
        return getSqlSession().delete("TReturnCashConditionMapper.delCondition", param);
    }
}