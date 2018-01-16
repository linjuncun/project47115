package com.hyperfit.dao.impl;

import com.hyperfit.dao.TClubCardMapper;
import com.hyperfit.entity.TClubCard;
import com.hyperfit.util.PageEntity;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository("tClubCardMapperImpl")
public class TClubCardMapperImpl extends SqlSessionDaoSupport implements TClubCardMapper {
    @Override
    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public List<TClubCard> getClubCardPageList(PageEntity pageEntity) {
        return getSqlSession().selectList("TClubCardMapper.getClubCard",
                pageEntity.getMap(), new RowBounds(pageEntity.getPageIndex(), pageEntity.getPageSize()));
    }

    @Override
    public List<TClubCard> getClubCardList(Map<String, Object> param) {
        return getSqlSession().selectList("TClubCardMapper.getClubCard", param);
    }

    @Override
    public List<TClubCard> getClubCards(Map<String, Object> param) {
        return getSqlSession().selectList("TClubCardMapper.getClubCard", param);
    }

    @Override
    public TClubCard getClubCardInfo(Map<String, Object> param) {
        return getSqlSession().selectOne("TClubCardMapper.getClubCard", param);
    }

    @Override
    public int insertClubCard(TClubCard record) {
        return getSqlSession().insert("TClubCardMapper.insertClubCard", record);
    }

    @Override
    public Integer updateClubCardInfo(Map<String, Object> param) {
        return getSqlSession().update("TClubCardMapper.updateClubCard", param);
    }
}