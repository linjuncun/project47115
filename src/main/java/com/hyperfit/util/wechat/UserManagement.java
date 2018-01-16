package com.hyperfit.util.wechat;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hyperfit.util.HttpRequestHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.Date;

@Component
public class UserManagement {

	private LogWrapper logWrapper = new LogWrapper(UserManagement.class);
	private Gson gson = new Gson();

	@Autowired
	private AppPrincipal appPrincipal;

	@Value("#{configProperties['user.authc.baseurl']}")
	private String userAuthorizeBaseUrl;

	@Value("#{configProperties['user.authc.callback.uri']}")
	private String userAuthorizeCallbackUri;

	@Value("#{configProperties['user.authc.uri.encode']}")
	private String userAuthorizeUrlEncode;

	@Value("#{configProperties['user.authc.scope.silence']}")
	private String userAuthorizeScopeInSilience;

	@Value("#{configProperties['user.authc.scope.active']}")
	private String userAuthorizeScopeActive;

	@Value("#{configProperties['user.authc.token.baseurl']}")
	private String userAuthorizeTokenBaseUrl;

	@Value("#{configProperties['user.authc.token.validation.baseurl']}")
	private String userAuthorizeTokenValidationBaseUrl;

	@Value("#{configProperties['user.refresh.token.baseurl']}")
	private String userRefreshTokenBaseUrl;

	@Value("#{configProperties['user.info.baseurl']}")
	private String userInforBaseUrl;

	@Value("#{configProperties['user.info.unionid.url']}")
	private String userInfoUnionidUrl;
	
	/**
	 * constructUserAuthcUrl 用户授权第一步: 生成引导用户授权的URL <br>
	 * 
	 * @param isBasic
	 *            <li>true代表snsapi_base, 静默授权,只能获取用户的openid</li>
	 *            <li>false代表snsapi_userinfo, 非静默授权,可以获取用户的基本信息</li>
	 * @param state
	 *            String 开发者定制的参数, 可以填写a-zA-Z0-9的参数值，最多128字节
	 * @return UrlData 用户授权的URL数据信息
	 */
	public UrlData constructUserAuthcUrl(boolean isBasic, String state, String callbackUrl) {
		String encodedCallbackUrl = null;
		try {
			encodedCallbackUrl = URLEncoder.encode(callbackUrl, this.userAuthorizeUrlEncode);
		} catch (Exception e) {
			Log logger = LogFactory.getLog(this.getClass());
			logger.fatal("encoding URL exception",e);
		}

		ParamAppender paramsAppender = null;
		if (isBasic) {
			paramsAppender = appPrincipal.appId2UriParam().appendAnd("redirect_uri", encodedCallbackUrl)
					.appendAnd("response_type=code").appendAnd("scope", this.userAuthorizeScopeInSilience)
					.appendAnd("state", state).append("#wechat_redirect");
		} else {
			paramsAppender = appPrincipal.appId2UriParam().appendAnd("redirect_uri", encodedCallbackUrl)
					.appendAnd("response_type=code").appendAnd("scope", this.userAuthorizeScopeActive)
					.appendAnd("state", state).append("#wechat_redirect");
		}

		UrlData urlData = new UrlData(this.userAuthorizeBaseUrl, paramsAppender.toString());

		return urlData;
	}

	/**
	 * 通过微信API，用Code换取Token
	 * 
	 * @param code
	 * @param state
	 * @return
	 */
	public UserAccessToken getUserAccessTokenByCode(String code, String state) {
		logWrapper.setAllHeaders("getUserAccessTokenByCode", code, state);

		if (null == code || code.isEmpty()) {
			// 用户拒绝授权
			logWrapper.logFail("user reject to authorize");
			return null;
		}

		ParamAppender paramsAppender = appPrincipal.principal2UriParam().appendAnd("code", code)
				.appendAnd("grant_type=authorization_code");
		String responses = HttpRequestHelper.sendGet(this.userAuthorizeTokenBaseUrl, paramsAppender.toString());

		// -- Get user access_token from Wechat API
		UserAccessToken accessToken = null;
		try {
			accessToken = gson.fromJson(responses, UserAccessToken.class);
			if (null != accessToken) {
				accessToken.setCreate_time((new Date()).getTime() / 1000);
				accessToken.setIsAuthorized(true);
			}

			if (UserAccessToken.isOK(accessToken)) {
				logWrapper.logSuccess(String.format("get access_token from wechat: %s", gson.toJson(accessToken)));
				return accessToken;
			} else {
				logWrapper.logFail("can not get user access_token from weixin");
				return null;
			}

		} catch (JsonSyntaxException e) {
			logWrapper.logFail(String.format("responses=%s", responses));
			return null;
		}
	}


