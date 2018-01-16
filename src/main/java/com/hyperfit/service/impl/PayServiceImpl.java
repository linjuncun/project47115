package com.hyperfit.service.impl;

import com.alibaba.fastjson.JSON;
import com.hyperfit.dao.*;
import com.hyperfit.entity.*;
import com.hyperfit.service.PayService;
import com.hyperfit.util.ApiModel;
import com.hyperfit.util.DateUtil;
import com.hyperfit.util.SignUtil;
import com.hyperfit.util.StringUtil;
import com.hyperfit.util.wechat.TemplateData;
import com.hyperfit.util.wechat.WeChatMessage;
import com.hyperfit.util.wechat.WeChatPayUtil;
import com.hyperfit.util.wechat.WxPaySendData;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.*;


@Service("payServiceImpl")
public class PayServiceImpl implements PayService {

    public Logger log = LogManager.getLogger(PayServiceImpl.class);

    /**
     * 微信公众号appid
     */
    @Value("#{configProperties['app.id']}")
    private String appid;
    /**
     * 微信支付商户号
     */
    @Value("#{configProperties['app.mchid']}")
    private String mchid;

    /**
     * 微信支付api密匙
     */
    @Value("#{configProperties['app.apikey']}")
    private String apikey;

    /**
     * 微信统一下单url
     */
    @Value("#{configProperties['pay.order.url']}")
    private String orderUrl;

    /**
     * 微信支付通知url
     */
    @Value("#{configProperties['pay.notify.url']}")
    private String notifyUrl;

    /**
     * 微信支付通知url
     */
    @Value("#{configProperties['pay.agency.notify.url']}")
    private String agencyNotifyUrl;

    @Value("#{configProperties['app.mch.cert.path']}")
    private String certPath;
    /**
     * 服务器域名
     */
    @Value("#{configProperties['server.domain']}")
    private String domain;

    @Resource(name = "tOrderMapperImpl")
    private TOrderMapper tOrderMapper;

    @Resource(name = "tClubCardMapperImpl")
    private TClubCardMapper tClubCardMapper;

    @Resource(name = "tUserMapperImpl")
    private TUserMapper tUserMapper;

    @Resource(name = "tBalanceItemMapperImpl")
    private TBalanceItemMapper tBalanceItemMapper;

    @Resource(name = "tCouponMapperImpl")
    private TCouponMapper tCouponMapper;

    @Resource(name = "tPackageMapperImpl")
    private TPackageMapper tPackageMapper;

    @Autowired
    private WeChatMessage weChatMessage;

