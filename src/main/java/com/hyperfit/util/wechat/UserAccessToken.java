package com.hyperfit.util.wechat;

import org.springframework.stereotype.Component;

@Component
public class UserAccessToken extends ApiAccessToken {
	/**
	 * 用户是否授权 <br>
	 */
	private boolean isAuthorized = true;
	
	private String refresh_token = "";
	private String openid = "";
	private String scope = "";
	private String unionid = "";

	// -- Methods
	public static boolean isOK(UserAccessToken rhToken) {
		return (null != rhToken && rhToken.isOK());
	}

	public boolean isOK() {
		return (super.isOK() && isStringOK(refresh_token) && isStringOK(openid) && isStringOK(scope));
	}

	// -- Getters & Setters

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

	public boolean getIsAuthorized() {
		return isAuthorized;
	}

	public void setIsAuthorized(boolean isAuthorized) {
		this.isAuthorized = isAuthorized;
	}
}
