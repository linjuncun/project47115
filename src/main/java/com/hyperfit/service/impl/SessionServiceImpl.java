package com.hyperfit.service.impl;

import com.hyperfit.dao.SysConfigMapper;
import com.hyperfit.service.SessionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;


@Service("sessionServiceImpl")
public class SessionServiceImpl implements SessionService {

    @Resource(name = "sysConfigMapperImpl")
    private SysConfigMapper sysConfigMapper;

    @Override
    public String getSessionByKey(Map<String, Object> param) {
        return sysConfigMapper.getSessionByKey(param);
    }

    @Override
    public int addSession(Map<String, Object> param) {
        int num;
        String result = sysConfigMapper.getSessionByKey(param);
        if (StringUtils.isBlank(result)) {
            num = sysConfigMapper.addSession(param);
        } else {
            num = sysConfigMapper.updateSession(param);
        }
        return num;
    }


}