    @Override
    public int order(TOrder order, ApiModel apiModel) throws Exception {
        int result = tOrderMapper.insertOrder(order);
        //微信通知参数
        String first = "您的课程支付成功通知";
        String keyword1 = "课程购买暂无订单号";
        String keyword2 = "购买课程";
        String keyword3 = "课程：" + order.getCourseName() + "，上课时间" + DateUtil.toString(order.getClassHour(), "yyyy-MM-dd HH:mm");
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid + "&redirect_uri=http%3A%2F%2F" + domain + "%2F%23%2ForderClassInfo%3FcourseId%3D" + order.getCourseId() + "&response_type=code&scope=snsapi_userinfo#wechat_redirect";
        if (order.getOrderType() == 2) {
            first = "恭喜加入我们的会员";
            keyword1 = "会员卡购买暂无订单号";
            keyword2 = "购买会员卡";
            if (order.getBuyCardType() == 1) {
                keyword3 = "会员储值卡，到帐" + order.getArrive() + "元";
            } else {
                keyword3 = "会员次卡，到帐" + order.getArrive() + "次";
            }
            url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid + "&redirect_uri=http%3A%2F%2F" + domain + "%2F%3Fadd%3Dtrue%23%2FmyClubCardInfo&response_type=code&scope=snsapi_userinfo#wechat_redirect";
        }
        TreeMap<String, TreeMap<String, String>> params = new TreeMap<>();
        params.put("first", TemplateData.item(first, "#173177"));
        params.put("keyword1", TemplateData.item(keyword1, "#173177"));
        params.put("keyword2", TemplateData.item(keyword2, "#173177"));
        params.put("keyword3", TemplateData.item(keyword3, "#173177"));
        params.put("keyword4", TemplateData.item(order.getAmount() + "元", "#173177"));
        params.put("keyword5", TemplateData.item(DateUtil.toString(new Date(), "yyyy-MM-dd HH:mm"), "#173177"));
        params.put("remark", TemplateData.item("", ""));
        TemplateData template = new TemplateData();
        template.setTemplate_id("mfYKFNc6iGEWfqJuSe2K1GJ0NBaky0D7j-g67AQmf4Q");
        template.setTouser(order.getOpenid());
        template.setUrl(url);
        template.setData(params);
        String jsonMsg = JSON.toJSONString(template);
        if (result > 0 && order.getAmount() > 0) {
            if (order.getPayType() == 1) {//微信支付
                // 生成前端支付参数
                WxPaySendData data = new WxPaySendData();
                data.setAppid(appid);
                data.setMch_id(mchid);
                data.setNonce_str(StringUtil.getRandomStr(26));
                data.setAttach("");//自定义参数值
                if (order.getOrderType() == 1) {
                    data.setBody("hyperfit-课程");
                } else {
                    data.setBody("hyperfit-会员卡");
                }
                String outTradeNo = order.getOrderId() + "_V2";//TODO 上线后缀改成online
                data.setOut_trade_no(outTradeNo);
                Integer orderAmount = order.getAmount() * 100;// 订单金额（分）
//                data.setTotal_fee(orderAmount.longValue());// 单位：分
                data.setTotal_fee(1);//TODO 上线取消上面一行注释
                data.setSpbill_create_ip(order.getIp());
                data.setNotify_url(notifyUrl);// 微信支付成功回调地址
                data.setTrade_type("JSAPI");
                data.setOpenid(order.getOpenid());
                String returnXml = WeChatPayUtil.unifiedOrder(data, apikey, orderUrl);

                // 处理返回结果，并封装预付单信息
                Map<String, String> map = new HashMap<>();
                map.put("appid", appid);
                map.put("apikey", apikey);
                map.put("mchid", mchid);
                map.put("orderId", order.getOrderId() + "");
                map.put("returnXml", returnXml);
                WeChatPayUtil.handleReturnXml(map, apiModel);
            } else if (order.getPayType() == 2) {
                //会员卡支付（包括储值和次卡）
                Map<String, Object> temp = new HashMap<>();
                temp.put("cardId", order.getCardId());
                TClubCard card = tClubCardMapper.getClubCardInfo(temp);
                //扣除会员卡余额
                TClubCard record = new TClubCard();//更新数据实体类
                record.setCardId(order.getCardId());
                //判断会员卡老套餐余额是否失效
                if (card.getOldDeadline() != null) {
                    //如果余额中还含有老套餐余额，并且老套餐已到失效日期，则将老余额扣除（失效）
                    int oldBalance = card.getBalance() - card.getPackageMoney();//老套餐余额
                    Date now = new Date();
                    if (oldBalance > 0 && card.getOldDeadline().compareTo(now) <= 0) {
                        card.setBalance(card.getPackageMoney());//扣除老套餐余额
                        //插入余额过期记录
                        TOrder orderRecord = new TOrder();
                        orderRecord.setOrderType(3);//订单类型 1：购买课程  2：购买会员卡套餐  3：会员卡余额过期
                        orderRecord.setOpenid(order.getOpenid());
                        orderRecord.setUserId(order.getUserId());
                        orderRecord.setAmount(oldBalance);
                        orderRecord.setCardId(order.getCardId());
                        orderRecord.setStatus(3);//订单状态 1：待付款 2：购买中 3：已完成 4：申请退款 5：已退款 6：退款异常
                        orderRecord.setCreateTime(card.getOldDeadline());//设置时间为老套餐过期时间
                        orderRecord.setUpdateTime(card.getOldDeadline());//设置时间为老套餐过期时间
                        tOrderMapper.insertOrder(orderRecord);
                    }
                }
                if (card.getCardType() == 1) {
                    record.setBalance(card.getBalance() - order.getAmount());
                } else {
                    record.setBalance(card.getBalance() - 1);
                }
                //更新返现活动已达成条件
                if (card.getIsReturnCash() == 1 && card.getReturnCash() == 0) {//有返现活动且还未返现
                    record.setMoneyUsed(card.getMoneyUsed() + order.getAmount());
                    record.setTimesUsed(card.getTimesUsed() + 1);
                    //判断是否达到了返现标准
                    List<TReturnCashCondition> conditions = card.getReturnCashCondition();//返现条件list
                    int days = DateUtil.daysBetween(card.getCreateTime(), new Date());//已经获得会员卡多少天了
                    boolean matchCondition = false;//是否满足返现条件
                    int returnCash = 0;//返现金额
                    Integer conditionId = 0;//返现条件id
                    for (int i = 0; i < conditions.size(); i++) {
                        if (days <= conditions.get(i).getDays()) {//在有效天数内
                            if ((conditions.get(i).getMoney() > 0 && record.getMoneyUsed() >= conditions.get(i).getMoney()) ||
                                    (conditions.get(i).getTimes() > 0 && record.getTimesUsed() >= conditions.get(i).getTimes())) {
                                matchCondition = true;
                                conditionId = conditions.get(i).getConditionId();
                                returnCash = conditions.get(i).getReturnCash();
                                break;
                            }
                        }
                    }
                    //返现
                    if (matchCondition) {
                        Map<String, Object> userMap = new HashMap<>();
                        TUser user = new TUser();
                        user.setUserId(card.getUserId());//返给主卡所在用户
                        user.setBalance(returnCash);//余额加上返现金额
                        userMap.put("record", user);
                        tUserMapper.updateUserInfo(userMap);
                        //插入返现记录
                        TBalanceItem item = new TBalanceItem();
                        item.setUserId(card.getUserId());
                        item.setItemType(1);//明细类型 1：返现 2：提现
                        item.setPackageId(card.getPackageId());
                        item.setConditionId(conditionId);
                        item.setAmount(returnCash);
                        tBalanceItemMapper.insertBalanceItem(item);
                        //更改会员卡返现状态
                        record.setReturnCash(1);//是否已返现 0：否 1：是 2: 已失效
                    }
                }
                //更新会员卡数据
                Map<String, Object> cardMap = new HashMap<>();
                cardMap.put("record", record);
                tClubCardMapper.updateClubCardInfo(cardMap);
                //更新优惠券状态为已使用
                updateUserCoupon(order.getUserCouponId(), 2);
                // 微信通知用户
                weChatMessage.sendTemplateMessage(jsonMsg);
                apiModel.setCode("2");
                apiModel.setMsg("购买成功");
            }
        } else if (result > 0 && order.getAmount() == 0) {//当优惠券面值比需支付的金额更大时
            //更新优惠券状态为已使用
            updateUserCoupon(order.getUserCouponId(), 2);
            // 微信通知用户
            weChatMessage.sendTemplateMessage(jsonMsg);
            apiModel.setCode("2");
            apiModel.setMsg("购买成功");
        } else {
            apiModel.setCode("1");
            apiModel.setMsg("生成订单失败");
            return 0;
        }
        return 0;
    }

