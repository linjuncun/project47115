package com.hyperfit.util.wechat;

import com.hyperfit.util.HttpRequestHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * <p> 微信模版消息工具类</p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/12/16 16:30
 */
@Component
public class WeChatMessage {

    public Logger log = LogManager.getLogger(WeChatMessage.class);

    @Resource(name = "jsapiTask")
    private JsapiTicketTimeTask jsapiTask;

    public void sendTemplateMessage(String jsonMsg) {
        String access_token = jsapiTask.accessToken;
        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + access_token;
        String result = HttpRequestHelper.post(url, jsonMsg);
        log.info("模板消息发送结果：" + result);
    }


}
