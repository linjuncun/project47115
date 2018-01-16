package com.hyperfit.service.impl;

import com.hyperfit.dao.TOrderMapper;
import com.hyperfit.entity.TOrder;
import com.hyperfit.service.OrderService;
import com.hyperfit.util.PageEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Service("orderServiceImpl")
public class OrderServiceImpl implements OrderService {

    @Resource(name = "tOrderMapperImpl")
    private TOrderMapper tOrderMapper;

    @Override
    public List<TOrder> getOrderList(PageEntity pageEntity) {
        return tOrderMapper.getOrderList(pageEntity);
    }

    @Override
    public TOrder getOrderInfo(Map<String, Object> param) {
        return tOrderMapper.getOrderInfo(param);
    }

    @Override
    public int insertOrder(TOrder record) {
        return tOrderMapper.insertOrder(record);
    }

    @Override
    public Integer updateOrderInfo(Map<String, Object> param) {
        return tOrderMapper.updateOrderInfo(param);
    }
}