    @Override
    public int dropCourse(Map<String, Object> param) {
        TOrder record = new TOrder();
        TOrder order = (TOrder) param.get("order");
        if (order.getPayType() == 1) {
            //微信支付
            wxRefund(order.getOrderId() + "", order.getAmount() * 100 + "", order.getAmount() * 100 + "", record);
            record.setStatus(4);//订单状态 1：待付款 2：购买中 3：已完成 4：申请退款 5：已退款 6：退款异常
        } else {
            //会员卡支付，如果该订单刚好返现，不需要退回返现的钱，因为返现后，可能用户已经提现
            Map<String, Object> cardMap = new HashMap<>();
            cardMap.put("cardId", order.getCardId());
            TClubCard cardInfo = tClubCardMapper.getClubCardInfo(cardMap);
            TClubCard card = new TClubCard();
            card.setCardId(order.getCardId());
            if (cardInfo.getCardType() == 1) {
                card.setBalance(cardInfo.getBalance() + order.getAmount());
            } else {
                card.setBalance(cardInfo.getBalance() + 1);
            }
            if (cardInfo.getReturnCash() == 0) {
                //未返现时需要减去已消费的数据
                card.setMoneyUsed(cardInfo.getMoneyUsed() - order.getAmount());
                card.setTimesUsed(cardInfo.getTimesUsed() - 1);
            }
            cardMap.put("record", card);
            tClubCardMapper.updateClubCardInfo(cardMap);
            record.setStatus(5);
            //如果使用了优惠券，更新优惠券状态为未使用
            updateUserCoupon(order.getUserCouponId(), 1);
        }
        //更新订单状态
        record.setOrderId(order.getOrderId());
        param.put("record", record);
        int result = tOrderMapper.updateOrderInfo(param);
        return result;
    }

