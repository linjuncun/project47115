package com.hyperfit.service;

import com.hyperfit.entity.TTemplate;
import com.hyperfit.util.PageEntity;

import java.util.List;
import java.util.Map;

/**
 * <p> 模版service</p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/12/5 16:25
 */
public interface TemplateService {

    /**
     * 查询模版分页列表
     */
    List<TTemplate> getTemplateList(PageEntity pageEntity);

    /**
     * 查询模版列表
     */
    List<TTemplate> getTemplates(Map<String, Object> param);

    /**
     * 查询模版信息
     */
    TTemplate getTemplateInfo(Map<String, Object> param);

    /**
     * 新增模版
     */
    int insertTemplate(TTemplate record);

    /**
     * 更新模版信息
     */
    Integer updateTemplateInfo(Map<String, Object> param);

}
