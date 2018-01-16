package com.hyperfit.controller;

import com.alibaba.fastjson.JSON;
import com.hyperfit.entity.*;
import com.hyperfit.service.*;
import com.hyperfit.util.ApiModel;
import com.hyperfit.util.DateUtil;
import com.hyperfit.util.PageEntity;
import com.hyperfit.util.wechat.TemplateData;
import com.hyperfit.util.wechat.WeChatMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * <p> 支付控制器 </p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/11/23 15:47
 */
@Controller
@RequestMapping("/pay")
public class PayController extends BaseController {

    @Resource(name = "courseServiceImpl")
    private CourseService courseService;

    @Resource(name = "couponServiceImpl")
    private CouponService couponService;

    @Resource(name = "payServiceImpl")
    private PayService payService;

    @Resource(name = "packageServiceImpl")
    private PackageService packageService;

    @Resource(name = "orderServiceImpl")
    private OrderService orderService;

    @Autowired
    private WeChatMessage weChatMessage;

    /**
     * 微信端-购买课程
     *
     * @param payType 支付方式 1：微信支付 2：会员卡支付（包括储值和次卡）
     */
    @RequestMapping("buyCourse.do")
    @ResponseBody
    public ApiModel buyCourse(HttpServletRequest request, String openid, String courseId,
                              String payType, String userCouponId, String cardId) {
        ApiModel apiModel = new ApiModel();
        TOrder order = new TOrder();//订单实体
        try {
            if (StringUtils.isAnyBlank(courseId, payType)) {
                apiModel.setCode("1");
                apiModel.setMsg("参数错误");
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            TUser user = new TUser();
            if (!checkUser(apiModel, openid, user)) {
                return apiModel;
            }
            //验证是否绑定手机号
            if (StringUtils.isBlank(user.getPhone())) {
                apiModel.setCode("-3");
                apiModel.setMsg("未绑定手机号");
                return apiModel;
            }
            //判断课程是否可购买
            map.put("queryUserId", user.getUserId());
            map.put("courseId", courseId);
            TCourse course = courseService.getCourseInfo(map);
            if (course == null) {
                apiModel.setCode("1");
                apiModel.setMsg("未找到课程");
                return apiModel;
            }
            if (course.getStatus() == 2) {
                apiModel.setCode("1");
                apiModel.setMsg("该课程已关闭，请选择其他课程");
                return apiModel;
            }
            if (course.getIsOrder() == 1) {
                apiModel.setCode("1");
                apiModel.setMsg("您已预约了此课程，请选择其他课程");
                return apiModel;
            }
            if (course.getStopOrder() == 1) {
                apiModel.setCode("1");
                apiModel.setMsg("本课程已停止预约，请选择其他课程");
                return apiModel;
            }
            //使用优惠券时判断优惠券是否过期
            Integer couponValue = 0;//优惠券面值
            if (StringUtils.isNotBlank(userCouponId)) {
                PageEntity param = new PageEntity();
                Map<String, Object> temp = new HashMap<>();
                temp.put("userId", user.getUserId());
                temp.put("userCouponId", userCouponId);
                param.setMap(temp);
                List<TCoupon> list = couponService.getUserCoupon(param);
                if (list.size() == 0) {
                    apiModel.setCode("1");
                    apiModel.setMsg("优惠券不存在");
                    return apiModel;
                } else {
                    Date now = new Date();
                    if (list.get(0).getDeadline().compareTo(now) >= 0) {
                        couponValue = list.get(0).getCouponValue();
                    } else {
                        apiModel.setCode("1");
                        apiModel.setMsg("优惠券已过期");
                        return apiModel;
                    }
                }
            }
            //会员卡支付
            if ("2".equals(payType)) {
                if (StringUtils.isBlank(cardId)) {
                    apiModel.setCode("1");
                    apiModel.setMsg("参数错误");
                    return apiModel;
                }
                Map<String, Object> temp = new HashMap<>();
                temp.put("cardId", cardId);
                TClubCard card = userService.getClubCardInfo(temp);
                //判断是否支持本门店本课程消费
                if (!"0".equals(card.getGymIds())) {
                    boolean flag = false;//是否支持本门店
                    String[] gymIds = card.getGymIds().split(",");
                    for (String id : gymIds) {
                        if ((course.getGymId() + "").equals(id)) {
                            flag = true;
                            break;
                        }
                        if (course.getType() == 2 && !"0".equals(course.getGymIds())) {
                            //判断会员卡是否支持参与活动的门店
                            String[] gyms = course.getGymIds().split(",");//参与该活动课程的门店Id数组
                            for (String gym : gyms) {
                                if (gym.equals(id)) {
                                    flag = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!flag) {
                        apiModel.setCode("1");
                        if (course.getType() == 1) {
                            apiModel.setMsg("抱歉，您的会员卡不支持本店消费");
                        } else {
                            apiModel.setMsg("抱歉，您的会员卡不支持本活动");
                        }
                        return apiModel;
                    }
                }
                //判断是否支持该课程（活动课程不用判断）
                if (!"0".equals(card.getCourseIds()) && course.getType() == 1) {
                    boolean flag = false;//是否支持本课程
                    String[] courseIds = card.getCourseIds().split(",");
                    for (String id : courseIds) {
                        Map<String, Object> courseMap = new HashMap<>();
                        courseMap.put("courseId", id);
                        TCourse check = courseService.getCourseInfo(courseMap);
                        if (course.getCourseName().equals(check.getCourseName())) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        apiModel.setCode("1");
                        apiModel.setMsg("抱歉，您的会员卡不支持本课程消费");
                        return apiModel;
                    }
                }
                //判断余额是否足够本次消费
                if (card.getCardType() == 1 && (card.getBalance() - (course.getPrice() - couponValue) < 0)) {
                    apiModel.setCode("1");
                    apiModel.setMsg("会员卡余额不足");
                    return apiModel;
                } else if (card.getCardType() == 2 && card.getBalance() == 0) {
                    apiModel.setCode("1");
                    apiModel.setMsg("会员卡次数不足");
                    return apiModel;
                }
                //会员卡支付，直接将订单状态设置成已完成
                order.setStatus(3);
                order.setCardId(Integer.parseInt(cardId));//设置使用会员卡id
            }
            //下单
            order.setIp(request.getRemoteAddr());
            order.setOrderType(1);//订单类型 1：购买课程  2：购买会员卡套餐  3：会员卡余额过期
            order.setOpenid(openid);
            order.setUserId(user.getUserId());
            order.setCourseId(Integer.parseInt(courseId));
            if (course.getPrice() > couponValue) {
                order.setAmount(course.getPrice() - couponValue);
            } else {
                order.setAmount(0);
            }
            if (StringUtils.isNotBlank(userCouponId)) {
                order.setUserCouponId(Integer.parseInt(userCouponId));
            }
            order.setPayType(Integer.parseInt(payType));//支付方式 1：微信支付 2：会员卡支付（包括储值和次卡）
            order.setCourseName(course.getCourseName());//用于微信通知
            order.setClassHour(course.getClassHour());//用于微信通知

            payService.order(order, apiModel);
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("购买课程异常：" + e);
        }
        return apiModel;
    }


    /**
     * 微信端-购买会员卡套餐
     */
    @RequestMapping("buyPackage.do")
    @ResponseBody
    public ApiModel buyPackage(HttpServletRequest request, String openid, String packageId,
                               String sysUserId, String userCouponId) {
        ApiModel apiModel = new ApiModel();
        TOrder order = new TOrder();//订单实体
        try {
            if (StringUtils.isAnyBlank(packageId)) {
                apiModel.setCode("1");
                apiModel.setMsg("参数错误");
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            TUser user = new TUser();
            if (!checkUser(apiModel, openid, user)) {
                return apiModel;
            }
            //验证是否绑定手机号
            if (StringUtils.isBlank(user.getPhone())) {
                apiModel.setCode("-3");
                apiModel.setMsg("未绑定手机号");
                return apiModel;
            }
            //判断套餐是否可购买
            map.put("packageId", packageId);
            TPackage packageInfo = packageService.getPackageInfo(map);
            if (packageInfo == null) {
                apiModel.setCode("1");
                apiModel.setMsg("未找到套餐");
                return apiModel;
            }
            if (packageInfo.getStatus() == 2) {
                apiModel.setCode("1");
                apiModel.setMsg("该套餐已下线，请选择其他套餐");
                return apiModel;
            }
            //使用优惠券时判断优惠券是否过期
            Integer couponValue = 0;//优惠券面值
            if (StringUtils.isNotBlank(userCouponId)) {
                PageEntity param = new PageEntity();
                Map<String, Object> temp = new HashMap<>();
                temp.put("userId", user.getUserId());
                temp.put("userCouponId", userCouponId);
                param.setMap(temp);
                List<TCoupon> list = couponService.getUserCoupon(param);
                if (list.size() == 0) {
                    apiModel.setCode("1");
                    apiModel.setMsg("优惠券不存在");
                    return apiModel;
                } else {
                    Date now = new Date();
                    if (list.get(0).getDeadline().compareTo(now) >= 0) {
                        couponValue = list.get(0).getCouponValue();
                    } else {
                        apiModel.setCode("1");
                        apiModel.setMsg("优惠券已过期");
                        return apiModel;
                    }
                }
            }

            //下单
            order.setIp(request.getRemoteAddr());
            order.setOrderType(2);//订单类型 1：购买课程  2：购买会员卡套餐  3：会员卡余额过期
            order.setOpenid(openid);
            order.setUserId(user.getUserId());
            order.setPackageId(Integer.parseInt(packageId));
            if (packageInfo.getRecharge() > couponValue) {
                order.setAmount(packageInfo.getRecharge() - couponValue);
            } else {
                order.setAmount(0);
            }
            if (StringUtils.isNotBlank(userCouponId)) {
                order.setUserCouponId(Integer.parseInt(userCouponId));
            }
            if (StringUtils.isNotBlank(sysUserId)) {
                order.setSysUserId(Integer.parseInt(sysUserId));
            }
            order.setPayType(1);//支付方式 1：微信支付 2：会员卡支付（包括储值和次卡）
            order.setStatus(1);//订单状态 1：待付款 2：购买中 3：已完成 4：申请退款 5：已退款 6：退款异常
            order.setBuyCardType(packageInfo.getCardType());//用于微信通知

            payService.order(order, apiModel);
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("购买会员卡套餐异常：" + e);
        }
        return apiModel;
    }

    /**
     * 微信端-退课
     */
    @RequestMapping("dropCourse.do")
    @ResponseBody
    public ApiModel dropCourse(String openid, String orderId) {
        ApiModel apiModel = new ApiModel();
        try {
            if (StringUtils.isAnyBlank(orderId)) {
                apiModel.setCode("1");
                apiModel.setMsg("参数错误");
                return apiModel;
            }
            TUser user = new TUser();
            if (!checkUser(apiModel, openid, user)) {
                return apiModel;
            }
            if (StringUtils.isNotBlank(openid)) {
                //验证是否绑定手机号
                if (StringUtils.isBlank(user.getPhone())) {
                    apiModel.setCode("-3");
                    apiModel.setMsg("未绑定手机号");
                    return apiModel;
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put("orderId", orderId);
            TOrder order = orderService.getOrderInfo(map);
            if (order == null) {
                apiModel.setCode("1");
                apiModel.setMsg("未找到课程订单");
                return apiModel;
            }
            if (order.getStatus() == 4 || order.getStatus() == 5) {
                apiModel.setCode("1");
                apiModel.setMsg("课程已退订");
                return apiModel;
            }

            map.put("order", order);
            //检查是否是会员卡支付，然后判断是否过期，过期不允许退课
            if (order.getPayType() == 2) {
                Map<String, Object> temp = new HashMap<>();
                temp.put("cardId", order.getCardId());
                TClubCard cardInfo = userService.getClubCardInfo(temp);
                Date now = new Date();
                if (now.compareTo(cardInfo.getDeadline()) > 0) {
                    apiModel.setCode("1");
                    apiModel.setMsg("会员卡已过期，无法退课");
                    return apiModel;
                }
            }
            payService.dropCourse(map);
            // 微信通知用户
            Map<String, Object> temp = new HashMap<>();
            temp.put("courseId", order.getCourseId());
            TCourse check = courseService.getCourseInfo(temp);
            String template_id = "yVr6ODvNTpfijcx-UQVLquTCc-IfoOBEdso15ExhN_o";
            String first = "退课通知";
            String keyword1 = "已经退订本课程";
            String keyword2 = check.getCourseName() + "(教练:" + check.getName() + "),退款金额" + order.getAmount() + "元";
            String keyword3 = DateUtil.toString(check.getClassHour(), "yyyy-MM-dd HH:mm");
            String remark = "退款与优惠券按原路退还";
            TreeMap<String, TreeMap<String, String>> params = new TreeMap<>();
            params.put("first", TemplateData.item(first, "#173177"));
            params.put("keyword1", TemplateData.item(keyword1, "#173177"));
            params.put("keyword2", TemplateData.item(keyword2, "#173177"));
            params.put("keyword3", TemplateData.item(keyword3, "#173177"));
            params.put("remark", TemplateData.item(remark, ""));
            TemplateData template = new TemplateData();
            template.setTemplate_id(template_id);
            template.setTouser(order.getOpenid());
            template.setData(params);
            String jsonMsg = JSON.toJSONString(template);
            weChatMessage.sendTemplateMessage(jsonMsg);
            apiModel.setCode("0");
            apiModel.setMsg("退课成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("退课异常：" + e);
        }
        return apiModel;
    }

    /**
     * TODO 微信端-提现
     */
    @RequestMapping("withdrawMoney.do")
    @ResponseBody
    public ApiModel withdrawMoney(String openid, String amount) {
        ApiModel apiModel = new ApiModel();
        try {
            if (StringUtils.isAnyBlank(amount)) {
                apiModel.setCode("1");
                apiModel.setMsg("参数错误");
                return apiModel;
            }
            TUser user = new TUser();
            if (!checkUser(apiModel, openid, user)) {
                return apiModel;
            }
            //验证是否绑定手机号
            if (StringUtils.isBlank(user.getPhone())) {
                apiModel.setCode("-3");
                apiModel.setMsg("未绑定手机号");
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("amount", amount);
//            payService.dropCourse(map);
            apiModel.setCode("1");
            apiModel.setMsg("暂无提现功能");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("退课异常：" + e);
        }
        return apiModel;
    }


}