    @Override
    public int updatePayData(SortedMap<Object, Object> reqMap) {
        int result = 1;
        String transactionId = (String) reqMap.get("transaction_id");// 微信支付订单号
        String out_trade_no = (String) reqMap.get("out_trade_no");
        String orderId = out_trade_no.split("_")[0];// 平台订单号
        String openid = (String) reqMap.get("openid");// 支付用户openid
        Map<String, Object> param = new HashMap<>();
        param.put("orderId", orderId);
        TOrder order = tOrderMapper.getOrderInfo(param);
        if (null == order) {
            log.error("微信支付回调异常--找不到订单");
            return 0;
        }
        //若是重复通知，不处理
        if (order.getStatus() > 1) {
            return 0;
        }
        //处理业务逻辑
        TOrder record = new TOrder();
        if (order.getOrderType() == 1) {
            //如果使用了优惠券，更新优惠券状态为已使用
            updateUserCoupon(order.getUserCouponId(), 2);
        } else if (order.getOrderType() == 2) {
            Map<String, Object> tempMap = new HashMap<>();
            //判断用户是购买新会员卡，还是充值会员卡
            tempMap.put("userId", order.getUserId());
            tempMap.put("status", 1);
            boolean isRecharge = false;
            Integer cardId = 0;
            Integer oldBalance = 0;//老套餐余额
            Date oldDeadline = new Date();
            List<TClubCard> cards = tClubCardMapper.getClubCardList(tempMap);
            for (int i = 0; i < cards.size(); i++) {
                if (order.getPackageId() == cards.get(i).getPackageId()) {
                    isRecharge = true;
                    cardId = cards.get(i).getCardId();
                    oldBalance = cards.get(i).getBalance();
                    oldDeadline = cards.get(i).getDeadline();
                }
            }
            tempMap.clear();
            tempMap.put("packageId", order.getPackageId());
            TPackage tPackage = tPackageMapper.getPackageInfo(tempMap);
            if (tPackage == null) {
                log.error("微信支付回调异常--找不到套餐");
                return 0;
            }
            if (isRecharge) {
                //更新会员卡
                TClubCard card = new TClubCard();
                card.setCardId(cardId);
                card.setBalance(tPackage.getArrive() + oldBalance);
                card.setPackageMoney(tPackage.getArrive());
                card.setMoneyUsed(0);
                card.setTimesUsed(0);
                card.setReturnCash(0);
                card.setSysUserId(order.getSysUserId());
                card.setCreateTime(new Date());
                card.setOldDeadline(oldDeadline);
                tempMap.clear();
                tempMap.put("record", card);
                result = tClubCardMapper.updateClubCardInfo(tempMap);
            } else {
                //新增会员卡
                TClubCard card = new TClubCard();
                card.setUserId(order.getUserId());
                card.setPackageId(order.getPackageId());
                card.setBalance(tPackage.getArrive());
                card.setPackageMoney(tPackage.getArrive());
                card.setSysUserId(order.getSysUserId());
                result = tClubCardMapper.insertClubCard(card);
            }
        }

        // 更新订单信息
        record.setOrderId(order.getOrderId());
        record.setApplyNo(transactionId);
        record.setStatus(3);
        param.put("record", record);
        tOrderMapper.updateOrderInfo(param);
        // 微信通知用户
        String first = "您的课程支付成功通知";
        String keyword1 = "课程购买暂无订单号";
        String keyword2 = "购买课程";
        String keyword3 = "课程：" + order.getCourseName() + "，上课时间" + DateUtil.toString(order.getClassHour(), "yyyy-MM-dd HH:mm");
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid + "&redirect_uri=http%3A%2F%2F" + domain + "%2F%23%2ForderClassInfo%3FcourseId%3D" + order.getCourseId() + "&response_type=code&scope=snsapi_userinfo#wechat_redirect";

        if (order.getOrderType() == 2) {
            first = "恭喜加入我们的会员";
            keyword1 = "会员卡购买暂无订单号";
            keyword2 = "购买会员卡";
            if (order.getBuyCardType() == 1) {
                keyword3 = "会员储值卡，到帐" + order.getArrive() + "元";
            } else {
                keyword3 = "会员次卡，到帐" + order.getArrive() + "次";
            }
            url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid + "&redirect_uri=http%3A%2F%2F" + domain + "%2F%3Fadd%3Dtrue%23%2FmyClubCardInfo&response_type=code&scope=snsapi_userinfo#wechat_redirect";
        }
        TreeMap<String, TreeMap<String, String>> params = new TreeMap<>();
        params.put("first", TemplateData.item(first, "#173177"));
        params.put("keyword1", TemplateData.item(keyword1, "#173177"));
        params.put("keyword2", TemplateData.item(keyword2, "#173177"));
        params.put("keyword3", TemplateData.item(keyword3, "#173177"));
        params.put("keyword4", TemplateData.item(order.getAmount() + "元", "#173177"));
        params.put("keyword5", TemplateData.item(DateUtil.toString(order.getCreateTime(), "yyyy-MM-dd HH:mm"), "#173177"));
        params.put("remark", TemplateData.item("", ""));
        TemplateData template = new TemplateData();
        template.setTemplate_id("mfYKFNc6iGEWfqJuSe2K1GJ0NBaky0D7j-g67AQmf4Q");
        template.setTouser(openid);
        template.setUrl(url);
        template.setData(params);
        String jsonMsg = JSON.toJSONString(template);
        weChatMessage.sendTemplateMessage(jsonMsg);
        return result;
    }

