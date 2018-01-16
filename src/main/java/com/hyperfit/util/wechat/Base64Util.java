package com.hyperfit.util.wechat;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;

public class Base64Util {
    // 编码
    public static String encode(byte[] str) {
        BASE64Encoder b64 = new BASE64Encoder();
        return b64.encode(str);
    }

    // 还原
    public static byte[] decode(String str) {
        try {
            BASE64Decoder b64decoder = new BASE64Decoder();
            byte[] after = b64decoder.decodeBuffer(str);
            return after;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
