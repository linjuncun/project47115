package com.hyperfit.dao;

import com.hyperfit.entity.TBalanceItem;
import com.hyperfit.util.PageEntity;

import java.util.List;

public interface TBalanceItemMapper {

    /**
     * 查询余额明细列表
     */
    List<TBalanceItem> getBalanceItem(PageEntity pageEntity);

    /**
     * 新增余额明细
     */
    int insertBalanceItem(TBalanceItem record);


}