	/**
	 * 通过微信API，检验token是否仍然有效
	 * 
	 * @return
	 */
	public boolean isTokenValid(UserAccessToken accessToken) {
		if (null == accessToken || null == accessToken.getAccess_token() || accessToken.getAccess_token().isEmpty()
				|| null == accessToken.getOpenid() || accessToken.getOpenid().isEmpty()) {
			return false;
		} else {
			logWrapper.setAllHeaders("isTokenValid", accessToken);
		}

		ParamAppender paramsAppender = ParamAppender.create("access_token", accessToken.getAccess_token())
				.appendAnd("openid", accessToken.getOpenid());
		String responses = HttpRequestHelper.sendGet(this.userAuthorizeTokenValidationBaseUrl,
				paramsAppender.toString());

		try {
			UserAccessToken newToken = gson.fromJson(responses, UserAccessToken.class);
			logWrapper.logSuccess(String.format("get user access_token from weixin: %s", gson.toJson(newToken)));

			if (UserAccessToken.isOK(newToken)) {
				return true;
			} else {
				return false;
			}

		} catch (JsonSyntaxException e) {
			logWrapper.logFail("response=" + responses);
			return false;
		}

	}

	/**
	 * 通过微信API接口，刷新token
	 * 
	 * @param refreshTokenStr
	 * @return
	 */
	public UserAccessToken refreshToken(String refreshTokenStr) {
		logWrapper.setAllHeaders("refreshToken", refreshTokenStr);

		ParamAppender paramsAppender = appPrincipal.appId2UriParam().appendAnd("grant_type=refresh_token")
				.appendAnd("refresh_token", refreshTokenStr);
		String responses = HttpRequestHelper.sendGet(this.userRefreshTokenBaseUrl, paramsAppender.toString());

		try {
			UserAccessToken accessToken = gson.fromJson(responses, UserAccessToken.class);
			logWrapper.logSuccess(String.format("get user access_token from weixin: %s", accessToken));

			return accessToken;

		} catch (JsonSyntaxException e) {
			logWrapper.logFatal("response=" + responses);
			return null;
		}

	}
	
	public UserInfo getUserInfo(UserAccessToken accessToken) {
		logWrapper.setAllHeaders("getUserInfo", accessToken);

		if (!UserAccessToken.isOK(accessToken)) {
			logWrapper.logFail("invalid access_token");
			return null;
		}

		if (!accessToken.getIsAuthorized()) {
			logWrapper.logFail("user has not authorized");
			return null;
		}

		ParamAppender paramsAppender = appPrincipal.appId2UriParam()
				.appendAnd("access_token", accessToken.getAccess_token()).appendAnd("openid", accessToken.getOpenid())
				.appendAnd("lang", "zh_CN");
		String responses = HttpRequestHelper.sendGet(this.userInforBaseUrl, paramsAppender.toString());

		try {
			UserInfo userInfo = gson.fromJson(responses, UserInfo.class);
			logWrapper.logSuccess(String.format("user info from weixin: %s", gson.toJson(userInfo)));

			return userInfo;

		} catch (JsonSyntaxException e) {
			logWrapper.logFatal("response=" + responses);
			return null;
		}
	}
	/**
	 * 获取微信用户信息
	 * @param type 获取方式 1：UnionID机制  2：网页授权方式
	 */
	public UserInfo getUserInfo(String accessToken, String openid, int type) {
		logWrapper.setAllHeaders("getUserInfo", accessToken, openid);

		if (!isStringOK(accessToken)) {
			logWrapper.logFail("invalid access_token");
			return null;
		}

		if (!isStringOK(openid)) {
			logWrapper.logFail("invalid openid");
			return null;
		}

		ParamAppender paramsAppender = appPrincipal.appId2UriParam().appendAnd("access_token", accessToken)
				.appendAnd("openid", openid).appendAnd("lang", "zh_CN");
		String responses = "";
		if(type == 1){//获取用户基本信息(UnionID机制)
			responses = HttpRequestHelper.sendGet(this.userInfoUnionidUrl, paramsAppender.toString());
		}else{//获取用户基本信息(网页授权方式)
			responses = HttpRequestHelper.sendGet(this.userInforBaseUrl, paramsAppender.toString());
		}

		try {
			UserInfo userInfo = gson.fromJson(responses, UserInfo.class);
			logWrapper.logSuccess(String.format("get user info from weixin: %s", gson.toJson(userInfo)));

			return userInfo;

		} catch (JsonSyntaxException e) {
			logWrapper.logFatal("response=" + responses);
			return null;
		}
	}
	private boolean isRefreshRequired(UserAccessToken daoAccessToken) {
		logWrapper.setAllHeaders("isRefreshRequired", daoAccessToken);

		// -- If no valid access_token in DAO yet
		if (!ApiAccessToken.isOK(daoAccessToken)) {
			logWrapper.logFail("invalid access_token");
			return true;
		}

		// -- Judge validation
		return isTokenValid(daoAccessToken);
	}

	public String getUserAuthorizeTokenValidationBaseUrl() {
		return userAuthorizeTokenValidationBaseUrl;
	}

	public void setUserAuthorizeTokenValidationBaseUrl(String userAuthorizeTokenValidationBaseUrl) {
		this.userAuthorizeTokenValidationBaseUrl = userAuthorizeTokenValidationBaseUrl;
	}

	private boolean isStringOK(String val) {
		return (null != val && !val.isEmpty());
	}

}
