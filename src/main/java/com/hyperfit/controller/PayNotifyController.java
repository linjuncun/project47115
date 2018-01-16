package com.hyperfit.controller;

import com.hyperfit.service.PayService;
import com.hyperfit.util.SignUtil;
import com.hyperfit.util.StringUtil;
import com.hyperfit.util.wechat.AESUtil;
import com.hyperfit.util.wechat.HttpXmlUtils;
import com.hyperfit.util.wechat.WeChatPayUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.security.Security;
import java.util.Map;
import java.util.SortedMap;

/**
 * <p> 微信支付回调控制器 </p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/12/13 17:16
 */
@Controller
@RequestMapping("/notify")
public class PayNotifyController extends BaseController {

    /**
     * 微信支付API密钥
     */
    @Value("#{configProperties['app.apikey']}")
    private String apikey;

    @Resource(name = "payServiceImpl")
    private PayService payService;

    /**
     * 订单支付成功-微信回调处理
     */
    @RequestMapping("paySuccess.do")
    public void paySuccess(HttpServletRequest request, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            SortedMap<Object, Object> reqMap = WeChatPayUtil.getRequestMap(request);
            String returnCode = (String) reqMap.get("return_code");
            String resultCode = (String) reqMap.get("result_code");
            String returnSign = (String) reqMap.get("sign");
            if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {
                // 签名验证
                String localSign = SignUtil.wxPaySign(reqMap, apikey).toUpperCase();
                if (localSign.equals(returnSign)) {
                    log.info("微信支付回调--签名验证成功");
                    out.write(StringUtil.setXML("SUCCESS", ""));
                    log.info("微信支付回调--回复微信成功");
                    // 更新相关数据
                    payService.updatePayData(reqMap);
                    log.info("微信支付回调--业务逻辑处理成功");
                } else {
                    out.write(StringUtil.setXML("FAIL", "签名失败"));
                    log.error("微信支付回调--签名验证失败");
                }
            } else {
                log.error("微信支付失败");
            }
        } catch (Exception e) {
            log.error("微信支付回调处理异常：", e);
        } finally {
            out.close();
            out = null;
        }
    }

    /**
     * 微信退款结果回调通知
     */
    @RequestMapping("refund.do")
    public void refund(HttpServletRequest request, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            SortedMap<Object, Object> reqMap = WeChatPayUtil.getRequestMap(request);
            //获取加密信息
            Security.addProvider(new BouncyCastleProvider());
            String reqInfo = reqMap.get("req_info").toString();
            String result = AESUtil.decryptData(reqInfo, apikey);
            Map<String, String> resultMaps = HttpXmlUtils.parseRefundNotifyXml(result);
            out.write(StringUtil.setXML("SUCCESS", ""));
            payService.updateRefundData(resultMaps);
        } catch (Exception e) {
            log.error("退款结果回调处理异常：", e);
        } finally {
            out.close();
            out = null;
        }
    }

}
