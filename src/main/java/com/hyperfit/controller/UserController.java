package com.hyperfit.controller;

import com.hyperfit.entity.*;
import com.hyperfit.service.CourseService;
import com.hyperfit.service.SessionService;
import com.hyperfit.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
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
 * <p> 用户控制器 </p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/11/17 21:14
 */
@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

    @Resource(name = "sessionServiceImpl")
    private SessionService sessionService;

    @Resource(name = "courseServiceImpl")
    private CourseService courseService;

    /**
     * 公众号AppId
     */
    @Value("#{configProperties['app.id']}")
    private String appid;

    /**
     * 微信端-查询用户信息
     * 不传userId，则查询微信当前登录用户信息
     *
     * @param userId 要查询的用户id
     */
    @RequestMapping("getUserInfo.do")
    @ResponseBody
    public ApiModel getUserInfo(String openid, String userId) {
        ApiModel apiModel = new ApiModel();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, openid, user)) {
                return apiModel;
            }
            //验证是否绑定手机号，因前端这个请求是公共请求，暂时去掉权限
            /*if (StringUtils.isBlank(user.getPhone())) {
                apiModel.setCode("-3");
                apiModel.setMsg("未绑定手机号");
                return apiModel;
            }*/
            if (StringUtils.isNotBlank(userId)) {
                //查询其他用户信息
                Map<String, Object> map = new HashMap<>();
                map.put("userId", userId);
                user = userService.getUserInfo(map);
            }
            apiModel.setCode("0");
            apiModel.setData(user);
            apiModel.setMsg("查询成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("查询用户信息失败：", e);
        }
        return apiModel;
    }

    /**
     * 微信端-修改用户信息
     */
    @RequestMapping("updateUserInfo.do")
    @ResponseBody
    public ApiModel updateUserInfo(TUser user, String authcode) {
        ApiModel result = new ApiModel();
        try {
            String openid = user.getOpenid();
            TUser checkUser = new TUser();
            if (!checkUser(result, openid, checkUser)) {
                return result;
            }
            user.setUserId(checkUser.getUserId());
            String couponFlag = "no";//是否需要发放新手注册优惠券
            //绑定手机号
            if (StringUtils.isNotBlank(user.getPhone())) {
                if (!MobileUtil.isMobileNO(user.getPhone())) {
                    result.setCode("1");
                    result.setMsg("请填写正确的手机号码");
                    return result;
                }
                if (StringUtils.isBlank(authcode)) {
                    result.setCode("1");
                    result.setMsg("验证码不能为空");
                    return result;
                }
                Map<String, Object> map = new HashMap<>();
                map.put("openid", openid);
                map.put("key", user.getPhone());
                String systemCode = sessionService.getSessionByKey(map);//系统验证码
                if (!authcode.equals(systemCode)) {
                    result.setCode("1");
                    result.setMsg("验证码错误");
                    return result;
                }
                //检查手机号是否注册
                Map<String, Object> checkMap = new HashMap<>();
                checkMap.put("phone", user.getPhone());
                TUser temp = userService.getUserInfo(checkMap);
                if (temp != null && StringUtils.isBlank(temp.getOpenid())) {
                    //如果后台已创建该手机号的会员，则将当前微信号与后台的会员绑定，删除当前微信号注册的用户
                    Map<String, Object> delMap = new HashMap<>();
                    delMap.put("userId", checkUser.getUserId());
                    userService.delUser(delMap);
                    user.setUserId(temp.getUserId());
                    couponFlag = "yes";
                } else if (temp != null && StringUtils.isNotBlank(temp.getOpenid()) && !temp.getOpenid().equals(user.getOpenid())) {
                    result.setCode("1");
                    result.setMsg("该手机号码已注册");
                    return result;
                } else if (temp == null) {
                    couponFlag = "yes";
                }
            } else {//修改用户信息
                //验证是否绑定手机号
                if (StringUtils.isBlank(checkUser.getPhone())) {
                    result.setCode("-3");
                    result.setMsg("未绑定手机号");
                    return result;
                }
            }
            Map<String, Object> param = new HashMap<>();
            param.put("record", user);
            param.put("couponFlag", couponFlag);
            userService.updateUserInfo(param);
            result.setCode("0");
            result.setMsg("修改成功");
        } catch (Exception e) {
            result.setCode("1");
            result.setMsg("系统异常：" + e.getMessage());
            log.error("修改用户信息异常：" + e);
        }
        return result;
    }

    /**
     * 前端-查询所有系统用户
     */
    @RequestMapping("getSysUsers.do")
    @ResponseBody
    public ApiModel getSysUsers() {
        ApiModel result = new ApiModel();
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("status", "1");//状态 1:启用 2 :停用 3:删除
            List<SysUser> list = userService.getSysUsers(map);
            result.setCode("0");
            result.setData(list);
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setPassword("");
            }
            if (list.size() > 0) {
                result.setMsg("查询成功");
            } else {
                result.setMsg("查询结果为空");
            }
        } catch (Exception e) {
            result.setCode("1");
            result.setMsg("系统异常：请联系管理员");
            log.error("查询系统用户列表失败：", e);
        }
        return result;
    }

    /**
     * 微信端-查询用户会员卡列表
     */
    @RequestMapping("getClubCardList.do")
    @ResponseBody
    public ApiModel getClubCardList(String openid, String courseId) {
        ApiModel apiModel = new ApiModel();
        try {
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
            Map<String, Object> map = new HashMap<>();
            map.put("openid", openid);
            map.put("userId", user.getUserId());
            map.put("courseId", courseId);
            map.put("status", 1);//状态 1：正常 2：停用
            if (StringUtils.isNotBlank(courseId)) {
                map.put("queryType", 1);//查询未过期
            }
            List<TClubCard> list = userService.getClubCardList(map);

            apiModel.setCode("0");
            apiModel.setData(list);
            if (list.size() > 0) {
                apiModel.setMsg("查询成功");
            } else {
                apiModel.setMsg("查询结果为空");
            }
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：请联系管理员");
            log.error("查询用户会员卡列表失败：", e);
        }
        return apiModel;
    }

    /**
     * 查询会员卡详情
     */
    @RequestMapping("getClubCardInfo.do")
    @ResponseBody
    public ApiModel getClubCardInfo(String cardId) {
        ApiModel apiModel = new ApiModel();
        try {
            if (StringUtils.isAnyBlank(cardId)) {
                apiModel.setCode("1");
                apiModel.setMsg("参数不能为空");
                return apiModel;
            }
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("cardId", cardId);
            TClubCard cardInfo = userService.getClubCardInfo(map);

            apiModel.setCode("0");
            apiModel.setData(cardInfo);
            if (cardInfo != null) {
                apiModel.setMsg("查询成功");
            } else {
                apiModel.setMsg("查询结果为空");
            }
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：请联系管理员");
            log.error("查询会员卡详情失败：", e);
        }
        return apiModel;
    }

    /**
     * 查询用户余额明细列表
     */
    @RequestMapping("getBalanceItem.do")
    @ResponseBody
    public ApiModel getBalanceItem(Integer pageIndex, Integer pageSize, String openid) {
        ApiModel apiModel = new ApiModel();
        PageEntity param = new PageEntity();
        try {
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
            Map<String, Object> map = new HashMap<>();
            map.put("userId", user.getUserId());
            map.put("itemType", 1);//明细类型 1：返现 2：提现
            param.setMap(map);
            if (pageIndex != null) {
                param.setPageIndex(pageIndex);
            }
            if (pageSize != null) {
                param.setPageSize(pageSize);
            }
            List<TBalanceItem> list = userService.getBalanceItem(param);
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
            log.error("查询用户余额明细列表失败：", e);
        }
        apiModel.setData(param);
        return apiModel;
    }

    /**
     * 后台-查询系统用户功能权限列表
     */
    @RequestMapping("getUserFunctions.do")
    @ResponseBody
    public ApiModel getUserFunctions(String querySysUserId, String functionId) {
        ApiModel apiModel = new ApiModel();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            if (StringUtils.isNotBlank(querySysUserId)) {
                map.put("sysUserId", querySysUserId);//查询指定用户功能权限
            } else {
                map.put("sysUserId", sysUserId);//查询当前用户功能权限
            }
            map.put("functionId", functionId);
            List<SysFunction> list = userService.getUserFunctions(map);
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
            log.error("查询用户功能权限列表失败：", e);
        }
        return apiModel;
    }

    /**
     * 后台-查询系统用户分页列表
     */
    @RequestMapping("getSysUserList.do")
    @ResponseBody
    public ApiModel getSysUserList(Integer draw, Integer pageIndex, Integer pageSize, String conditions) {
        ApiModel apiModel = new ApiModel();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            // 判断是否拥有权限
            List<SysFunction> functions = getFunctions("1");
            boolean hasFunction = hasFunction(functions, "view");
            if (!hasFunction) {
                apiModel.setCode("1");
                apiModel.setMsg("无查看权限");
                return apiModel;
            }
            PageEntity param = new PageEntity();
            Map<String, Object> map = new HashMap<>();
            map.put("conditions", conditions);
            map.put("status", "1,2");//不查询删除状态的数据
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
            List<SysUser> list = userService.getSysUserList(param);
            PageUtil.objectToPage(param, list);
            apiModel.setCode("0");
            apiModel.setMsg("SUCCESS");
            apiModel.setData(param);
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常，请联系管理员");
            log.error("查询系统用户分页列表异常：", e);
        }
        return apiModel;
    }

    /**
     * 后台-新增系统用户
     */
    @RequestMapping("addSysUser.do")
    @ResponseBody
    public ApiModel addSysUser(SysUser sysUser) {
        ApiModel apiModel = new ApiModel();
        try {
            if (StringUtils.isAnyBlank(sysUser.getTruename(), sysUser.getUsername(), sysUser.getPassword())) {
                apiModel.setCode("1");
                apiModel.setMsg("参数不能为空");
                return apiModel;
            }
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            // 判断是否拥有权限
            List<SysFunction> functions = getFunctions("1");
            boolean hasFunction = hasFunction(functions, "add");
            if (!hasFunction) {
                apiModel.setCode("1");
                apiModel.setMsg("无操作权限");
                return apiModel;
            }
            //姓名查重
            Map<String, Object> map = new HashMap<>();
            map.put("truename", sysUser.getTruename());
            map.put("status", "1,2");//状态  1:启用  2 :停用  3:删除
            SysUser checkUser = userService.getSysUserInfo(map);
            if (checkUser != null) {
                apiModel.setCode("1");
                apiModel.setMsg("姓名重复，请重新输入");
                return apiModel;
            }
            //帐号查重
            map.clear();
            map.put("username", sysUser.getUsername());
            map.put("status", "1,2");//状态  1:启用  2 :停用  3:删除
            checkUser = userService.getSysUserInfo(map);
            if (checkUser != null) {
                apiModel.setCode("1");
                apiModel.setMsg("帐号重复，请重新输入");
                return apiModel;
            }
            userService.insertSysUser(sysUser);
            apiModel.setCode("0");
            apiModel.setMsg("操作成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("新增系统用户异常：" + e);
        }
        return apiModel;
    }

    /**
     * 后台-修改系统用户
     */
    @RequestMapping("updateSysUser.do")
    @ResponseBody
    public ApiModel updateSysUser(SysUser sysUser) {
        ApiModel apiModel = new ApiModel();
        try {
            if (sysUser.getSysUserId() == null) {
                apiModel.setCode("1");
                apiModel.setMsg("参数不能为空");
                return apiModel;
            }
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            // 判断是否拥有权限
            List<SysFunction> functions = getFunctions("1");
            boolean hasFunction = hasFunction(functions, "edit");
            if (!hasFunction) {
                apiModel.setCode("1");
                apiModel.setMsg("无操作权限");
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            //姓名查重
            if (StringUtils.isNotBlank(sysUser.getTruename())) {
                map.put("truename", sysUser.getTruename());
                map.put("status", "1,2");//状态  1:启用  2 :停用  3:删除
                SysUser checkUser = userService.getSysUserInfo(map);
                if (checkUser != null && checkUser.getSysUserId() != sysUser.getSysUserId()) {
                    apiModel.setCode("1");
                    apiModel.setMsg("姓名重复，请重新输入");
                    return apiModel;
                }
            }
            //帐号查重
            if (StringUtils.isNotBlank(sysUser.getUsername())) {
                map.clear();
                map.put("username", sysUser.getUsername());
                map.put("status", "1,2");//状态  1:启用  2 :停用  3:删除
                SysUser checkUser = userService.getSysUserInfo(map);
                if (checkUser != null && checkUser.getSysUserId() != sysUser.getSysUserId()) {
                    apiModel.setCode("1");
                    apiModel.setMsg("帐号重复，请重新输入");
                    return apiModel;
                }
            }
            map.clear();
            map.put("record", sysUser);
            userService.updateSysUserInfo(map);
            apiModel.setCode("0");
            apiModel.setMsg("操作成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("修改系统用户异常：" + e);
        }
        return apiModel;
    }

    /**
     * 后台-更新系统用户权限
     *
     * @param handleSysUserId 要更新的系统用户id
     */
    @RequestMapping("addSysUserFunctions.do")
    @ResponseBody
    public ApiModel addSysUserFunctions(String handleSysUserId, String functionIds) {
        ApiModel apiModel = new ApiModel();
        try {
            if (StringUtils.isAnyBlank(handleSysUserId, functionIds)) {
                apiModel.setCode("1");
                apiModel.setMsg("参数不能为空");
                return apiModel;
            }
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            // 判断是否拥有权限
            List<SysFunction> functions = getFunctions("1");
            boolean hasFunction = hasFunction(functions, "auth");
            if (!hasFunction) {
                apiModel.setCode("1");
                apiModel.setMsg("无操作权限");
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("sysUserId", handleSysUserId);
            map.put("functionIds", functionIds);
            userService.addSysUserFunctions(map);
            apiModel.setCode("0");
            apiModel.setMsg("操作成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("更新系统用户权限异常：" + e);
        }
        return apiModel;
    }

    /**
     * 查询用户列表
     */
    @RequestMapping("getUsers.do")
    @ResponseBody
    public ApiModel getUsers(String userType, String status) {
        ApiModel apiModel = new ApiModel();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("userType", userType);
            map.put("status", status);
            List<TUser> list = userService.getUsers(map);
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
            log.error("查询用户列表失败：", e);
        }
        return apiModel;
    }

    /**
     * 查询用户分页列表
     */
    @RequestMapping("getUserList.do")
    @ResponseBody
    public ApiModel getUserList(Integer draw, Integer pageIndex, Integer pageSize, String userId, String gymId, String coachType, String sex, String conditions,
                                String userType, String status, String startDate, String endDate, String nickname, String truename, String phone, String cardType) {
        ApiModel apiModel = new ApiModel();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            PageEntity param = new PageEntity();
            Map<String, Object> map = new HashMap<>();
            map.put("userId", userId);
            map.put("gymId", gymId);
            map.put("coachType", coachType);
            map.put("sex", sex);
            map.put("conditions", conditions);
            map.put("userType", userType);
            map.put("status", status);
            map.put("startDate", startDate);
            map.put("endDate", endDate);
            map.put("nickname", nickname);
            map.put("truename", truename);
            map.put("phone", phone);
            map.put("cardType", cardType);
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
            List<TUser> list = userService.getUserList(param);
            PageUtil.objectToPage(param, list);
            apiModel.setCode("0");
            if (list.size() > 0) {
                apiModel.setMsg("查询成功");
            } else {
                apiModel.setMsg("查询结果为空");
            }
            apiModel.setData(param);
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("查询用户分页列表失败：", e);
        }
        return apiModel;
    }

    /**
     * 后台-查询会员详情
     */
    @RequestMapping("getUserDetail.do")
    @ResponseBody
    public ApiModel getUserDetail(String userId, String phone) {
        ApiModel apiModel = new ApiModel();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            if (StringUtils.isBlank(userId) && StringUtils.isBlank(phone)) {
                apiModel.setCode("1");
                apiModel.setMsg("参数不能为空");
                return apiModel;
            }
            Map<String, Object> param = new HashMap<>();
            param.put("userId", userId);
            param.put("phone", phone);
            user = userService.getUserInfo(param);
            if (user != null) {
                apiModel.setCode("0");
                apiModel.setData(user);
                apiModel.setMsg("查询成功");
            } else {
                apiModel.setCode("1");
                apiModel.setMsg("用户不存在");
            }
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("查询会员信息失败：", e);
        }
        return apiModel;
    }

    /**
     * 后台-新增教练
     *
     * @param gymIds 门店id，多个以逗号分隔
     */
    @RequestMapping("addUser.do")
    @ResponseBody
    public ApiModel addUser(TUser user, String gymIds) {
        ApiModel apiModel = new ApiModel();
        try {
            String functionId = "34";
            if (user.getUserType() == 1) {//添加会员
                if (StringUtils.isAnyBlank(user.getHeadimgurl(), user.getName(), user.getPhone(), user.getIntro()) || user.getSex() == null) {
                    apiModel.setCode("1");
                    apiModel.setMsg("参数不能为空");
                    return apiModel;
                }
                functionId = "39";
            } else {//添加教练
                if (StringUtils.isAnyBlank(user.getHeadimgurl(), user.getName(), user.getPhone(), user.getCoachLabel(), user.getIntro(), user.getCoachImages(), gymIds)
                        || user.getSex() == null || user.getCoachType() == null || user.getWeight() == null) {
                    apiModel.setCode("1");
                    apiModel.setMsg("参数不能为空");
                    return apiModel;
                }
                functionId = "34";
            }
            TUser checkUser = new TUser();
            if (!checkUser(apiModel, "", checkUser)) {
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
            //检查手机号是否重复
            Map<String, Object> map = new HashMap<>();
            map.put("phone", user.getPhone());
            TUser temp = userService.getUserInfo(map);
            if (temp != null) {
                apiModel.setCode("1");
                apiModel.setMsg("该手机号码已注册");
                return apiModel;
            }
            map.put("gymIds", gymIds);
            map.put("record", user);
            userService.insertUser(map);
            apiModel.setCode("0");
            apiModel.setMsg("操作成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("新增用户异常：" + e);
        }
        return apiModel;
    }

    /**
     * 后台-修改用户信息
     *
     * @param gymIds   门店id，多个以逗号分隔
     * @param pageType 页面类型 1：会员管理  2：教练管理
     */
    @RequestMapping("updateUser.do")
    @ResponseBody
    public ApiModel updateUser(TUser user, String gymIds, String pageType) {
        ApiModel apiModel = new ApiModel();
        try {
            if (user.getUserId() == null) {
                apiModel.setCode("1");
                apiModel.setMsg("参数不能为空");
                return apiModel;
            }
            TUser checkUser = new TUser();
            if (!checkUser(apiModel, "", checkUser)) {
                return apiModel;
            }
            // 判断是否拥有权限
            String functionId = "34";
            if ("1".equals(pageType)) {
                functionId = "39";
            }
            String code = "edit";
            if (user.getStatus() != null && user.getStatus() == 2) {
                code = "stop";
            }
            if (user.getStatus() != null && user.getStatus() == 3) {
                code = "del";
            }
            List<SysFunction> functions = getFunctions(functionId);
            boolean hasFunction = hasFunction(functions, code);
            if (!hasFunction) {
                apiModel.setCode("1");
                apiModel.setMsg("无操作权限");
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            if (user.getStatus() == null) {//删除、封禁操作不用查重
                //检查手机号是否重复
                map.put("phone", user.getPhone());
                TUser temp = userService.getUserInfo(map);
                if (temp != null && temp.getUserId() != user.getUserId()) {
                    apiModel.setCode("1");
                    apiModel.setMsg("该手机号码已注册");
                    return apiModel;
                }
            } else if ("2".equals(pageType) && user.getStatus() == 3) {
                //删除教练时，先查询是否有未上的课程
                Map<String, Object> temp = new HashMap<>();
                temp.put("userId", user.getUserId());
                temp.put("queryStatus", 1);//查询未开课的课程
                List<TCourse> list = courseService.getCourses(temp);
                if (list.size() > 0) {
                    apiModel.setCode("1");
                    apiModel.setMsg("该教练仍有未开课的课程，请将课程关闭或将课程教练更换后再删除");
                    return apiModel;
                }
            }
            map.put("gymIds", gymIds);
            map.put("record", user);
            userService.updateUserInfo(map);
            apiModel.setCode("0");
            apiModel.setMsg("操作成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("修改用户异常：" + e);
        }
        return apiModel;
    }

    /**
     * 管理会员卡用户
     */
    @RequestMapping("updateClubCard.do")
    @ResponseBody
    public ApiModel updateClubCard(TClubCard record) {
        ApiModel apiModel = new ApiModel();
        try {
            if (record.getCardId() == null) {
                apiModel.setCode("1");
                apiModel.setMsg("参数不能为空");
                return apiModel;
            }
            TUser checkUser = new TUser();
            if (!checkUser(apiModel, "", checkUser)) {
                return apiModel;
            }
            // 判断是否拥有权限
            List<SysFunction> functions = getFunctions("39");
            boolean hasFunction = hasFunction(functions, "bind");
            if (!hasFunction) {
                apiModel.setCode("1");
                apiModel.setMsg("无操作权限");
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("record", record);
            userService.updateClubCardInfo(map);
            apiModel.setCode("0");
            apiModel.setMsg("操作成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("修改会员卡信息异常：" + e);
        }
        return apiModel;
    }

    /**
     * 导出用户excel文件
     *
     * @param userIds 要导出的用户id，多个以英文逗号","分隔
     * @param type    数据类型  1：会员  2：教练
     */
    @RequestMapping("/exportUser.do")
    public void exportUser(HttpServletResponse response, String userIds, String type) {
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
            if (StringUtils.isBlank(userIds)) {
                msg = "参数不能为空";
                out.print(msg);
                return;
            }
            // excel表头
            LinkedHashMap<String, String> fieldMap = new LinkedHashMap<>();
            fieldMap.put("name", "姓名");
            fieldMap.put("phone", "手机号");
            fieldMap.put("sex", "性别");
            // excel的sheetName
            String sheetName = "会员列表";
            if ("1".equals(type)) {
                fieldMap.put("cardTypeStr", "会员卡类型");
                fieldMap.put("statusStr", "状态");
                fieldMap.put("createTimeStr", "注册日期");
            } else if ("2".equals(type)) {
                fieldMap.put("gyms", "所属门店");
                fieldMap.put("coachTypeStr", "类型");
                fieldMap.put("weight", "权重");
                sheetName = "教练列表";
            }
            // excel要导出的数据
            Map<String, Object> map = new HashMap<>();
            String[] ids = userIds.split(",");
            map.put("ids", ids);
            List<TUser> list = userService.getUsers(map);
            if (list.size() == 0) {
                TUser empty = new TUser();
                list.add(empty);
            } else {
                //自定义数据
                for (int i = 0; i < list.size(); i++) {
                    if ("1".equals(list.get(i).getSex())) {
                        list.get(i).setSex("男");
                    } else if ("2".equals(list.get(i).getSex())) {
                        list.get(i).setSex("女");
                    } else {
                        list.get(i).setSex("未知");
                    }
                    if (list.get(i).getStatus() == 1) {
                        list.get(i).setStatusStr("正常");
                    } else if (list.get(i).getStatus() == 2) {
                        list.get(i).setStatusStr("已封禁");
                    }
                    List<TClubCard> cardList = list.get(i).getCardList();
                    String cardType = "";
                    for (int j = 0; j < cardList.size(); j++) {
                        if (cardList.get(j).getCardType() == 1 && !cardType.contains("储值卡")) {
                            cardType += "储值卡、";
                        } else if (cardList.get(j).getCardType() == 2 && !cardType.contains("次卡")) {
                            cardType += "次卡、";
                        }
                    }
                    if (StringUtils.isNotBlank(cardType)) {
                        cardType = cardType.substring(0, cardType.length() - 1);
                    }
                    list.get(i).setCardTypeStr(cardType);

                    List<TGym> gymList = list.get(i).getGymList();
                    String gyms = "";
                    for (int j = 0; j < gymList.size(); j++) {
                        gyms += gymList.get(j).getGymName() + "、";
                    }
                    if (StringUtils.isNotBlank(gyms)) {
                        gyms = gyms.substring(0, gyms.length() - 1);
                    }
                    list.get(i).setGyms(gyms);
                    if (list.get(i).getCoachType() == 1) {
                        list.get(i).setCoachTypeStr("全职");
                    } else if (list.get(i).getCoachType() == 2) {
                        list.get(i).setCoachTypeStr("兼职");
                    }
                    list.get(i).setCreateTimeStr(DateUtil.toString(list.get(i).getCreateTime(), "yyyy-MM-dd HH:mm"));
                }
            }
            // 导出
            String fileName = "";//excel文件名
            ExcelUtil.listToExcel(list, fieldMap, sheetName, 65535, response, fileName);
        } catch (Exception e) {
            log.error("导出用户数据异常：" + e);
        }
    }

}