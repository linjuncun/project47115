package com.hyperfit.controller;

import com.hyperfit.entity.SysFunction;
import com.hyperfit.entity.TUser;
import com.hyperfit.service.UserService;
import com.hyperfit.util.ApiModel;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BaseController {

    public Logger log = LogManager.getLogger(BaseController.class);

    @Autowired
    public HttpSession httpSession;

    @Resource(name = "userServiceImpl")
    public UserService userService;

    public Integer sysUserId;

    @InitBinder
    public void initBinder(ServletRequestDataBinder binder) {
        /**
         * 自动转换日期类型的字段格式
         */
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm"), true));
    }


    /**
     * 校验用户
     */
    public boolean checkUser(ApiModel result, String openid, TUser user) throws Exception {
        if (StringUtils.isBlank(openid)) {
            //后台校验用户，用于判断ajax请求是否登录
            sysUserId = (Integer) httpSession.getAttribute("sysUserId");
            if (sysUserId == null || sysUserId == 0) {
                result.setCode("-1");
                result.setMsg("登录已失效，请重新登录");
                return false;
            }
            return true;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("openid", openid);
        TUser tUser = userService.getUserInfo(map);
        if (tUser == null) {
            result.setCode("-1");
            result.setMsg("用户未注册");
            return false;
        }
        if (tUser.getStatus() == 2) {
            result.setCode("-2");
            result.setMsg("帐号已被封禁，请联系工作人员");
            return false;
        }
        BeanUtils.copyProperties(user, tUser);
        return true;
    }


    /**
     * 获取用户权限列表
     */
    public List<SysFunction> getFunctions(String functionId) {
        Map<String, Object> map = new HashMap<>();
        map.put("sysUserId", sysUserId);
        map.put("functionId", functionId);
        List<SysFunction> list = userService.getUserFunctions(map);
        return list;
    }

    /**
     * 检验用户是否拥有权限
     */
    public boolean hasFunction(List<SysFunction> functions, String code) {
        boolean result = false;
        for (SysFunction function : functions) {
            if (function.getCode().equals(code)) {
                return true;
            }
        }
        return result;
    }

}
