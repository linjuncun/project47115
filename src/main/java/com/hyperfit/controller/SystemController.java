package com.hyperfit.controller;

import com.hyperfit.entity.SysConfig;
import com.hyperfit.entity.SysFunction;
import com.hyperfit.entity.SysLocation;
import com.hyperfit.entity.TUser;
import com.hyperfit.service.SystemService;
import com.hyperfit.util.ApiModel;
import com.hyperfit.util.DateUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p> 系统配置控制器 </p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/11/23 19:50
 */
@Controller
@RequestMapping("/system")
public class SystemController extends BaseController {

    @Resource(name = "systemServiceImpl")
    private SystemService systemService;

    /**
     * 查询系统配置信息
     */
    @RequestMapping("getSysConfig.do")
    @ResponseBody
    public ApiModel getSysConfig(String openid) {
        ApiModel apiModel = new ApiModel();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, openid, user)) {
                return apiModel;
            }
            Map<String, Object> param = new HashMap<>();
            SysConfig config = systemService.getSysConfig(param);
            apiModel.setCode("0");
            apiModel.setData(config);
            apiModel.setMsg("操作成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("查询系统配置异常：" + e);
        }
        return apiModel;
    }

    /**
     * 修改系统配置
     */
    @RequestMapping("updateSysConfig.do")
    @ResponseBody
    public ApiModel updateSysConfig(SysConfig config) {
        ApiModel apiModel = new ApiModel();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            Map<String, Object> param = new HashMap<>();
            param.put("record", config);
            systemService.updateSysConfig(param);
            apiModel.setCode("0");
            apiModel.setMsg("操作成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("修改系统配置异常：" + e);
        }
        return apiModel;
    }

    /**
     * 查询系统功能列表
     */
    @RequestMapping("getSysFunctions.do")
    @ResponseBody
    public ApiModel getSysFunctions(String roleId) {
        ApiModel apiModel = new ApiModel();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("roleId", roleId);
            List<SysFunction> list = systemService.getSysFunctions(map);
            apiModel.setCode("0");
            apiModel.setData(list);
            if (list.size() > 0) {
                apiModel.setMsg("查询成功");
            } else {
                apiModel.setMsg("查询结果为空");
            }
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("查询系统功能列表失败：", e);
        }
        return apiModel;
    }

    /**
     * 查询地区字典
     *
     * @param queryType  查询类型 1：查询所有省份
     * @param provinceId 省份locationId（传此参数则查询该省份下数据）
     * @param cityId     城市locationId（传此参数则查询该城市下数据）
     */
    @RequestMapping("getLocation.do")
    @ResponseBody
    public ApiModel getLocation(String queryType, String provinceId, String cityId) {
        ApiModel apiModel = new ApiModel();
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("queryType", queryType);
            map.put("provinceId", provinceId);
            map.put("cityId", cityId);
            List<SysLocation> list = systemService.getLocation(map);
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
            log.error("查询地区数据异常：", e);
        }
        return apiModel;
    }

    /**
     * 检查系统时间格式
     */
    @RequestMapping("checkSystemTime.do")
    @ResponseBody
    public ApiModel checkSystemTime() {
        ApiModel apiModel = new ApiModel();
        Date now = new Date();
        apiModel.setCode("0");
        apiModel.setMsg("今天的正确返回值是：星期几加1");
        apiModel.setData(DateUtil.dayOfWeekInt(now));
        return apiModel;
    }

    /**
     * 查询用户消费概览
     */
    @RequestMapping("getUserView.do")
    @ResponseBody
    public ApiModel getUserView(String userId) {
        ApiModel apiModel = new ApiModel();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            Map<String, Object> param = new HashMap<>();
            param.put("userId", userId);
            Object data = systemService.getUserView(param);
            apiModel.setCode("0");
            apiModel.setData(data);
            apiModel.setMsg("查询成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("查询用户消费概览异常：" + e);
        }
        return apiModel;
    }

    /**
     * 查询用户预约情况报表
     *
     * @param startDate 查询开始日期
     * @param endDate   查询结束日期
     * @param groupType 分组类型  1：按周  2：按月
     */
    @RequestMapping("getOrderView.do")
    @ResponseBody
    public ApiModel getOrderView(String userId, String startDate, String endDate, String groupType) {
        ApiModel apiModel = new ApiModel();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            Map<String, Object> param = new HashMap<>();
            param.put("userId", userId);
            param.put("startDate", startDate);
            param.put("endDate", endDate);
            param.put("groupType", groupType);
            param.put("orderType", 1);
            param.put("status", 3);
            List<Map<String, Object>> list = systemService.getOrderView(param);
            if ("1".equals(groupType)) {
                for (int i = 0; i < list.size(); i++) {
                    Integer year = (Integer) list.get(i).get("orderYear");
                    Integer week = (Integer) list.get(i).get("orderWeek");
                    String day = DateUtil.getStartDayOfWeekNo(year, week);
                    list.get(i).put("day", day);
                }
            }
            apiModel.setCode("0");
            apiModel.setData(list);
            apiModel.setMsg("查询成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("查询用户预约情况报表异常：" + e);
        }
        return apiModel;
    }

    /**
     * 查询用户上课时间报表
     *
     * @param startDate 查询开始日期
     * @param endDate   查询结束日期
     */
    @RequestMapping("getClassHourView.do")
    @ResponseBody
    public ApiModel getClassHourView(String userId, String startDate, String endDate) {
        ApiModel apiModel = new ApiModel();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            Map<String, Object> param = new HashMap<>();
            param.put("userId", userId);
            param.put("startDate", startDate);
            param.put("endDate", endDate);
            List<Map<String, Object>> list = systemService.getClassHourView(param);
            apiModel.setCode("0");
            apiModel.setData(list);
            apiModel.setMsg("查询成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("查询用户上课时间报表异常：" + e);
        }
        return apiModel;
    }

    /**
     * 查询总收入
     */
    @RequestMapping("getTotalView.do")
    @ResponseBody
    public ApiModel getTotalView(String startDate, String endDate) {
        ApiModel apiModel = new ApiModel();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            Map<String, Object> param = new HashMap<>();
            param.put("startDate", startDate);
            param.put("endDate", endDate);
            Object data = systemService.getTotalView(param);
            apiModel.setCode("0");
            apiModel.setData(data);
            apiModel.setMsg("查询成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("查询总收入异常：" + e);
        }
        return apiModel;
    }

    /**
     * 查询会员卡销售收入报表
     *
     * @param startDate 查询开始日期
     * @param endDate   查询结束日期
     */
    @RequestMapping("getCardView.do")
    @ResponseBody
    public ApiModel getCardView(String startDate, String endDate) {
        ApiModel apiModel = new ApiModel();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            Map<String, Object> param = new HashMap<>();
            param.put("startDate", startDate);
            param.put("endDate", endDate);
            List<Map<String, Object>> list = systemService.getCardView(param);
            apiModel.setCode("0");
            apiModel.setData(list);
            apiModel.setMsg("查询成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("查询会员卡销售收入报表异常：" + e);
        }
        return apiModel;
    }

    /**
     * 查询课程总预览
     */
    @RequestMapping("getCourseTotal.do")
    @ResponseBody
    public ApiModel getCourseTotal() {
        ApiModel apiModel = new ApiModel();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            Map<String, Object> param = new HashMap<>();
            Object data = systemService.getCourseTotal(param);
            apiModel.setCode("0");
            apiModel.setData(data);
            apiModel.setMsg("查询成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("查询课程总预览异常：" + e);
        }
        return apiModel;
    }

    /**
     * 查询课程报表
     *
     * @param startDate 查询开始日期
     * @param endDate   查询结束日期
     */
    @RequestMapping("getCourseView.do")
    @ResponseBody
    public ApiModel getCourseView(String startDate, String endDate) {
        ApiModel apiModel = new ApiModel();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            Map<String, Object> param = new HashMap<>();
            param.put("startDate", startDate);
            param.put("endDate", endDate);
            List<Map<String, Object>> courseNum = systemService.getCourseNumView(param);
            List<Map<String, Object>> courseOrder = systemService.getCourseOrderView(param);
            Map<String, Object> data = new HashMap<>();
            data.put("courseNum", courseNum);
            data.put("courseOrder", courseOrder);
            apiModel.setCode("0");
            apiModel.setData(data);
            apiModel.setMsg("查询成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("查询课程数量报表异常：" + e);
        }
        return apiModel;
    }

    /**
     * 查询会员转化报表
     */
    @RequestMapping("getUserCardView.do")
    @ResponseBody
    public ApiModel getUserCardView(String startDate, String endDate) {
        ApiModel apiModel = new ApiModel();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            Map<String, Object> param = new HashMap<>();
            param.put("startDate", startDate);
            param.put("endDate", endDate);
            Object data = systemService.getUserCardView(param);
            apiModel.setCode("0");
            apiModel.setData(data);
            apiModel.setMsg("查询成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("查询会员转化报表异常：" + e);
        }
        return apiModel;
    }

    /**
     * 查询当前会员卡状态报表
     */
    @RequestMapping("getCardStatusView.do")
    @ResponseBody
    public ApiModel getCardStatusView() {
        ApiModel apiModel = new ApiModel();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            Map<String, Object> param = new HashMap<>();
            Object data = systemService.getCardStatusView(param);
            apiModel.setCode("0");
            apiModel.setData(data);
            apiModel.setMsg("查询成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("查询当前会员卡状态报表异常：" + e);
        }
        return apiModel;
    }
}