package com.hyperfit.controller;

import com.hyperfit.entity.SysFunction;
import com.hyperfit.entity.TCourse;
import com.hyperfit.entity.TCourseAction;
import com.hyperfit.entity.TUser;
import com.hyperfit.service.CourseService;
import com.hyperfit.util.*;
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
 * <p> 课程控制器 </p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/11/20 20:04
 */
@Controller
@RequestMapping("/course")
public class CourseController extends BaseController {

    @Resource(name = "courseServiceImpl")
    private CourseService courseService;

    /**
     * 微信端-查询课程列表
     */
    @RequestMapping("getCourseList.do")
    @ResponseBody
    public ApiModel getCourseList(Integer draw, Integer pageIndex, Integer pageSize, String openid, String queryDate, String gymId, String courseType, String userId,
                                  String conditions, String templateId, String startDate, String endDate, String queryStatus, String functionId,
                                  String type, String queryType) {
        ApiModel apiModel = new ApiModel();
        PageEntity param = new PageEntity();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, openid, user)) {
                return apiModel;
            }
            if (StringUtils.isBlank(openid)) {//后台查询
                // 判断是否拥有权限
                List<SysFunction> functions = getFunctions(functionId);
                boolean hasFunction = hasFunction(functions, "view");
                if (!hasFunction) {
                    apiModel.setCode("1");
                    apiModel.setMsg("无查看权限");
                    return apiModel;
                }
            } else {//前端查询
                //验证是否绑定手机号
                if (StringUtils.isBlank(user.getPhone())) {
                    apiModel.setCode("-3");
                    apiModel.setMsg("未绑定手机号");
                    return apiModel;
                }
            }

            Map<String, Object> map = new HashMap<>();
            map.put("queryDate", queryDate);
            map.put("gymId", gymId);
            map.put("courseType", courseType);
            map.put("userId", userId);
            map.put("type", type);
            if (StringUtils.isNotBlank(openid)) {//前端查询
                map.put("status", 1);//课程状态 1：正常 2：关闭 3：删除
                map.put("orderType",1);//排序方式 1：微信端查询，按开课时间正序排列
            } else {
                map.put("queryStatus", queryStatus);//后台查询状态  1：未开课 2：开课中  3：已关闭  4：已完结
                map.put("orderType",2);//排序方式 2：后台查询，按开课时间倒序+课程首字母
            }
            map.put("conditions", conditions);
            map.put("templateId", templateId);
            map.put("startDate", startDate);
            map.put("queryType", queryType);//查询类型 1:查询已开始预约的课程 2：正在进行活动课程  3：已完成活动课程
            map.put("endDate", endDate);
            if (user.getUserId() != null && !(user.getUserId() + "").equals(userId)) {//不是教练端或后台查询
                map.put("queryType", 1);//查询已开始预约的课程
            }
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
            List<TCourse> list = courseService.getCourseList(param);
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
            log.error("查询课程列表失败：", e);
        }
        apiModel.setData(param);
        return apiModel;
    }

    /**
     * 微信端-查询课程详情
     */
    @RequestMapping("getCourseDetail.do")
    @ResponseBody
    public ApiModel getCourseDetail(String openid, String courseId, String functionId) {
        ApiModel apiModel = new ApiModel();
        try {
            if (StringUtils.isAnyBlank(courseId)) {
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
                List<SysFunction> functions = getFunctions(functionId);
                boolean hasFunction = hasFunction(functions, "view");
                if (!hasFunction) {
                    apiModel.setCode("1");
                    apiModel.setMsg("无查看权限");
                    return apiModel;
                }
            } else {//前端查询
                //验证是否绑定手机号
                if (StringUtils.isBlank(user.getPhone())) {
                    apiModel.setCode("-3");
                    apiModel.setMsg("未绑定手机号");
                    return apiModel;
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put("queryUserId", user.getUserId());
            map.put("courseId", courseId);
            TCourse course = courseService.getCourseInfo(map);

            apiModel.setData(course);
            apiModel.setCode("0");
            apiModel.setMsg("查询成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("查询课程详情失败：", e);
        }
        return apiModel;
    }

    /**
     * 微信端-查询活动课程
     */
    @RequestMapping("getActivityCourse.do")
    @ResponseBody
    public ApiModel getActivityCourse(String openid) {
        ApiModel apiModel = new ApiModel();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, openid, user)) {
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("queryUserId", user.getUserId());
            map.put("saleStatus", 1);//活动销售状态 1：在售 2：下线
            map.put("type", 2);//类型 1：普通课程 2：活动课程
            TCourse course = courseService.getCourseInfo(map);

            apiModel.setData(course);
            apiModel.setCode("0");
            apiModel.setMsg("查询成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("查询活动课程失败：", e);
        }
        return apiModel;
    }

    /**
     * 查询用户课程列表
     */
    @RequestMapping("getUserCourse.do")
    @ResponseBody
    public ApiModel getUserCourse(Integer draw, Integer pageIndex, Integer pageSize, String openid, String queryType, String queryUserId,
                                  String courseType, String startDate, String endDate, String gymId, String coachId, String status) {
        ApiModel apiModel = new ApiModel();
        PageEntity param = new PageEntity();
        try {
            TUser user = new TUser();
            if (StringUtils.isBlank(queryUserId)) {
                apiModel.setCode("1");
                apiModel.setMsg("参数错误");
                return apiModel;
            }
            if (!checkUser(apiModel, openid, user)) {
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            if (StringUtils.isNotBlank(openid)) {//微信端请求
                //验证是否绑定手机号
                if (StringUtils.isBlank(user.getPhone())) {
                    apiModel.setCode("-3");
                    apiModel.setMsg("未绑定手机号");
                    return apiModel;
                }
            }
            map.put("userId", queryUserId);
            map.put("courseType", courseType);
            map.put("startDate", startDate);
            map.put("endDate", endDate);
            map.put("gymId", gymId);
            map.put("coachId", coachId);
            map.put("status", status);
            map.put("orderType", 1);//查询课程订单
            map.put("queryType", queryType);//查询类型 1：未上课  2：已上课  3：已退课 4:已预约（包含未上和已上课）
            param.setMap(map);
            if (pageIndex != null) {
                param.setPageIndex(pageIndex);
            }
            if (pageSize != null) {
                param.setPageSize(pageSize);
            }
            if (draw != null) {
                param.setDraw(draw);
            }
            List<TCourse> list = courseService.getUserCourse(param);
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
            log.error("查询课程列表失败：", e);
        }
        apiModel.setData(param);
        return apiModel;
    }

    /**
     * 查询课程预约用户列表
     */
    @RequestMapping("getCourseUser.do")
    @ResponseBody
    public ApiModel getCourseUser(Integer draw, Integer pageIndex, Integer pageSize, String openid, String courseId) {
        ApiModel apiModel = new ApiModel();
        PageEntity param = new PageEntity();
        try {
            TUser user = new TUser();
            if (StringUtils.isBlank(courseId)) {
                apiModel.setCode("1");
                apiModel.setMsg("参数错误");
                return apiModel;
            }
            if (!checkUser(apiModel, openid, user)) {
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            if (StringUtils.isNotBlank(openid)) {//微信端请求
                //验证是否绑定手机号
                if (StringUtils.isBlank(user.getPhone())) {
                    apiModel.setCode("-3");
                    apiModel.setMsg("未绑定手机号");
                    return apiModel;
                }
                map.put("status", 3);//订单状态 1：待付款 2：购买中 3：已完成 4：申请退款 5：已退款 6：退款异常
            } else {
                //后台查询
                map.put("status", "3,5");
            }
            map.put("courseId", courseId);
            param.setMap(map);
            if (pageIndex != null) {
                param.setPageIndex(pageIndex);
            }
            if (pageSize != null) {
                param.setPageSize(pageSize);
            }
            if (draw != null) {
                param.setDraw(draw);
            }
            List<TUser> list = courseService.getCourseUser(param);
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
            log.error("查询课程预约用户列表失败：", e);
        }
        apiModel.setData(param);
        return apiModel;
    }

    /**
     * 教练端-填写课程内容
     */
    @RequestMapping("editCourseInfo.do")
    @ResponseBody
    public ApiModel editCourseInfo(String openid, TCourse course, String actions, String delActiconIds) {
        ApiModel apiModel = new ApiModel();
        try {
            if (course.getCourseId() == null) {
                apiModel.setCode("1");
                apiModel.setMsg("参数错误");
                return apiModel;
            }
            TUser user = new TUser();
            if (!checkUser(apiModel, openid, user)) {
                return apiModel;
            }
            //验证是否绑定手机号
            if (StringUtils.isBlank(user.getPhone())) {
                apiModel.setCode("-3");
                apiModel.setMsg("未绑定手机号");
                return apiModel;
            }
            Map<String, Object> param = new HashMap<>();
            param.put("record", course);
            param.put("actions", actions);
            param.put("delActiconIds", delActiconIds);
            courseService.updateCourseInfo(param);
            apiModel.setCode("0");
            apiModel.setMsg("操作成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("填写课程内容异常：" + e);
        }
        return apiModel;
    }

    /**
     * 微信端-查询课程动作列表
     */
    @RequestMapping("getCourseActions.do")
    @ResponseBody
    public ApiModel getCourseActions(String openid, String courseId) {
        ApiModel apiModel = new ApiModel();
        try {
            if (StringUtils.isAnyBlank(courseId)) {
                apiModel.setCode("1");
                apiModel.setMsg("参数错误");
                return apiModel;
            }
            TUser user = new TUser();
            if (!checkUser(apiModel, openid, user)) {
                return apiModel;
            }
            if (StringUtils.isNotBlank(openid)) {//前端查询
                //验证是否绑定手机号
                if (StringUtils.isBlank(user.getPhone())) {
                    apiModel.setCode("-3");
                    apiModel.setMsg("未绑定手机号");
                    return apiModel;
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put("courseId", courseId);
            List<TCourseAction> actions = courseService.getCourseActions(map);
            apiModel.setData(actions);
            apiModel.setCode("0");
            apiModel.setMsg("查询成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("查询课程动作列表失败：", e);
        }
        return apiModel;
    }

    /**
     * 新增课程
     */
    @RequestMapping("addCourse.do")
    @ResponseBody
    public ApiModel addCourse(TCourse course, String startDate, String endDate, String periods, String functionId) {
        ApiModel apiModel = new ApiModel();
        try {
            if ("58".equals(functionId)) {//新增活动课程
                if (StringUtils.isAnyBlank(course.getCourseName(), course.getSolgan(), course.getGymIds(), course.getIntro(), course.getNotes()) || course.getMaxPeople() == null
                        || course.getImgs() == null || course.getUserId() == null || course.getPrice() == null || course.getClassHour() == null) {
                    apiModel.setCode("1");
                    apiModel.setMsg("参数不能为空");
                    return apiModel;
                }
            } else {
                if (StringUtils.isAnyBlank(course.getCourseName(), course.getCourseLabel(), course.getIntro(), course.getNotes(), startDate, endDate, periods) || course.getTemplateId() == null
                        || course.getGymId() == null || course.getUserId() == null || course.getPrice() == null) {
                    apiModel.setCode("1");
                    apiModel.setMsg("参数不能为空");
                    return apiModel;
                }
            }
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            // 判断是否拥有权限
            List<SysFunction> functions = getFunctions(functionId);
            boolean hasFunction = hasFunction(functions, "add");
            if (!hasFunction) {
                apiModel.setCode("1");
                apiModel.setMsg("无操作权限");
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("course", course);
            map.put("startDate", startDate);
            map.put("endDate", endDate);
            map.put("periods", periods);
            if ("58".equals(functionId)) {//新增活动课程
                courseService.insertCourse(course);
            } else {
                //新增课程
                courseService.insertCourseBatch(map);
            }
            apiModel.setCode("0");
            apiModel.setMsg("操作成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("新增课程异常：" + e);
        }
        return apiModel;
    }

    /**
     * 修改课程
     */
    @RequestMapping("updateCourse.do")
    @ResponseBody
    public ApiModel updateCourse(TCourse course, String functionId) {
        ApiModel apiModel = new ApiModel();
        try {
            if (course.getCourseId() == null) {
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
            if (course.getStatus() != null) {
                if (course.getStatus() == 2) {
                    code = "off";
                } else if (course.getStatus() == 3) {
                    code = "del";
                }
            }
            if (course.getSaleStatus() != null) {
                code = "stop";
            }
            List<SysFunction> functions = getFunctions(functionId);
            boolean hasFunction = hasFunction(functions, code);
            if (!hasFunction) {
                apiModel.setCode("1");
                apiModel.setMsg("无操作权限");
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("record", course);
            courseService.updateCourseInfo(map);
            apiModel.setCode("0");
            apiModel.setMsg("操作成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("修改课程异常：" + e);
        }
        return apiModel;
    }

    /**
     * 导出课程excel文件
     *
     * @param courseIds 要导出的课程id，多个以英文逗号","分隔
     */
    @RequestMapping("/exportCourse.do")
    public void exportCourse(HttpServletResponse response, String courseIds, String functionId) {
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
            if (StringUtils.isBlank(courseIds)) {
                msg = "参数不能为空";
                out.print(msg);
                return;
            }
            // 判断是否拥有权限
            List<SysFunction> functions = getFunctions(functionId);
            boolean hasFunction = hasFunction(functions, "export");
            if (!hasFunction) {
                msg = "无导出数据权限";
                out.print(msg);
                return;
            }

            // excel表头
            LinkedHashMap<String, String> fieldMap = new LinkedHashMap<>();
            fieldMap.put("courseName", "名称");
            fieldMap.put("gymName", "所在健身房");
            fieldMap.put("name", "教练");
            fieldMap.put("courseTypeStr", "类型");
            fieldMap.put("classHourStr", "上课时间");
            fieldMap.put("maxNum", "上课人数上限");
            fieldMap.put("minNum", "最少开课人数");
            fieldMap.put("courseLabel", "标签");
            fieldMap.put("intro", "文字介绍");
            fieldMap.put("notes", "注意事项");
            // excel的sheetName
            String sheetName = "课程列表";
            // excel要导出的数据
            Map<String, Object> map = new HashMap<>();
            String[] ids = courseIds.split(",");
            map.put("ids", ids);
            List<TCourse> list = courseService.getCourses(map);
            if (list.size() == 0) {
                TCourse empty = new TCourse();
                list.add(empty);
            } else {
                //自定义数据
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getCourseType() == 1) {
                        list.get(i).setCourseTypeStr("团课");
                    } else if (list.get(i).getCourseType() == 2) {
                        list.get(i).setCourseTypeStr("私教");
                    }
                    list.get(i).setClassHourStr(DateUtil.toString(list.get(i).getClassHour(), "yyyy-MM-dd HH:mm"));
                }
            }
            // 导出
            String fileName = "";//excel文件名
            ExcelUtil.listToExcel(list, fieldMap, sheetName, 65535, response, fileName);
        } catch (Exception e) {
            log.error("导出课程数据异常：" + e);
        }
    }

    /**
     * 查询不重复课程（同一种课程只展示一条数据，不按上课时间都展示出来）
     */
    @RequestMapping("getCourseNoRepeat.do")
    @ResponseBody
    public ApiModel getCourseNoRepeat(String gymIds, String status) {
        ApiModel apiModel = new ApiModel();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            if (!"0".equals(gymIds)) {
                map.put("gymIds", gymIds);
            }
            map.put("status", status);
            List<TCourse> list = courseService.getCourseNoRepeat(map);
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
            log.error("查询不重复课程列表失败：", e);
        }
        return apiModel;
    }
}
