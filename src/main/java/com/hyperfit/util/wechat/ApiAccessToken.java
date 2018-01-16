package com.hyperfit.util.wechat;

import org.springframework.stereotype.Component;

/**
 * 公众号在微信平台的全局唯一票据 <br>
 * 参考 http://mp.weixin.qq.com/wiki/home/index.html <br>
 * "获取接口调用凭证->获取access_token"
 *
 */
@Component
public class ApiAccessToken {
	private String access_token = "";
	private int expires_in = 0;
	private long create_time = 0;

	public static boolean isOK(ApiAccessToken rhToken) {
		return (null != rhToken && rhToken.isOK());
	}

	public boolean isOK() {
		return (isStringOK(access_token) && expires_in > 0 && getCreate_time() > 0);
	}

	protected boolean isStringOK(String val) {
		return (null != val && !val.isEmpty());
	}

	// -- Getters & Setters

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public int getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(int expires_in) {
		this.expires_in = expires_in;
	}

	public long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}
}
