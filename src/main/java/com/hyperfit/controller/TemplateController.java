package com.hyperfit.controller;

import com.hyperfit.entity.SysFunction;
import com.hyperfit.entity.TTemplate;
import com.hyperfit.entity.TUser;
import com.hyperfit.service.TemplateService;
import com.hyperfit.util.ApiModel;
import com.hyperfit.util.PageEntity;
import com.hyperfit.util.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p> 模版控制器 </p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/12/5 16:39
 */
@Controller
@RequestMapping("/template")
public class TemplateController extends BaseController {

    @Resource(name = "templateServiceImpl")
    private TemplateService templateService;

    /**
     * 查询模版列表
     */
    @RequestMapping("getTemplates.do")
    @ResponseBody
    public ApiModel getTemplates(String courseType) {
        ApiModel apiModel = new ApiModel();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("courseType", courseType);
            List<TTemplate> list = templateService.getTemplates(map);
            apiModel.setCode("0");
            if (list.size() > 0) {
                apiModel.setMsg("查询成功");
            } else {
                apiModel.setMsg("查询结果为空");
            }
            apiModel.setData(list);
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("查询模版列表失败：", e);
        }
        return apiModel;
    }

    /**
     * 查询模版分页列表
     */
    @RequestMapping("getTemplateList.do")
    @ResponseBody
    public ApiModel getTemplateList(Integer draw, Integer pageIndex, Integer pageSize, String courseType, String functionId) {
        ApiModel apiModel = new ApiModel();
        PageEntity param = new PageEntity();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            // 判断是否拥有权限
            List<SysFunction> functions = getFunctions(functionId);
            boolean hasFunction = hasFunction(functions, "template-view");
            if (!hasFunction) {
                apiModel.setCode("1");
                apiModel.setMsg("无查看权限");
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("courseType", courseType);
            param.setMap(map);
            if (draw != null) {
                param.setDraw(draw);
            }
            if (pageIndex != null) {
                param.setPageIndex(pageIndex);
            }
            if (pageSize != null) {
                param.setPageSize(pageSize);
            }
            List<TTemplate> list = templateService.getTemplateList(param);
            PageUtil.objectToPage(param, list);
            apiModel.setCode("0");
            if (list.size() > 0) {
                apiModel.setMsg("查询成功");
            } else {
                apiModel.setMsg("查询结果为空");
            }
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("查询模版列表分页失败：", e);
        }
        apiModel.setData(param);
        return apiModel;
    }

    /**
     * 查询模版详情
     */
    @RequestMapping("getTemplateDetail.do")
    @ResponseBody
    public ApiModel getTemplateDetail(String templateId) {
        ApiModel apiModel = new ApiModel();
        try {
            if (StringUtils.isBlank(templateId)) {
                apiModel.setCode("1");
                apiModel.setMsg("参数错误");
                return apiModel;
            }
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("templateId", templateId);
            TTemplate template = templateService.getTemplateInfo(map);
            apiModel.setData(template);
            apiModel.setCode("0");
            apiModel.setMsg("查询成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("查询模版详情失败：", e);
        }
        return apiModel;
    }

    /**
     * 新增模版
     */
    @RequestMapping("addTemplate.do")
    @ResponseBody
    public ApiModel addTemplate(TTemplate template, String functionId) {
        ApiModel apiModel = new ApiModel();
        try {
            if (StringUtils.isAnyBlank(template.getTemplateName())) {
                apiModel.setCode("1");
                apiModel.setMsg("参数不能为空");
                return apiModel;
            }
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            // 判断是否拥有权限
            List<SysFunction> functions = getFunctions(functionId);
            boolean hasFunction = hasFunction(functions, "template-edit");
            if (!hasFunction) {
                apiModel.setCode("1");
                apiModel.setMsg("无操作权限");
                return apiModel;
            }
            //名称查重
            Map<String, Object> map = new HashMap<>();
            map.put("templateName", template.getTemplateName());
            TTemplate templateChk = templateService.getTemplateInfo(map);
            if (templateChk != null) {
                apiModel.setCode("1");
                apiModel.setMsg("名称重复，请重新输入");
                return apiModel;
            }
            templateService.insertTemplate(template);
            apiModel.setCode("0");
            apiModel.setMsg("操作成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("新增模版异常：" + e);
        }
        return apiModel;
    }

    /**
     * 修改模版
     *
     * @param coachIds 教练id,多个以逗号分隔
     */
    @RequestMapping("updateTemplate.do")
    @ResponseBody
    public ApiModel updateTemplate(TTemplate template, String coachIds, String functionId) {
        ApiModel apiModel = new ApiModel();
        try {
            if (template.getTemplateId() == null) {
                apiModel.setCode("1");
                apiModel.setMsg("参数不能为空");
                return apiModel;
            }
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            // 判断是否拥有权限
            List<SysFunction> functions = getFunctions(functionId);
            boolean hasFunction = hasFunction(functions, "template-edit");
            if (!hasFunction) {
                apiModel.setCode("1");
                apiModel.setMsg("无操作权限");
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            if (template.getStatus() == null) {//删除操作不检查重复
                //名称查重
                map.put("templateName", template.getTemplateName());
                TTemplate templateChk = templateService.getTemplateInfo(map);
                if (templateChk != null && template.getTemplateId() != templateChk.getTemplateId()) {
                    apiModel.setCode("1");
                    apiModel.setMsg("名称重复，请重新输入");
                    return apiModel;
                }
            }
            map.put("record", template);
            templateService.updateTemplateInfo(map);
            apiModel.setCode("0");
            apiModel.setMsg("操作成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("修改模版异常：" + e);
        }
        return apiModel;
    }

}
