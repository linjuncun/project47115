package com.hyperfit.util.wechat;

import com.hyperfit.util.ApiModel;
import com.hyperfit.util.SignUtil;
import com.hyperfit.util.StringUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class WeChatPayUtil {

    private final static Log log = LogFactory.getLog(WeChatPayUtil.class);

    public static String unifiedOrder(WxPaySendData data, String key, String url) {
        // 统一下单支付
        String result = null;
        try {
            // 生成sign签名
            SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
            parameters.put("appid", data.getAppid());
            parameters.put("attach", data.getAttach());
            parameters.put("body", data.getBody());
            parameters.put("mch_id", data.getMch_id());
            parameters.put("nonce_str", data.getNonce_str());
            parameters.put("notify_url", data.getNotify_url());
            parameters.put("out_trade_no", data.getOut_trade_no());
            parameters.put("total_fee", data.getTotal_fee());
            parameters.put("trade_type", data.getTrade_type());
            parameters.put("spbill_create_ip", data.getSpbill_create_ip());
            parameters.put("openid", data.getOpenid());
            parameters.put("device_info", data.getDevice_info());
            parameters.put("detail", data.getDetail());
            data.setSign(SignUtil.wxPaySign(parameters, key).toUpperCase());
            XStream xs = new XStream(new DomDriver("UTF-8", new XmlFriendlyReplacer("-_", "_")));
            xs.alias("xml", WxPaySendData.class);
            String xml = xs.toXML(data);
            log.info("统一下单xml参数为:\n" + xml);
            //发起请求
            URL orderUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) orderUrl.openConnection();
            conn.setConnectTimeout(30000); // 设置连接主机超时（单位：毫秒)
            conn.setReadTimeout(30000); // 设置从主机读取数据超时（单位：毫秒)
            conn.setDoOutput(true); // post请求参数要放在http正文内，顾设置成true，默认是false
            conn.setDoInput(true); // 设置是否从httpUrlConnection读入，默认情况下是true
            conn.setUseCaches(false); // Post 请求不能使用缓存
            // 设定传送的内容类型是可序列化的java对象(如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");// 设定请求的方法为"POST"，默认是GET
            conn.setRequestProperty("Content-Length", xml.length() + "");
            String encode = "utf-8";
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), encode);
            out.write(xml.toString());
            out.flush();
            out.close();
            result = getOut(conn);
            log.info("返回结果:" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getOut(HttpURLConnection conn) throws IOException {
        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            return null;
        }
        // 获取响应内容体
        BufferedReader in = new BufferedReader(new InputStreamReader(
                conn.getInputStream(), "UTF-8"));
        String line = "";
        StringBuffer strBuf = new StringBuffer();
        while ((line = in.readLine()) != null) {
            strBuf.append(line).append("\n");
        }
        in.close();
        return strBuf.toString().trim();
    }


    /**
     * 处理调用微信统一下单接口返回的xml数据，并生成相应的预付单对象（用于前台支付）
     *
     * @param apiModel
     */
    public static void handleReturnXml(Map<String, String> param, ApiModel apiModel) {
        log.info("解析微信统一下单接口返回xml数据");
        Map<String, String> map = new HashMap<String, String>();
        try {
            Document document = DocumentHelper.parseText(param.get("returnXml"));
            Element root = document.getRootElement();
            List<Element> elementList = root.elements();
            for (Element e : elementList) {
                map.put(e.getName(), e.getText());
            }
        } catch (DocumentException e1) {
            e1.printStackTrace();
        }
        String returnCode = map.get("return_code");//微信统一下单返回状态码
        String returnMsg = map.get("return_msg");//微信统一下单返回信息
        String resultCode = map.get("result_code");//微信统一下单业务结果
        String errCode = map.get("err_code");//微信统一下单业务结果错误代码
        String errCodeDes = map.get("err_code_des");//微信统一下单业务结果错误代码描述
        String prepayId = map.get("prepay_id");//预支付交易会话标识,效期为2小时
        if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {
            //返回前台预付单信息
            SortedMap<Object, Object> jsonMap = new TreeMap<Object, Object>();
            jsonMap.put("appId", param.get("appid"));
            jsonMap.put("timeStamp", System.currentTimeMillis() / 1000 + "");
            jsonMap.put("nonceStr", StringUtil.getRandomStr(26));
            jsonMap.put("package", "prepay_id=" + prepayId);
            jsonMap.put("signType", "MD5");
            String paySign = SignUtil.wxPaySign(jsonMap, param.get("apikey"));
            jsonMap.put("paySign", paySign);
            jsonMap.put("orderId", param.get("orderId"));
            apiModel.setData(jsonMap);
            apiModel.setCode("0");
            apiModel.setMsg("调用微信统一下单接口成功");
            log.info("解析xml数据成功");
        } else if ("FAIL".equals(returnCode)) {
            apiModel.setCode("1");
            apiModel.setMsg("调用微信统一下单接口失败:" + returnMsg);
            log.error("调用微信统一下单接口失败:" + returnMsg);
        } else if ("FAIL".equals(resultCode)) {
            apiModel.setCode("1");
            apiModel.setMsg("下单失败，errCode:" + errCode + "," + errCodeDes);
            log.error("调用微信统一下单接口:业务失败，errCode:" + errCode + "，errCodeDes:" + errCodeDes);
        }
    }

    /**
     * 获取微信回调请求map
     *
     * @param request
     * @return
     */
    public static SortedMap<Object, Object> getRequestMap(HttpServletRequest request) {
        SortedMap<Object, Object> reqMap = new TreeMap<Object, Object>();
        try {
            InputStream inStream = request.getInputStream();
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            outSteam.close();
            inStream.close();
            String resultStr = new String(outSteam.toByteArray(), "utf-8");

            Document document = DocumentHelper.parseText(resultStr);
            Element root = document.getRootElement();
            List<Element> elementList = root.elements();
            for (Element e : elementList) {
                reqMap.put(e.getName(), e.getText());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e1) {
            e1.printStackTrace();
        }
        return reqMap;
    }


}
