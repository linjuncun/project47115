package com.hyperfit.util;

public class StringUtil {
	
	/**
	 * 随机生成数字
	 * @param length 字符串长度
	 * @return  
	 */
	public static String getRandomNum(int length) {
		String string = "1234567890"; //随机数字容器 
		StringBuffer random = new StringBuffer();
		int len = string.length();
		for (int i = 0; i < length; i++) {
			random.append(string.charAt((int)Math.round(Math.random() * (len-1))));
		}
		return random.toString();
	}
	
	/**
	 * 随机生成字符串
	 * @param length 字符串长度
	 * @return  
	 */
	public static String getRandomStr(int length) {
		String string = "abcdefghijklmnopqrstuvwxyz1234567890"; //随机字符串容器 
		StringBuffer random = new StringBuffer();
        int len = string.length();
        for (int i = 0; i < length; i++) {
        	random.append(string.charAt((int)Math.round(Math.random() * (len-1))));
        }
        return random.toString();
	}
	
	/**
	 * 微信支付回调返回信息设置
	 * @param return_code
	 * @param return_msg
	 */
	public static String setXML(String return_code, String return_msg) {
        return "<xml><return_code><![CDATA[" + return_code
                + "]]></return_code><return_msg><![CDATA[" + return_msg
                + "]]></return_msg></xml>";
	}
	
	
	/** 
	 * 数字不足位数左补0 
	 * @param str 要补0的字符串
	 * @param length 补足后的字符串长度
	 */  
	public static String addZeroForNum(String str, int length) {  
		int strLen = str.length();  
		if (strLen < length) {  
			while (strLen < length) {  
				StringBuffer sb = new StringBuffer();  
				sb.append("0").append(str);//左补0  
				// sb.append(str).append("0");//右补0  
				str = sb.toString();  
				strLen = str.length();  
			}  
		}  
		return str;  
	} 

}
