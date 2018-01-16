package com.hyperfit.service.impl;

import com.hyperfit.dao.SysLocationMapper;
import com.hyperfit.dao.TGymMapper;
import com.hyperfit.entity.SysLocation;
import com.hyperfit.entity.TGym;
import com.hyperfit.entity.TGymCoach;
import com.hyperfit.service.GymService;
import com.hyperfit.util.PageEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("gymServiceImpl")
public class GymServiceImpl implements GymService {

    @Resource(name = "tGymMapperImpl")
    private TGymMapper tGymMapper;

    @Resource(name = "sysLocationMapperImpl")
    private SysLocationMapper sysLocationMapper;

    @Override
    public List<SysLocation> getGymLocationList(Map<String, Object> param) {
        return sysLocationMapper.getGymLocationList(param);
    }

    @Override
    public List<TGym> getGymList(PageEntity pageEntity) {
        return tGymMapper.getGymList(pageEntity);
    }

    @Override
    public List<TGym> getGyms(Map<String, Object> param) {
        return tGymMapper.getGyms(param);
    }

    @Override
    public TGym getGymInfo(Map<String, Object> param) {
        return tGymMapper.getGymInfo(param);
    }

    @Override
    public int insertGym(Map<String, Object> param) {
        String coachIds = (String) param.get("coachIds");
        TGym gym = (TGym) param.get("record");
        int result = tGymMapper.insertGym(gym);
        // 关联健身房教练
        if (StringUtils.isNotBlank(coachIds)) {
            List<TGymCoach> list = new ArrayList<>();
            String[] ids = coachIds.split(",");
            for (String userId : ids) {
                TGymCoach temp = new TGymCoach();
                temp.setGymId(gym.getGymId());
                temp.setUserId(Integer.parseInt(userId));
                list.add(temp);
            }
            tGymMapper.addGymCoaches(list);
        }
        return result;
    }

    @Override
    public Integer updateGymInfo(Map<String, Object> param) {
        int result = tGymMapper.updateGymInfo(param);
        // 更新健身房教练
        String coachIds = (String) param.get("coachIds");
        TGym gym = (TGym) param.get("record");
        // 关联健身房教练
        if (StringUtils.isNotBlank(coachIds)) {
            //先删除
            Map<String, Object> map = new HashMap<>();
            map.put("gymId", gym.getGymId());
            tGymMapper.delGymCoaches(map);
            //再增加
            List<TGymCoach> list = new ArrayList<>();
            String[] ids = coachIds.split(",");
            for (String userId : ids) {
                TGymCoach temp = new TGymCoach();
                temp.setGymId(gym.getGymId());
                temp.setUserId(Integer.parseInt(userId));
                list.add(temp);
            }
            tGymMapper.addGymCoaches(list);
        }
        return result;
    }
}
