package com.hyperfit.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobileUtil {
	 /**
     * 判断字段串是否是手机号
     * @param mobiles
     */
    public static boolean isMobileNO(String mobiles) {  
        boolean flag = false;  
        try {  
            /* 
             * 移动：134、135、136、137、138、139、150、151、152、157、158、159、182、183、184、187、188、178(4G)、147(上网卡)
             * 联通：130、131、132、155、156、185、186、176(4G)、145(上网卡)
             * 电信：133、153、180、181、189 、177(4G)
             */
            Pattern p = Pattern.compile("^((13[0-9])|(14[5|7])|(15[0-9])|(17[6-8])|(18[0-9]))\\d{8}$");  
            Matcher m = p.matcher(mobiles);  
            flag = m.matches();  
        } catch (Exception e) {  
            flag = false;  
        }  
        return flag;  
    }  
}
