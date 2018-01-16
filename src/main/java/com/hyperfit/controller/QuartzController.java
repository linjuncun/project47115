package com.hyperfit.controller;

import com.alibaba.fastjson.JSON;
import com.hyperfit.entity.TCourse;
import com.hyperfit.entity.TOrder;
import com.hyperfit.service.CourseService;
import com.hyperfit.service.OrderService;
import com.hyperfit.service.PayService;
import com.hyperfit.service.SystemService;
import com.hyperfit.util.ApiModel;
import com.hyperfit.util.DateUtil;
import com.hyperfit.util.PageEntity;
import com.hyperfit.util.wechat.TemplateData;
import com.hyperfit.util.wechat.WeChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * <p> 定时任务控制器 </p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/12/13 22:09
 */
@Controller
@RequestMapping("/quartz")
public class QuartzController extends BaseController {


    @Resource(name = "courseServiceImpl")
    private CourseService courseService;

    @Resource(name = "payServiceImpl")
    private PayService payService;

    @Resource(name = "orderServiceImpl")
    private OrderService orderService;

    @Resource(name = "systemServiceImpl")
    private SystemService systemService;

    @Autowired
    private WeChatMessage weChatMessage;

    /**
     * 微信公众号appid
     */
    @Value("#{configProperties['app.id']}")
    private String appid;
    /**
     * 服务器域名
     */
    @Value("#{configProperties['server.domain']}")
    private String domain;

    /**
     * 日常检查任务
     */
    @RequestMapping("checkCourse.do")
    @ResponseBody
    public ApiModel checkCourse() {
        doCardCheck();
        return doCheck();
    }

