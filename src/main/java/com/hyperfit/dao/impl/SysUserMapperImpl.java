package com.hyperfit.dao.impl;

import com.hyperfit.dao.SysUserMapper;
import com.hyperfit.entity.SysUser;
import com.hyperfit.util.PageEntity;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository("sysUserMapperImpl")
public class SysUserMapperImpl extends SqlSessionDaoSupport implements SysUserMapper {
    @Override
    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public List<SysUser> getSysUserList(PageEntity pageEntity) {
        return getSqlSession().selectList("SysUserMapper.getSysUser",
                pageEntity.getMap(), new RowBounds(pageEntity.getPageIndex(), pageEntity.getPageSize()));
    }

    @Override
    public List<SysUser> getSysUsers(Map<String, Object> param) {
        return getSqlSession().selectList("SysUserMapper.getSysUser", param);
    }

    @Override
    public SysUser getSysUserInfo(Map<String, Object> param) {
        return getSqlSession().selectOne("SysUserMapper.getSysUser", param);
    }

    @Override
    public int insertSysUser(SysUser record) {
        return getSqlSession().insert("SysUserMapper.insertSysUser", record);
    }

    @Override
    public int updateSysUserInfo(Map<String, Object> param) {
        return getSqlSession().update("SysUserMapper.updateSysUser", param);
    }
}