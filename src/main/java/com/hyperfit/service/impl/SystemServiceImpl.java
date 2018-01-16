package com.hyperfit.service.impl;

import com.hyperfit.dao.*;
import com.hyperfit.entity.*;
import com.hyperfit.service.SystemService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("systemServiceImpl")
public class SystemServiceImpl implements SystemService {

    @Resource(name = "sysConfigMapperImpl")
    private SysConfigMapper sysConfigMapper;

    @Resource(name = "sysFunctionMapperImpl")
    private SysFunctionMapper sysFunctionMapper;

    @Resource(name = "sysLocationMapperImpl")
    private SysLocationMapper sysLocationMapper;

    @Resource(name = "tClubCardMapperImpl")
    private TClubCardMapper tClubCardMapper;

    @Resource(name = "tOrderMapperImpl")
    private TOrderMapper tOrderMapper;

    @Override
    public SysConfig getSysConfig(Map<String, Object> param) {
        return sysConfigMapper.getSysConfig(param);
    }

    @Override
    public int updateSysConfig(Map<String, Object> param) {
        return sysConfigMapper.updateSysConfig(param);
    }

    @Override
    public List<SysFunction> getSysFunctions(Map<String, Object> param) {
        return sysFunctionMapper.getSysFunctions(param);
    }

    @Override
    public List<SysLocation> getLocation(Map<String, Object> param) {
        return sysLocationMapper.getLocation(param);
    }

    @Override
    public Object getUserView(Map<String, Object> param) {
        return sysConfigMapper.getUserView(param);
    }

    @Override
    public List<Map<String, Object>> getOrderView(Map<String, Object> param) {
        return sysConfigMapper.getOrderView(param);
    }

    @Override
    public List<Map<String, Object>> getClassHourView(Map<String, Object> param) {
        return sysConfigMapper.getClassHourView(param);
    }

    @Override
    public Object getTotalView(Map<String, Object> param) {
        return sysConfigMapper.getTotalView(param);
    }

    @Override
    public List<Map<String, Object>> getCardView(Map<String, Object> param) {
        return sysConfigMapper.getCardView(param);
    }

    @Override
    public Object getCourseTotal(Map<String, Object> param) {
        return sysConfigMapper.getCourseTotal(param);
    }

    @Override
    public List<Map<String, Object>> getCourseNumView(Map<String, Object> param) {
        return sysConfigMapper.getCourseNumView(param);
    }

    @Override
    public List<Map<String, Object>> getCourseOrderView(Map<String, Object> param) {
        return sysConfigMapper.getCourseOrderView(param);
    }

    @Override
    public Object getUserCardView(Map<String, Object> param) {
        return sysConfigMapper.getUserCardView(param);
    }

    @Override
    public Object getCardStatusView(Map<String, Object> param) {
        return sysConfigMapper.getCardStatusView(param);
    }

    @Override
    public void doCardCheck() {
        Map<String, Object> map = new HashMap<>();
        map.put("queryType", 2);//查询已过期
        List<TClubCard> list = tClubCardMapper.getClubCards(map);
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).getBalance() > 0){
                TClubCard record = new TClubCard();//更新数据实体类
                record.setCardId(list.get(i).getCardId());
                record.setBalance(0);
                Map<String, Object> cardMap = new HashMap<>();
                cardMap.put("record", record);
                tClubCardMapper.updateClubCardInfo(cardMap);

                //插入余额过期记录
                TOrder orderRecord = new TOrder();
                orderRecord.setOrderType(3);//订单类型 1：购买课程  2：购买会员卡套餐  3：会员卡余额过期
                orderRecord.setOpenid(list.get(i).getOpenid());
                orderRecord.setUserId(list.get(i).getUserId());
                orderRecord.setAmount(list.get(i).getBalance());
                orderRecord.setCardId(list.get(i).getCardId());
                orderRecord.setStatus(3);//订单状态 1：待付款 2：购买中 3：已完成 4：申请退款 5：已退款 6：退款异常
                tOrderMapper.insertOrder(orderRecord);
            }
        }
    }
}
