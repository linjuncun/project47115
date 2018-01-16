package com.hyperfit.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public class SignUtil {
	/**
	 * 对字符串进行MD5加密
	 * @param sourceStr
	 * @return
	 */
	public static String MD5(String sourceStr) {
        try {
        	if (sourceStr == null) {
        		return "";
        	}
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes("UTF-8"));
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            //32位加密  
            return buf.toString();  
            // 16位的加密  
            //return buf.toString().substring(8, 24);  
        } catch (Exception e) {
        	e.printStackTrace();
        	return null;
        }
    }

	/**
	 * 微信支付md5签名
	 * @param parameters
	 * @param key
	 * @return  
	 * @date   2016年9月20日 下午6:02:23
	 */
	public static String wxPaySign(SortedMap<Object, Object> parameters, String key) {
		String result = "";
		try {
			StringBuffer str = new StringBuffer();
			Set<Map.Entry<Object, Object>> es = parameters.entrySet();// 所有参与传参的参数按照accsii排序（升序）
			Iterator<Map.Entry<Object, Object>> it = es.iterator();
			while (it.hasNext()) {
				Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) it.next();
				String k = (String) entry.getKey();
				Object v = entry.getValue();
				if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
					str.append(k + "=" + v + "&");
				}
			}
			str.append("key=" + key);
			System.out.println(str);
			// String sign =
			// 生成实现指定摘要算法的 MessageDigest 对象。
			MessageDigest md = MessageDigest.getInstance("MD5");
			// 使用指定的字节数组更新摘要。
			md.update(str.toString().getBytes("UTF-8"));
			// 通过执行诸如填充之类的最终操作完成哈希计算。
			byte b[] = md.digest();
			// 生成具体的md5密码到buf数组
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			result = buf.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	public static boolean checkSignature(String signature, String token, String timestamp,
			String nonce) {
		//从请求中（也就是微信服务器传过来的）拿到的token, timestamp, nonce
		String[] arr = new String[] { token, timestamp, nonce };
		// 将token、timestamp、nonce三个参数进行字典序排序
		sort(arr);
		StringBuilder content = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			content.append(arr[i]);
		}
		MessageDigest md = null;
		String tmpStr = null;

		try {
			md = MessageDigest.getInstance("SHA-1");
			// 将三个参数字符串拼接成一个字符串进行sha1加密
			byte[] digest = md.digest(content.toString().getBytes());
			//将字节数组转成字符串
			tmpStr = byteToStr(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		content = null;
		// 将sha1加密后的字符串可与signature对比，标识该请求来源于微信
		return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;
	}
	
	private static String byteToStr(byte[] byteArray) {
		String strDigest = "";
		for (int i = 0; i < byteArray.length; i++) {
			strDigest += byteToHexStr(byteArray[i]);
		}
		return strDigest;
	}
	
	
	private static String byteToHexStr(byte mByte) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
				'B', 'C', 'D', 'E', 'F' };
		char[] tempArr = new char[2];
		tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
		tempArr[1] = Digit[mByte & 0X0F];

		String s = new String(tempArr);
		return s;
	}
	
	public static void sort(String a[]) {
		for (int i = 0; i < a.length - 1; i++) {
			for (int j = i + 1; j < a.length; j++) {
				if (a[j].compareTo(a[i]) < 0) {
					String temp = a[i];
					a[i] = a[j];
					a[j] = temp;
				}
			}
		}
	}

}
