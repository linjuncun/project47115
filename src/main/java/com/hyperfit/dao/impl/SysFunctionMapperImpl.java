package com.hyperfit.dao.impl;

import com.hyperfit.dao.SysFunctionMapper;
import com.hyperfit.entity.SysFunction;
import com.hyperfit.entity.SysUserFunction;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository("sysFunctionMapperImpl")
public class SysFunctionMapperImpl extends SqlSessionDaoSupport implements SysFunctionMapper {

    @Override
    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public List<SysFunction> getUserFunctions(Map<String, Object> param) {
        return getSqlSession().selectList("SysFunctionMapper.getUserFunctions", param);
    }

    @Override
    public List<SysFunction> getSysFunctions(Map<String, Object> param) {
        return getSqlSession().selectList("SysFunctionMapper.getSysFunctions", param);
    }

    @Override
    public int delSysUserFunctions(Map<String, Object> param) {
        return getSqlSession().delete("SysFunctionMapper.delSysUserFunctions", param);
    }

    @Override
    public int addSysUserFunctions(List<SysUserFunction> list) {
        return getSqlSession().insert("SysFunctionMapper.addSysUserFunctions", list);
    }
}
