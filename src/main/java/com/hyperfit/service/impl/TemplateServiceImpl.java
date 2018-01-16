package com.hyperfit.service.impl;

import com.hyperfit.dao.TTemplateMapper;
import com.hyperfit.entity.TTemplate;
import com.hyperfit.service.TemplateService;
import com.hyperfit.util.PageEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Service("templateServiceImpl")
public class TemplateServiceImpl implements TemplateService {

    @Resource(name = "tTemplateMapperImpl")
    private TTemplateMapper tTemplateMapper;

    @Override
    public List<TTemplate> getTemplateList(PageEntity pageEntity) {
        return tTemplateMapper.getTemplateList(pageEntity);
    }

    @Override
    public List<TTemplate> getTemplates(Map<String, Object> param) {
        return tTemplateMapper.getTemplates(param);
    }

    @Override
    public TTemplate getTemplateInfo(Map<String, Object> param) {
        return tTemplateMapper.getTemplateInfo(param);
    }

    @Override
    public int insertTemplate(TTemplate record) {
        return tTemplateMapper.insertTemplate(record);
    }

    @Override
    public Integer updateTemplateInfo(Map<String, Object> param) {
        return tTemplateMapper.updateTemplateInfo(param);
    }
}
