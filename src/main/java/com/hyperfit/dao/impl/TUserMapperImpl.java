package com.hyperfit.dao.impl;

import com.hyperfit.dao.TUserMapper;
import com.hyperfit.entity.TUser;
import com.hyperfit.util.PageEntity;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository("tUserMapperImpl")
public class TUserMapperImpl extends SqlSessionDaoSupport implements TUserMapper {
    @Override
    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public TUser getUserInfo(Map<String, Object> param) {
        return getSqlSession().selectOne("TUserMapper.getUserInfo", param);
    }

    @Override
    public Integer updateUserInfo(Map<String, Object> param) {
        return getSqlSession().update("TUserMapper.updateUserInfo", param);
    }

    @Override
    public int insertUser(TUser param) {
        return getSqlSession().insert("TUserMapper.insertUser", param);
    }

    @Override
    public int delUser(Map<String, Object> param) {
        return getSqlSession().delete("TUserMapper.delUser", param);
    }


    @Override
    public List<TUser> getUserList(PageEntity pageEntity) {
        return getSqlSession().selectList("TUserMapper.getUserInfo",
                pageEntity.getMap(), new RowBounds(pageEntity.getPageIndex(), pageEntity.getPageSize()));
    }

    @Override
    public List<TUser> getUsers(Map<String, Object> param) {
        return getSqlSession().selectList("TUserMapper.getUserInfo", param);
    }

    @Override
    public List<TUser> getCourseUser(PageEntity pageEntity) {
        return getSqlSession().selectList("TUserMapper.getCourseUser",
                pageEntity.getMap(), new RowBounds(pageEntity.getPageIndex(), pageEntity.getPageSize()));
    }

}