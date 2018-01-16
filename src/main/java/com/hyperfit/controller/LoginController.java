package com.hyperfit.controller;

import com.hyperfit.entity.SysUser;
import com.hyperfit.entity.TUser;
import com.hyperfit.service.LoginService;
import com.hyperfit.util.ApiModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p> 登录控制器 </p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/11/16 21:58
 */
@Controller
@RequestMapping("/")
public class LoginController extends BaseController {

    @Resource(name = "loginServiceImpl")
    private LoginService loginService;

    /**
     * 微信端-用户登录（若用户未注册，则自动注册）
     *
     * @param code 前端调用微信授权页面获取的code，这里用code去换取微信openid和access_token
     */
    @RequestMapping("login.do")
    @ResponseBody
    public ApiModel apiLogin(String code) {
        ApiModel apiModel = new ApiModel();
        if (StringUtils.isAnyBlank(code)) {
            apiModel.setCode("1");
            apiModel.setMsg("参数错误");
            return apiModel;
        }
        try {
            apiModel = loginService.apiLogin(code);
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("注册失败：系统异常");
            log.error("注册失败：", e);
            return apiModel;
        }
        return apiModel;
    }

    /**
     * 后台-登录操作
     */
    @RequestMapping("sysLogin.do")
    @ResponseBody
    public ApiModel sysLogin(String username, String password) {
        ApiModel apiModel = new ApiModel();
        Map<String, Object> map = new HashMap<>();
        try {
            if (StringUtils.isAnyBlank(username, password)) {
                apiModel.setCode("1");
                apiModel.setMsg("参数不能为空");
            }
            map.put("username", username);
            SysUser user = userService.getSysUserInfo(map);
            if (user == null) {
                apiModel.setCode("1");
                apiModel.setMsg("帐号不存在");
            } else if (user.getStatus() == 2) {
                apiModel.setCode("1");
                apiModel.setMsg("帐号已停用，请联系管理员");
            } else {
                if (user.getPassword().equals(password)) {
                    //设置 shiro 验证框架信息
                    Subject currentUser = SecurityUtils.getSubject();
                    UsernamePasswordToken token = new UsernamePasswordToken(username, "ok", false);
                    currentUser.login(token);
                    httpSession.setAttribute("sysUserId", user.getSysUserId());
                    httpSession.setAttribute("username", user.getUsername());

                    // 更新登录记录信息
                    SysUser record = new SysUser();
                    record.setSysUserId(user.getSysUserId());
                    record.setLastLoginTime(new Date());
                    map.put("record", record);
                    userService.updateSysUserInfo(map);

                    apiModel.setCode("0");
                    apiModel.setMsg("登录成功");
                } else {
                    apiModel.setCode("1");
                    apiModel.setMsg("密码错误");
                }
            }
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("后台登录异常：", e);
        }
        return apiModel;
    }

    /**
     * 后台-修改密码
     */
    @RequestMapping("resetPwd.do")
    @ResponseBody
    public ApiModel resetPwd(String password, String oldPwd) {
        ApiModel apiModel = new ApiModel();
        try {
            TUser checkUser = new TUser();
            if (!checkUser(apiModel, "", checkUser)) {
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            if (StringUtils.isAnyBlank(oldPwd, password)) {
                apiModel.setCode("1");
                apiModel.setMsg("参数不能为空");
            }
            map.put("sysUserId", sysUserId);
            SysUser user = userService.getSysUserInfo(map);
            if (user == null) {
                apiModel.setCode("1");
                apiModel.setMsg("帐号不存在");
                return apiModel;
            } else if (user.getStatus() == 2) {
                apiModel.setCode("1");
                apiModel.setMsg("帐号已停用，请联系管理员");
                return apiModel;
            }
            if (!user.getPassword().equals(oldPwd)) {
                apiModel.setCode("1");
                apiModel.setMsg("原密码错误");
                return apiModel;
            }
            SysUser record = new SysUser();
            record.setSysUserId(user.getSysUserId());
            record.setPassword(password);
            map.put("record", record);
            userService.updateSysUserInfo(map);
            apiModel.setCode("0");
            apiModel.setMsg("修改成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("修改系统用户密码异常：" + e);
        }
        return apiModel;
    }

    /**
     * 后台-退出登录
     */
    @RequestMapping("logout.do")
    @ResponseBody
    public ApiModel logout() {
        ApiModel apiModel = new ApiModel();
        try {
            Subject subject = SecurityUtils.getSubject();
            if (subject.isAuthenticated()) {
                subject.logout();
            }
            apiModel.setCode("0");
            apiModel.setMsg("成功退出");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("退出登录异常", e);
        }
        return apiModel;
    }
}
