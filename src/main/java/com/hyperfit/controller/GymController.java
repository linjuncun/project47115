package com.hyperfit.controller;

import com.hyperfit.entity.SysFunction;
import com.hyperfit.entity.SysLocation;
import com.hyperfit.entity.TGym;
import com.hyperfit.entity.TUser;
import com.hyperfit.service.GymService;
import com.hyperfit.util.ApiModel;
import com.hyperfit.util.ExcelUtil;
import com.hyperfit.util.PageEntity;
import com.hyperfit.util.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p> 健身房控制器 </p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/11/18 15:18
 */
@Controller
@RequestMapping("/gym")
public class GymController extends BaseController {

    @Resource(name = "gymServiceImpl")
    private GymService gymService;

    /**
     * 查询健身房分页列表
     */
    @RequestMapping("getGymList.do")
    @ResponseBody
    public ApiModel getGymList(Integer draw, Integer pageIndex, Integer pageSize, String openid, String locationId, String gymType,
                               String conditions, String provinceId, String cityId, String districtId) {
        ApiModel apiModel = new ApiModel();
        PageEntity param = new PageEntity();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, openid, user)) {
                return apiModel;
            }
            if (StringUtils.isBlank(openid)) {//后台查询
                // 判断是否拥有权限
                List<SysFunction> functions = getFunctions("7");
                boolean hasFunction = hasFunction(functions, "view");
                if (!hasFunction) {
                    apiModel.setCode("1");
                    apiModel.setMsg("无查看权限");
                    return apiModel;
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put("locationId", locationId);
            map.put("gymType", gymType);
            map.put("conditions", conditions);
            map.put("provinceId", provinceId);
            map.put("cityId", cityId);
            map.put("districtId", districtId);
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
            List<TGym> list = gymService.getGymList(param);
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
            log.error("查询健身房列表分页失败：", e);
        }
        apiModel.setData(param);
        return apiModel;
    }

    /**
     * 查询健身房列表
     */
    @RequestMapping("getGyms.do")
    @ResponseBody
    public ApiModel getGyms() {
        ApiModel apiModel = new ApiModel();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            List<TGym> list = gymService.getGyms(map);
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
            log.error("查询健身房列表失败：", e);
        }
        return apiModel;
    }

    /**
     * 查询健身房分布地区列表
     */
    @RequestMapping("getGymLocationList.do")
    @ResponseBody
    public ApiModel getGymLocationList(String openid) {
        ApiModel apiModel = new ApiModel();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, openid, user)) {
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            List<SysLocation> list = gymService.getGymLocationList(map);
            apiModel.setData(list);
            apiModel.setCode("0");
            if (list.size() > 0) {
                apiModel.setMsg("查询成功");
            } else {
                apiModel.setMsg("查询结果为空");
            }
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("查询健身房分布地区列表失败：", e);
        }
        return apiModel;
    }


    /**
     * 查询健身房详情
     */
    @RequestMapping("getGymDetail.do")
    @ResponseBody
    public ApiModel getGymDetail(String openid, String gymId) {
        ApiModel apiModel = new ApiModel();
        try {
            if (StringUtils.isBlank(gymId)) {
                apiModel.setCode("1");
                apiModel.setMsg("参数错误");
                return apiModel;
            }
            TUser user = new TUser();
            if (!checkUser(apiModel, openid, user)) {
                return apiModel;
            }
            if (StringUtils.isBlank(openid)) {//后台查询
                // 判断是否拥有权限
                List<SysFunction> functions = getFunctions("7");
                boolean hasFunction = hasFunction(functions, "view");
                if (!hasFunction) {
                    apiModel.setCode("1");
                    apiModel.setMsg("无查看权限");
                    return apiModel;
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put("gymId", gymId);
            TGym gym = gymService.getGymInfo(map);
            apiModel.setData(gym);
            apiModel.setCode("0");
            apiModel.setMsg("查询成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("查询健身房详情失败：", e);
        }
        return apiModel;
    }

    /**
     * 新增健身房
     *
     * @param coachIds 教练id,多个以逗号分隔
     */
    @RequestMapping("addGym.do")
    @ResponseBody
    public ApiModel addGym(TGym gym, String coachIds) {
        ApiModel apiModel = new ApiModel();
        try {
            if (StringUtils.isAnyBlank(gym.getGymName(), gym.getAddress(), gym.getIntro(), gym.getImages()) || gym.getLocationId() == null
                    || gym.getWeight() == null || gym.getSysUserId() == null || gym.getGymType() == null) {
                apiModel.setCode("1");
                apiModel.setMsg("参数不能为空");
                return apiModel;
            }
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            // 判断是否拥有权限
            List<SysFunction> functions = getFunctions("7");
            boolean hasFunction = hasFunction(functions, "add");
            if (!hasFunction) {
                apiModel.setCode("1");
                apiModel.setMsg("无操作权限");
                return apiModel;
            }
            //名称查重
            Map<String, Object> map = new HashMap<>();
            map.put("gymName", gym.getGymName());
            TGym gymChk = gymService.getGymInfo(map);
            if (gymChk != null) {
                apiModel.setCode("1");
                apiModel.setMsg("名称重复，请重新输入");
                return apiModel;
            }
            map.put("coachIds", coachIds);
            map.put("record", gym);
            gymService.insertGym(map);
            apiModel.setCode("0");
            apiModel.setMsg("操作成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("新增健身房异常：" + e);
        }
        return apiModel;
    }

    /**
     * 修改健身房
     *
     * @param coachIds 教练id,多个以逗号分隔
     */
    @RequestMapping("updateGym.do")
    @ResponseBody
    public ApiModel updateGym(TGym gym, String coachIds) {
        ApiModel apiModel = new ApiModel();
        try {
            if (gym.getGymId() == null) {
                apiModel.setCode("1");
                apiModel.setMsg("参数不能为空");
                return apiModel;
            }
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            // 判断是否拥有权限
            String code = "edit";
            if (gym.getStatus() != null && gym.getStatus() == 2) {
                code = "del";
            }
            List<SysFunction> functions = getFunctions("7");
            boolean hasFunction = hasFunction(functions, code);
            if (!hasFunction) {
                apiModel.setCode("1");
                apiModel.setMsg("无操作权限");
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            if (gym.getStatus() == null) {//删除操作不用查重
                //名称查重
                map.put("gymName", gym.getGymName());
                TGym gymChk = gymService.getGymInfo(map);
                if (gymChk != null && gym.getGymId() != gymChk.getGymId()) {
                    apiModel.setCode("1");
                    apiModel.setMsg("名称重复，请重新输入");
                    return apiModel;
                }
            }
            map.put("coachIds", coachIds);
            map.put("record", gym);
            gymService.updateGymInfo(map);
            apiModel.setCode("0");
            apiModel.setMsg("操作成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("修改健身房异常：" + e);
        }
        return apiModel;
    }

    /**
     * 导出健身房excel文件
     *
     * @param gymIds 要导出的健身房id，多个以英文逗号","分隔
     */
    @RequestMapping("/exportGym.do")
    public void exportGym(HttpServletResponse response, String gymIds) {
        try (PrintWriter out = response.getWriter()) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            ApiModel apiModel = new ApiModel();
            TUser user = new TUser();
            String msg;//返回消息
            if (!checkUser(apiModel, "", user)) {
                msg = "未登录或登录失效";
                out.print(msg);
                return;
            }
            if (StringUtils.isBlank(gymIds)) {
                msg = "参数不能为空";
                out.print(msg);
                return;
            }
            // 判断是否拥有权限
            List<SysFunction> functions = getFunctions("7");
            boolean hasFunction = hasFunction(functions, "export");
            if (!hasFunction) {
                msg = "无导出数据权限";
                out.print(msg);
                return;
            }

            // excel表头
            LinkedHashMap<String, String> fieldMap = new LinkedHashMap<>();
            fieldMap.put("gymName", "名称");
            fieldMap.put("address", "地址");
            fieldMap.put("weight", "权重");
            fieldMap.put("truename", "店长");
            fieldMap.put("gymTypeStr", "类型");
            fieldMap.put("intro", "文字介绍");
            fieldMap.put("coachFullTime", "全职教练");
            fieldMap.put("coachPartTime", "兼职教练");
            //TODO 确认是否要导出图片
            // excel的sheetName
            String sheetName = "健身房列表";
            // excel要导出的数据
            Map<String, Object> map = new HashMap<>();
            String[] ids = gymIds.split(",");
            map.put("ids", ids);
            List<TGym> list = gymService.getGyms(map);
            if (list.size() == 0) {
                TGym empty = new TGym();
                list.add(empty);
            } else {
                //自定义数据
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getGymType() == 1) {
                        list.get(i).setGymTypeStr("常规店");
                    } else if (list.get(i).getGymType() == 2) {
                        list.get(i).setGymTypeStr("共享店");
                    }
                    list.get(i).setAddress(list.get(i).getProvince() + list.get(i).getCity() + list.get(i).getDistrict() + list.get(i).getAddress());
                    List<TUser> coachList = list.get(i).getCoachList();
                    String coachFullTime = "";
                    String coachPartTime = "";
                    for (int j = 0; j < coachList.size(); j++) {
                        if (coachList.get(j).getCoachType() == 1) {//全职
                            coachFullTime += coachList.get(j).getName() + "、";
                        } else {
                            coachPartTime += coachList.get(j).getName() + "、";
                        }
                    }
                    if (StringUtils.isNotBlank(coachFullTime)) {
                        coachFullTime = coachFullTime.substring(0, coachFullTime.length() - 1);
                    }
                    if (StringUtils.isNotBlank(coachPartTime)) {
                        coachPartTime = coachPartTime.substring(0, coachPartTime.length() - 1);
                    }
                    list.get(i).setCoachFullTime(coachFullTime);
                    list.get(i).setCoachPartTime(coachPartTime);
                }
            }
            // 导出
            String fileName = "";//excel文件名
            ExcelUtil.listToExcel(list, fieldMap, sheetName, 65535, response, fileName);
        } catch (Exception e) {
            log.error("导出健身房数据异常：" + e);
        }
    }


}
