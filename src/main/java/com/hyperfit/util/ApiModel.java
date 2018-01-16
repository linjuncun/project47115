package com.hyperfit.util;

/**
 * <p> 接口响应json数据对象</p>
 */
public class ApiModel {
	private String code;                //状态码
	private String msg;                 //返回的文字消息
	private Object data;                //返回的数据

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