    @Override
    public int updateRefundData(Map<String, String> resultMaps) {
        TOrder record = new TOrder();
        String out_trade_no = resultMaps.get("out_trade_no");
        String orderId = out_trade_no.split("_")[0];// 平台订单号
        String refundStatus = resultMaps.get("refund_status");//退款状态
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);
        TOrder order = tOrderMapper.getOrderInfo(map);
        //若是重复通知，不处理
        if (order == null || order.getStatus() == 5 || order.getStatus() == 6) {
            return 0;
        }
        if ("SUCCESS".equals(refundStatus)) {
            //退款成功
            //如果使用了优惠券，更新优惠券状态为未使用
            updateUserCoupon(order.getUserCouponId(), 1);
            record.setStatus(5);
        } else if ("CHANGE".equals(refundStatus)) {
            //退款异常
            record.setStatus(6);
        } else if ("REFUNDCLOSE".equals(refundStatus)) {
            //退款关闭
            record.setStatus(6);
        }
        // 更新订单信息
        record.setOrderId(order.getOrderId());
        map.put("record", record);
        tOrderMapper.updateOrderInfo(map);
        return 1;
    }

    /**
     * 更新优惠券状态
     */
    private void updateUserCoupon(Integer userCouponId, int status) {
        if (userCouponId != null && userCouponId != 0) {
            Map<String, Object> couponMap = new HashMap<>();
            TCoupon coupon = new TCoupon();
            coupon.setUserCouponId(userCouponId);
            coupon.setStatus(status);//用户优惠券状态  1：未使用 2：已使用
            couponMap.put("record", coupon);
            tCouponMapper.updateUserCoupon(couponMap);
        }
    }

    /**
     * 微信公众号申请退款
     *
     * @param orderId    订单id
     * @param total_fee  订单金额
     * @param refund_fee 退款金额
     * @param record     更新订单实体
     * @return
     */
    public boolean wxRefund(String orderId, String total_fee, String refund_fee, TOrder record) {
        log.info("申请退款开始：total_fee=" + total_fee + ",orderId=" + orderId + ",refund_fee=" + refund_fee);
        boolean success = true;
        String outTradeNo = orderId + "_V2";//TODO 上线后缀改成online
        refund_fee = "1";//TODO 上线后删除
        total_fee = "1";//TODO 上线后删除
        String refundid = orderId + "_refund";// 退款单号
        String nonce_str = StringUtil.getRandomStr(26);

		/*-----  1.生成预支付订单需要的的package数据-----*/
        SortedMap<Object, Object> packageParams = new TreeMap<>();
        packageParams.put("appid", appid);
        packageParams.put("mch_id", mchid);
        packageParams.put("nonce_str", nonce_str);
        packageParams.put("out_trade_no", outTradeNo);
        packageParams.put("out_refund_no", refundid);
        packageParams.put("refund_fee", refund_fee);
        packageParams.put("total_fee", total_fee);
        /*----2.根据package生成签名sign---- */
        String sign = SignUtil.wxPaySign(packageParams, apikey).toUpperCase();

		/*----3.拼装需要提交到微信的数据xml---- */
        String xml = "<xml>" + "<appid>" + appid + "</appid>" + "<mch_id>" + mchid + "</mch_id>" + "<nonce_str>"
                + nonce_str + "</nonce_str>" + "<out_trade_no>" + outTradeNo + "</out_trade_no>" + "<out_refund_no>"
                + refundid + "</out_refund_no>" + "<refund_fee>" + refund_fee + "</refund_fee>" + "<total_fee>"
                + total_fee + "</total_fee>" + "<sign>" + sign + "</sign>" + "</xml>";
        /*----4.读取证书文件 ---- */
        // 指定读取证书格式为PKCS12
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            // 读取本机存放的PKCS12证书文件
            FileInputStream instream = new FileInputStream(new File(certPath));
            // 指定PKCS12的密码(商户ID)
            keyStore.load(instream, mchid.toCharArray());
            instream.close();
            SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, mchid.toCharArray()).build();
            // 指定TLS版本
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"},
                    null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            // 设置httpclient的SSLSocketFactory
            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

			/*----5.发送数据到微信的退款接口---- */
            String url = "https://api.mch.weixin.qq.com/secapi/pay/refund";
            HttpPost httpost = new HttpPost(url);

            httpost.setEntity(new StringEntity(xml, "UTF-8"));
            HttpResponse weixinResponse = httpClient.execute(httpost);
            String jsonStr = EntityUtils.toString(weixinResponse.getEntity(), "UTF-8");
            log.info("微信退款接口返回：" + jsonStr);
            Map<String, String> map = new HashMap<>();
            Document document = DocumentHelper.parseText(jsonStr);
            Element root = document.getRootElement();
            List<Element> elementList = root.elements();
            for (Element e : elementList) {
                map.put(e.getName(), e.getText());
            }
            String return_code = map.get("return_code");//
            String result_code = map.get("result_code");// 业务结果
            String err_code = map.get("err_code");// 错误代码
            String err_code_des = map.get("err_code_des");// 错误代码描述

            if ("SUCCESS".equalsIgnoreCase(return_code) && "SUCCESS".equalsIgnoreCase(result_code)) {
                log.info("订单：" + orderId + "，微信退款申请成功");
                record.setRemark("微信退款申请成功");
            } else {
                success = false;
                log.info("订单：" + orderId + "，自动退款失败，err_code=" + err_code + ",err_code_des=" + err_code_des);
                record.setRemark("自动退款失败，err_code=" + err_code + ",err_code_des=" + err_code_des);
            }
        } catch (Exception e) {
            success = false;
            log.error("订单：" + orderId + "，自动退款异常：" + e);
            record.setRemark("自动退款异常");
        }
        return success;
    }

}
