package com.hyperfit.controller;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.hyperfit.entity.SmsEntity;
import com.hyperfit.entity.TUser;
import com.hyperfit.service.SessionService;
import com.hyperfit.util.ApiModel;
import com.hyperfit.util.SmsUtil;
import com.hyperfit.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * <p> 阿里云短信控制器 </p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/11/17 14:16
 */
@Controller
@RequestMapping("/aliyun")
public class AliyunController extends BaseController {

    @Resource(name = "sessionServiceImpl")
    private SessionService sessionService;

    @Value("#{configProperties['sms.accessKeyId']}")
    private String accessKeyId;

    @Value("#{configProperties['sm.accessKeySecret']}")
    private String accessKeySecret;

    /**
     * 发送短信
     */
    @RequestMapping("sendSms.do")
    @ResponseBody
    public ApiModel sendSms(String phone, String openid) {
        ApiModel result = new ApiModel();
        try {
            if (StringUtils.isAnyBlank(phone, openid)) {
                result.setCode("1");
                result.setMsg("参数不能为空");
                return result;
            }
            TUser checkUser = new TUser();
            if (!checkUser(result, openid, checkUser)) {
                return result;
            }
            String smsCode = StringUtil.getRandomNum(4);//生成验证码
            //保存验证码至数据库模拟session表中
            Map<String, Object> map = new HashMap<>();
            map.put("openid", openid);
            map.put("key", phone);
            map.put("value", smsCode);
            sessionService.addSession(map);
            //调用阿里云SDK发短信
            SmsEntity smsEntity = new SmsEntity();
            smsEntity.setAccessKeyId(accessKeyId);
            smsEntity.setAccessKeySecret(accessKeySecret);
            smsEntity.setPhoneNumbers(phone);
            smsEntity.setSignName("Hyperfit健身");//设置签名（阿里云控制台通过审核的）
            smsEntity.setTemplateCode("SMS_111580376");//设置短信模版
            smsEntity.setTemplateParam("{\"code\":\"" + smsCode + "\"}");
            smsEntity.setOutId("");
            SendSmsResponse response = SmsUtil.sendSms(smsEntity);
            if ("OK".equals(response.getCode())) {
                result.setCode("0");
                result.setMsg("发送短信成功");
            } else {
                result.setCode("1");
                result.setMsg("短信发送失败");
                log.info("发送短信失败：" + response.getCode() + ":" + response.getMessage() + "。请求id:" + response.getRequestId() + ",回执ID:" + response.getBizId());
            }
        } catch (Exception e) {
            result.setCode("1");
            result.setMsg("发送短信失败：系统异常");
            log.error("发送短信失败：", e);
            return result;
        }
        return result;
    }
}