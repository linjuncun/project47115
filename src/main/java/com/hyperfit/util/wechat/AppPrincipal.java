package com.hyperfit.util.wechat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * AppPrincipal 公众号身份信息，包括
 */
@Component
public class AppPrincipal {
    /**
     * AppID(应用ID)
     */
    @Value("#{configProperties['app.id']}")
    private String appId;

    /**
     * AppSecret(应用密钥)
     */
    @Value("#{configProperties['app.secret']}")
    private String appSecret;

    /**
     * Token(公共帐号的令牌)
     */
    @Value("#{configProperties['app.token']}")
    private String appToken;

    // -- Methods

    public ParamAppender appId2UriParam() {
        return ParamAppender.create("appid", getAppId());
    }

    public ParamAppender appSecret2UriParam() {
        return ParamAppender.create("secret", getAppSecret());
    }

    public ParamAppender principal2UriParam() {
        return appId2UriParam().appendAnd(appSecret2UriParam());
    }

    public String appToken2UriParam() {
        return ParamAppender.create("appid", getAppId())
                .appendAnd("secret", getAppSecret()).toString();
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }

}
