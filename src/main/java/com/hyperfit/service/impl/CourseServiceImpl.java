package com.hyperfit.service.impl;

import com.alibaba.fastjson.JSON;
import com.hyperfit.dao.TCourseActionMapper;
import com.hyperfit.dao.TCourseMapper;
import com.hyperfit.dao.TOrderMapper;
import com.hyperfit.dao.TUserMapper;
import com.hyperfit.entity.*;
import com.hyperfit.service.CourseService;
import com.hyperfit.service.PayService;
import com.hyperfit.util.DateUtil;
import com.hyperfit.util.PageEntity;
import com.hyperfit.util.wechat.TemplateData;
import com.hyperfit.util.wechat.WeChatMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;


@Service("courseServiceImpl")
public class CourseServiceImpl implements CourseService {

    @Resource(name = "tCourseMapperImpl")
    private TCourseMapper tCourseMapper;

    @Resource(name = "tUserMapperImpl")
    private TUserMapper tUserMapper;

    @Resource(name = "tCourseActionMapperImpl")
    private TCourseActionMapper tCourseActionMapper;

    @Resource(name = "payServiceImpl")
    private PayService payService;

    @Resource(name = "tOrderMapperImpl")
    private TOrderMapper tOrderMapper;

    @Autowired
    private WeChatMessage weChatMessage;

    @Override
    public List<TCourse> getCourseList(PageEntity pageEntity) {
        return tCourseMapper.getCourseList(pageEntity);
    }

    @Override
    public List<TCourse> getCourses(Map<String, Object> param) {
        return tCourseMapper.getCourses(param);
    }

    @Override
    public List<TCourse> getUserCourse(PageEntity pageEntity) {
        return tCourseMapper.getUserCourse(pageEntity);
    }

    @Override
    public List<TUser> getCourseUser(PageEntity pageEntity) {
        return tUserMapper.getCourseUser(pageEntity);
    }

    @Override
    public TCourse getCourseInfo(Map<String, Object> param) {
        return tCourseMapper.getCourseInfo(param);
    }

    @Override
    public int insertCourseBatch(Map<String, Object> param) throws Exception {
        TCourse record = (TCourse) param.get("course");
        String startDate = (String) param.get("startDate");
        String endDate = (String) param.get("endDate");
        String periods = (String) param.get("periods");
        if (StringUtils.isNotBlank(periods)) {
            List<Period> periodList = JSON.parseArray(periods, Period.class);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-ddHH:mm");
            Date begin = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            for (; begin.compareTo(end) <= 0; ) {
                int weekDay = DateUtil.dayOfWeekInt(begin);
                for (Period item : periodList) {
                    if (item.getWeekDay() == weekDay) {
                        String classHour = sdf.format(begin) + item.getTime();
                        record.setClassHour(format.parse(classHour));
                        tCourseMapper.insertCourse(record);
                    }
                }
                begin = DateUtil.addDays(begin, 1);
            }
        }
        return 1;
    }

    @Override
    public int insertCourse(TCourse record) {
        return tCourseMapper.insertCourse(record);
    }

    @Override
    public Integer updateCourseInfo(Map<String, Object> param) {
        String actions = (String) param.get("actions");
        String delActiconIds = (String) param.get("delActiconIds");
        TCourse course = (TCourse) param.get("record");
        if (course.getSaleStatus() != null && course.getSaleStatus() == 1) {
            //上线活动时，先把处于上线状态的活动改成下线状态，同时只允许上线一个活动
            Map<String, Object> temp = new HashMap<>();
            TCourse record = new TCourse();
            record.setSaleStatus(2);
            temp.put("record", record);
            tCourseMapper.updateCourseInfo(temp);
        }
        int result = tCourseMapper.updateCourseInfo(param);
        //增加、更新动作
        if (StringUtils.isNotBlank(actions)) {
            List<TCourseAction> actionList = JSON.parseArray(actions, TCourseAction.class);
            for (int i = 0; i < actionList.size(); i++) {
                if (actionList.get(i).getActionId() == null) {
                    tCourseActionMapper.addCourseAction(actionList.get(i));
                } else {
                    tCourseActionMapper.updateCourseAction(actionList.get(i));
                }
            }
        }
        //删除动作
        if (StringUtils.isNotBlank(delActiconIds)) {
            Map<String, Object> temp = new HashMap<>();
            temp.put("actionIds", delActiconIds);
            tCourseActionMapper.delCourseAction(temp);
        }
        // 退课
        if (course.getStatus() != null && course.getStatus() == 2) {
            Map<String, Object> temp = new HashMap<>();
            temp.put("courseId", course.getCourseId());
            TCourse check = tCourseMapper.getCourseInfo(temp);
            if (check.getOrderNum() > 0) {
                PageEntity pageEntity = new PageEntity();
                temp.put("status", 3);//订单状态 1：待付款 2：购买中 3：已完成 4：申请退款 5：已退款 6：退款异常
                pageEntity.setMap(temp);
                pageEntity.setPageSize(999999);
                List<TOrder> orders = tOrderMapper.getOrderList(pageEntity);
                for (int i = 0; i < orders.size(); i++) {
                    Map<String, Object> dropMap = new HashMap<>();
                    dropMap.put("order", orders.get(i));
                    payService.dropCourse(dropMap);
                    // 微信通知用户
                    String template_id = "yVr6ODvNTpfijcx-UQVLquTCc-IfoOBEdso15ExhN_o";//默认退课模版
                    String first = "退课通知";
                    String keyword1 = "课程调整需关闭课程";
                    String keyword2 = check.getCourseName() + "(教练:" + check.getName() + "),退款金额" + orders.get(i).getAmount() + "元";
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
                    template.setData(params);
                    template.setTouser(orders.get(i).getOpenid());
                    String jsonMsg = JSON.toJSONString(template);
                    weChatMessage.sendTemplateMessage(jsonMsg);

                }
                // 微信通知教练
                String template_id = "yVr6ODvNTpfijcx-UQVLquTCc-IfoOBEdso15ExhN_o";//默认退课模版
                String first = "退课通知";
                String keyword1 = "课程调整需关闭课程";
                String keyword2 = check.getCourseName() + "(教练:" + check.getName() + ")";
                String keyword3 = DateUtil.toString(check.getClassHour(), "yyyy-MM-dd HH:mm");
                String remark = "";
                TreeMap<String, TreeMap<String, String>> params = new TreeMap<>();
                params.put("first", TemplateData.item(first, "#173177"));
                params.put("keyword1", TemplateData.item(keyword1, "#173177"));
                params.put("keyword2", TemplateData.item(keyword2, "#173177"));
                params.put("keyword3", TemplateData.item(keyword3, "#173177"));
                params.put("remark", TemplateData.item(remark, ""));
                TemplateData template = new TemplateData();
                template.setTemplate_id(template_id);
                template.setTouser(check.getOpenid());
                template.setData(params);
                String jsonMsg = JSON.toJSONString(template);
                weChatMessage.sendTemplateMessage(jsonMsg);
            }
        }
        return result;
    }

    @Override
    public List<TCourseAction> getCourseActions(Map<String, Object> param) {
        return tCourseActionMapper.getCourseActions(param);
    }

    @Override
    public List<TCourse> getCourseNoRepeat(Map<String, Object> param) {
        return tCourseMapper.getCourseNoRepeat(param);
    }

}
