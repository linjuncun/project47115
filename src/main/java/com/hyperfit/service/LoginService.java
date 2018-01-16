package com.hyperfit.service;

import com.hyperfit.util.ApiModel;

/**
 * <p> 登录service </p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/11/16 22:01
 */
public interface LoginService {

    /**
     * 微信端登录
     */
    public ApiModel apiLogin(String code);
}
