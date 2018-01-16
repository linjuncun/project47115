package com.hyperfit.service;

import com.hyperfit.entity.TClubCard;
import com.hyperfit.entity.TPackage;
import com.hyperfit.util.PageEntity;

import java.util.List;
import java.util.Map;

/**
 * <p> 套餐service</p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/11/23 20:36
 */
public interface PackageService {

    /**
     * 查询套餐列表
     */
    List<TPackage> getPackageList(PageEntity pageEntity);

    /**
     * 查询套餐信息
     */
    TPackage getPackageInfo(Map<String, Object> param);

    /**
     * 查询会员卡办卡数量
     */
    List<Object> getCardNumber(Map<String, Object> param);

    /**
     * 新增套餐
     */
    int insertPackage(TPackage record);

    /**
     * 更新套餐信息
     */
    Integer updatePackageInfo(Map<String, Object> param);

    /**
     * 查询套餐列表
     */
    List<TClubCard> getPackageCardList(PageEntity pageEntity);

    /**
     * 新增会员卡
     */
    int addClubCard(TClubCard record);

    /**
     * 编辑会员卡
     */
    Integer updateClubCard(Map<String, Object> param);


}
