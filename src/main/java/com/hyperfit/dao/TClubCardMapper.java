package com.hyperfit.dao;

import com.hyperfit.entity.TClubCard;
import com.hyperfit.util.PageEntity;

import java.util.List;
import java.util.Map;

/**
 * <p> 会员卡dao </p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/11/22 20:50
 */
public interface TClubCardMapper {

    /**
     * 查询会员卡分页列表
     */
    List<TClubCard> getClubCardPageList(PageEntity pageEntity);

    /**
     * 查询会员卡列表
     */
    List<TClubCard> getClubCardList(Map<String, Object> param);

    /**
     * 查询会员卡
     */
    List<TClubCard> getClubCards(Map<String, Object> param);

    /**
     * 查询会员卡信息
     */
    TClubCard getClubCardInfo(Map<String, Object> param);

    /**
     * 新增会员卡
     */
    int insertClubCard(TClubCard record);

    /**
     * 更新会员卡信息
     */
    Integer updateClubCardInfo(Map<String, Object> param);
}