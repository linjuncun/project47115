package com.hyperfit.util.wechat;

public class UrlData {
	private static final String questionMark = "?";
	private String baseUrl = null;
	private String paramsPart = null;

	public UrlData() {
	}

	public UrlData(String baseUrl, String params) {
		setBaseUrl(baseUrl);
		setParamsPart(params);
	}

	public String getURL() {
		if (null != paramsPart && !paramsPart.isEmpty()) {
			return (baseUrl + questionMark + paramsPart);
		} else {
			return baseUrl;
		}
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getParamsPart() {
		return paramsPart;
	}

	public void setParamsPart(String paramsPart) {
		this.paramsPart = paramsPart;
	}

}
