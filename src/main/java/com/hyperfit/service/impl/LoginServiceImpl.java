package com.hyperfit.service.impl;

import com.hyperfit.dao.TUserMapper;
import com.hyperfit.entity.TUser;
import com.hyperfit.service.LoginService;
import com.hyperfit.util.ApiModel;
import com.hyperfit.util.wechat.UserAccessToken;
import com.hyperfit.util.wechat.UserInfo;
import com.hyperfit.util.wechat.UserManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service("loginServiceImpl")
public class LoginServiceImpl implements LoginService {
    @Resource(name = "tUserMapperImpl")
    private TUserMapper tUserMapper;

    @Autowired
    private UserManagement userManagement;

    @Override
    public ApiModel apiLogin(String code) {
        ApiModel result = new ApiModel();
        Map<String, Object> map = new HashMap<>();
        UserAccessToken userToken = userManagement.getUserAccessTokenByCode(code, "1");
        if (null == userToken) {
            result.setCode("1");
            result.setMsg("失败：获取微信openid失败");
            return result;
        }
        String openid = userToken.getOpenid();
        String access_token = userToken.getAccess_token();
        userToken.setCreate_time((new Date()).getTime());
        userToken.setIsAuthorized(true); // 收到code代表用户授权了
        UserInfo userInfo = userManagement.getUserInfo(access_token, openid, 2);

        map.put("openid", openid);
        TUser user = tUserMapper.getUserInfo(map);
        if (user != null) {
            result.setCode("0");
            result.setData(user);
            result.setMsg("成功");
            //更新用户头像
            if (user.getHeadimgurl().contains("http")) {
                TUser record = new TUser();
                record.setUserId(user.getUserId());
                record.setHeadimgurl(userInfo.getHeadimgurl());
                Map<String, Object> temp = new HashMap<>();
                temp.put("record", record);
                tUserMapper.updateUserInfo(temp);
            }
            return result;
        }
        /*
         * 注册用户
		 */
        user = new TUser();
        user.setUserType(1);//用户类型 1：普通用户 2：教练
        user.setOpenid(openid);
        user.setName(userInfo.getNickname());
        user.setSex(userInfo.getSex());
        user.setHeadimgurl(userInfo.getHeadimgurl());
        tUserMapper.insertUser(user);

        result.setCode("0");
        result.setData(user);
        result.setMsg("成功");
        return result;
    }


}
