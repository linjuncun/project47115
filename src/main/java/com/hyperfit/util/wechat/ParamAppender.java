package com.hyperfit.util.wechat;

/**
 * ParamAppender API调用的URI参数字段 <br>
 * e.g. "appid=APPID"
 *
 */
public class ParamAppender {
	private static final String andSign = "&";
	private static final String equalSign = "=";

	private StringBuffer paramBuffer = null;

	public static ParamAppender create(String paramName, String paramValue) {
		return new ParamAppender(paramName, paramValue);
	}

	private ParamAppender(String paramName, String paramValue) {
		paramBuffer = new StringBuffer(paramName);
		paramBuffer.append(equalSign).append(paramValue);
	}

	@Override
	public String toString() {
		return this.paramBuffer.toString();
	}

	/**
	 * appendAnd 以"&"符号链接两个参数 <br>
	 * e.g. "appid=APPID&secret=APPSECRET"
	 * 
	 * @param appender
	 * @return
	 */
	public ParamAppender appendAnd(ParamAppender appender) {
		this.paramBuffer.append(andSign).append(appender.toString());
		return this;
	}

	/**
	 * appendAnd 以"&"符号链接两个参数 <br>
	 * e.g. "appid=APPID&secret=APPSECRET"
	 * 
	 * @param appender
	 * @return
	 */
	public ParamAppender appendAnd(String paramName, String paramValue) {
		this.paramBuffer.append(andSign).append(paramName).append(equalSign)
				.append(paramValue);
		return this;
	}

	/**
	 * append 以"&"连接另外一个String <br>
	 * e.g. "appid=APPID&STRING"
	 * 
	 * @param param
	 * @return
	 */
	public ParamAppender appendAnd(String param) {
		this.paramBuffer.append(andSign).append(param);
		return this;
	}

	/**
	 * append 连接另外一个String <br>
	 * e.g. "appid=APPIDSTRING"
	 * 
	 * @param param
	 * @return
	 */
	public ParamAppender append(String param) {
		this.paramBuffer.append(param);
		return this;
	}
}
