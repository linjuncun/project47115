package com.hyperfit.util.wechat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyperfit.controller.BaseController;
import com.hyperfit.util.HttpRequestHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * jsapi_ticket类
 */
public class JsapiTicketTimeTask {

    public Logger log = LogManager.getLogger(JsapiTicketTimeTask.class);

    public static String jsapi_ticket = "";
    public static String accessToken = "";

    /**
     * 每隔一个小时调用一次微信获取jsapi的接口的任务调用器，在spring配置里面调用
     */
    public void getTicket() {
        Properties prop = new Properties();
        InputStream in = getClass().getResourceAsStream("/project.properties");
        String appid = "";
        String secret = "";
        String token = "";
        String getticket = "";
        try {
            prop.load(in);
            appid = prop.getProperty("app.id").trim();
            secret = prop.getProperty("app.secret").trim();
            token = prop.getProperty("weixin.ticket.token").trim();
            getticket = prop.getProperty("weixin.ticket.getticket").trim();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 调用微信接口获取access_token凭证
        // Constant.ACCESS_TOKEN =
        // https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET
        String _url = token;
        String param = "grant_type=client_credential&appid=" + appid + "&secret=" + secret;
        String resp = HttpRequestHelper.sendGet(_url, param);
        if (!resp.contains("errcode")) {
            JSONObject object = JSON.parseObject(resp);
            String access_token = (String) object.get("access_token");
            if (access_token != null && !"".equals(access_token)) {
                accessToken = access_token;
                // 如果可以获取access_token，即可以调用jsapi_tiket的凭证了
                // Constant.JSAPI_TICKET = https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi
                String ticketUrl = getticket;
                param = "access_token=" + access_token + "&type=jsapi";
                String ticketStr = HttpRequestHelper.sendGet(ticketUrl, param);
                JSONObject ticketJson = JSONObject.parseObject(ticketStr);
                String errmsg = (String) ticketJson.get("errmsg");
                log.info(errmsg);
                // 如果调用成功，返回ok
                if ("ok".equals(errmsg)) {
                    jsapi_ticket = (String) ticketJson.get("ticket");
                    log.info("成功获取jsapi_ticket：" + jsapi_ticket);
                }
            }
        } else {
            log.info(resp);
        }

    }
}