    //每30分钟检查截止预约的课程中是否达到了最少人数，未达到，则进行退课处理
    public ApiModel doCheck() {
        ApiModel apiModel = new ApiModel();
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("queryType", 4);//查询截止预约且未上课的课程
            map.put("status", 1);
            List<TCourse> list = courseService.getCourses(map);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getOrderNum() > 0) {
                    //查出预约该课程/活动的所有订单
                    PageEntity pageEntity = new PageEntity();
                    Map<String, Object> temp = new HashMap<>();
                    temp.put("courseId", list.get(i).getCourseId());
                    temp.put("status", 3);
                    pageEntity.setPageSize(99999);
                    pageEntity.setMap(temp);
                    List<TOrder> orders = orderService.getOrderList(pageEntity);
                    if (list.get(i).getType() == 1) {//课程
                        String template_id = "yVr6ODvNTpfijcx-UQVLquTCc-IfoOBEdso15ExhN_o";//默认退课模版
                        String first = "退课通知";
                        String keyword1 = "未到最少上课人数";
                        String keyword2 = list.get(i).getCourseName() + "(教练:" + list.get(i).getName() + ")";
                        String keyword3 = DateUtil.toString(list.get(i).getClassHour(), "yyyy-MM-dd HH:mm");
                        String remark = "退款与优惠券按原路退还";
                        String url = "";
                        if (list.get(i).getOrderNum() >= list.get(i).getMinNum()) {
                            //上课通知
                            template_id = "k7Nlbiq903TwsKxbe6N320uchSkNW_At7tuBdWclZgw";
                            first = "开课通知";
                            keyword1 = keyword2;
                            keyword2 = list.get(i).getOrderNum() + "人";
                            remark = "";
                            url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid + "&redirect_uri=http%3A%2F%2F" + domain + "%2F%23%2ForderClassInfo%3FcourseId%3D" + list.get(i).getCourseId() + "&response_type=code&scope=snsapi_userinfo#wechat_redirect";
                        }
                        for (int j = 0; j < orders.size(); j++) {
                            String keyword2_user = list.get(i).getCourseName() + "(教练:" + list.get(i).getName() + "),退款金额" + orders.get(j).getAmount() + "元";
                            if (list.get(i).getOrderNum() < list.get(i).getMinNum()) {
                                //退课
                                temp.put("order", orders.get(j));
                                payService.dropCourse(temp);
                            } else {
                                keyword2_user = list.get(i).getOrderNum() + "人";
                            }
                            // 微信通知用户
                            TreeMap<String, TreeMap<String, String>> params = new TreeMap<>();
                            params.put("first", TemplateData.item(first, "#173177"));
                            params.put("keyword1", TemplateData.item(keyword1, "#173177"));
                            params.put("keyword2", TemplateData.item(keyword2_user, "#173177"));
                            params.put("keyword3", TemplateData.item(keyword3, "#173177"));
                            params.put("remark", TemplateData.item(remark, ""));
                            TemplateData template = new TemplateData();
                            template.setTemplate_id(template_id);
                            template.setTouser(orders.get(j).getOpenid());
                            template.setUrl(url);
                            template.setData(params);
                            String jsonMsg = JSON.toJSONString(template);
                            weChatMessage.sendTemplateMessage(jsonMsg);
                        }
                        // 微信通知教练
                        TreeMap<String, TreeMap<String, String>> paramsA = new TreeMap<>();
                        paramsA.put("first", TemplateData.item(first, "#173177"));
                        paramsA.put("keyword1", TemplateData.item(keyword1, "#173177"));
                        paramsA.put("keyword2", TemplateData.item(keyword2, "#173177"));
                        paramsA.put("keyword3", TemplateData.item(keyword3, "#173177"));
                        paramsA.put("remark", TemplateData.item("", ""));
                        TemplateData templateA = new TemplateData();
                        templateA.setTemplate_id(template_id);
                        templateA.setTouser(list.get(i).getOpenid());
                        templateA.setUrl(url);
                        templateA.setData(paramsA);
                        String jsonMsgA = JSON.toJSONString(templateA);
                        weChatMessage.sendTemplateMessage(jsonMsgA);
                    } else {
                        //活动开始通知
                        String template_id = "DgFIZu3nRKjdsZoSiBeNvVpb_-MxCF0eqD5CeOYhG2c";
                        String first = "活动开始通知";
                        String keyword1 = list.get(i).getCourseName() + "(教练:" + list.get(i).getName() + "),人数" + list.get(i).getOrderNum();
                        String keyword2 = DateUtil.toString(list.get(i).getClassHour(), "yyyy-MM-dd HH:mm");
                        String remark = "";
                        String url = "";
                        TreeMap<String, TreeMap<String, String>> params = new TreeMap<>();
                        params.put("first", TemplateData.item(first, "#173177"));
                        params.put("keyword1", TemplateData.item(keyword1, "#173177"));
                        params.put("keyword2", TemplateData.item(keyword2, "#173177"));
                        params.put("remark", TemplateData.item(remark, ""));
                        TemplateData template = new TemplateData();
                        template.setTemplate_id(template_id);
                        template.setUrl(url);
                        template.setData(params);
                        for (int j = 0; j < orders.size(); j++) {
                            // 微信通知用户
                            template.setTouser(orders.get(j).getOpenid());
                            String jsonMsg = JSON.toJSONString(template);
                            weChatMessage.sendTemplateMessage(jsonMsg);
                        }
                        // 微信通知教练
                        template.setTouser(list.get(i).getOpenid());
                        String jsonMsgA = JSON.toJSONString(template);
                        weChatMessage.sendTemplateMessage(jsonMsgA);
                    }
                }
            }
            apiModel.setCode("0");
            apiModel.setMsg("检查成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常");
            log.error("检查任务异常：", e);
        }
        return apiModel;
    }

    //每1小时检查过期的卡，并将余额清零
    public ApiModel doCardCheck() {
        ApiModel apiModel = new ApiModel();
        try {
            systemService.doCardCheck();
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常");
            log.error("检查过期卡异常：", e);
        }
        return apiModel;
    }

}
