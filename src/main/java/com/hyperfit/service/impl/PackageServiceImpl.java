package com.hyperfit.service.impl;

import com.hyperfit.dao.TClubCardMapper;
import com.hyperfit.dao.TPackageMapper;
import com.hyperfit.dao.TReturnCashConditionMapper;
import com.hyperfit.entity.TClubCard;
import com.hyperfit.entity.TPackage;
import com.hyperfit.entity.TReturnCashCondition;
import com.hyperfit.service.PackageService;
import com.hyperfit.util.PageEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("packageServiceImpl")
public class PackageServiceImpl implements PackageService {

    @Resource(name = "tPackageMapperImpl")
    private TPackageMapper tPackageMapper;

    @Resource(name = "tClubCardMapperImpl")
    private TClubCardMapper tClubCardMapper;

    @Resource(name = "tReturnCashConditionMapperImpl")
    private TReturnCashConditionMapper tReturnCashConditionMapper;

    @Override
    public List<TPackage> getPackageList(PageEntity pageEntity) {
        return tPackageMapper.getPackageList(pageEntity);
    }

    @Override
    public TPackage getPackageInfo(Map<String, Object> param) {
        return tPackageMapper.getPackageInfo(param);
    }

    @Override
    public List<Object> getCardNumber(Map<String, Object> param) {
        return tPackageMapper.getCardNumber(param);
    }

    @Override
    public int insertPackage(TPackage record) {
        int result = tPackageMapper.insertPackage(record);
        if (result > 0 && record.getReturnCashCondition() != null) {
            List<TReturnCashCondition> list = record.getReturnCashCondition();
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setPackageId(record.getPackageId());
                tReturnCashConditionMapper.addCondition(list.get(i));
            }
        }
        return result;
    }

    @Override
    public Integer updatePackageInfo(Map<String, Object> param) {
        int result = tPackageMapper.updatePackageInfo(param);
        TPackage record = (TPackage) param.get("record");
        if (result > 0 && record.getReturnCashCondition() != null) {
            //先删除
            Map<String, Object> map = new HashMap<>();
            map.put("packageId", record.getPackageId());
            tReturnCashConditionMapper.delCondition(map);
            //再添加
            List<TReturnCashCondition> list = record.getReturnCashCondition();
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setPackageId(record.getPackageId());
                tReturnCashConditionMapper.addCondition(list.get(i));
            }
        }
        return result;
    }

    @Override
    public List<TClubCard> getPackageCardList(PageEntity pageEntity) {
        return tClubCardMapper.getClubCardPageList(pageEntity);
    }

    @Override
    public int addClubCard(TClubCard record) {
        return tClubCardMapper.insertClubCard(record);
    }

    @Override
    public Integer updateClubCard(Map<String, Object> param) {
        return tClubCardMapper.updateClubCardInfo(param);
    }
}
