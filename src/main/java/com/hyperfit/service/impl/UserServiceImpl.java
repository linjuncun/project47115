package com.hyperfit.service.impl;

import com.hyperfit.dao.*;
import com.hyperfit.entity.*;
import com.hyperfit.service.UserService;
import com.hyperfit.util.DateUtil;
import com.hyperfit.util.PageEntity;
import com.hyperfit.util.SignUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


@Service("userServiceImpl")
public class UserServiceImpl implements UserService {

    @Resource(name = "tUserMapperImpl")
    private TUserMapper tUserMapper;

    @Resource(name = "tClubCardMapperImpl")
    private TClubCardMapper tClubCardMapper;

    @Resource(name = "sysUserMapperImpl")
    private SysUserMapper sysUserMapper;

    @Resource(name = "tCourseMapperImpl")
    private TCourseMapper tCourseMapper;

    @Resource(name = "tBalanceItemMapperImpl")
    private TBalanceItemMapper tBalanceItemMapper;

    @Resource(name = "tOrderMapperImpl")
    private TOrderMapper tOrderMapper;

    @Resource(name = "sysFunctionMapperImpl")
    private SysFunctionMapper sysFunctionMapper;

    @Resource(name = "tGymMapperImpl")
    private TGymMapper tGymMapper;

    @Resource(name = "tCouponMapperImpl")
    private TCouponMapper tCouponMapper;

    @Override
    public TUser getUserInfo(Map<String, Object> param) {
        return tUserMapper.getUserInfo(param);
    }

    @Override
    public int insertUser(Map<String, Object> param) {
        String gymIds = (String) param.get("gymIds");
        TUser user = (TUser) param.get("record");
        int result = tUserMapper.insertUser(user);
        // 关联健身房-教练
        if (StringUtils.isNotBlank(gymIds)) {
            List<TGymCoach> list = new ArrayList<>();
            String[] ids = gymIds.split(",");
            for (String gymId : ids) {
                TGymCoach temp = new TGymCoach();
                temp.setGymId(Integer.parseInt(gymId));
                temp.setUserId(user.getUserId());
                list.add(temp);
            }
            tGymMapper.addGymCoaches(list);
        }
        if (user.getUserType() == 2) {
            //同时增加一个教练系统帐户，账号为手机，密码为手机
            SysUser sysUser = new SysUser();
            sysUser.setUsername(user.getPhone());
            sysUser.setTruename(user.getName());
            sysUser.setPassword(SignUtil.MD5(user.getPhone()));
            sysUserMapper.insertSysUser(sysUser);
        }
        return result;
    }

    @Override
    public int updateUserInfo(Map<String, Object> param) {
        int result = tUserMapper.updateUserInfo(param);
        // 更新教练所属健身房数据
        String gymIds = (String) param.get("gymIds");
        TUser user = (TUser) param.get("record");
        String couponFlag = (String) param.get("couponFlag");
        // 关联健身房-教练
        if (StringUtils.isNotBlank(gymIds)) {
            //先删除
            Map<String, Object> map = new HashMap<>();
            map.put("userId", user.getUserId());
            tGymMapper.delGymCoaches(map);
            //再增加
            List<TGymCoach> list = new ArrayList<>();
            String[] ids = gymIds.split(",");
            for (String gymId : ids) {
                TGymCoach temp = new TGymCoach();
                temp.setGymId(Integer.parseInt(gymId));
                temp.setUserId(user.getUserId());
                list.add(temp);
            }
            tGymMapper.addGymCoaches(list);
        }
        if ("yes".equals(couponFlag)) {
            //发放新手注册优惠券
            TCoupon temp = new TCoupon();
            temp.setUserId(user.getUserId());
            temp.setCouponId(1);
            tCouponMapper.addUserCoupon(temp);
        }
        return result;
    }

    @Override
    public int delUser(Map<String, Object> param) {
        return tUserMapper.delUser(param);
    }

    @Override
    public List<TUser> getUserList(PageEntity pageEntity) {
        return tUserMapper.getUserList(pageEntity);
    }

    @Override
    public List<TUser> getUsers(Map<String, Object> param) {
        return tUserMapper.getUsers(param);
    }

    @Override
    public List<TClubCard> getClubCardList(Map<String, Object> param) throws Exception {
        List<TClubCard> list = tClubCardMapper.getClubCardList(param);
        String courseId = (String) param.get("courseId");
        String openid = (String) param.get("openid");
        Integer userId = (Integer) param.get("userId");
        //若传了courseId，则检查会员卡是否支持此课程及所在门店
        if (StringUtils.isNotBlank(courseId)) {
            Map<String, Object> temp = new HashMap<>();
            temp.put("courseId", courseId);
            TCourse course = tCourseMapper.getCourseInfo(temp);
            for (int i = 0; i < list.size(); i++) {
                if ("0".equals(list.get(i).getGymIds())) {
                    list.get(i).setIsSupportGym(1);
                } else {
                    list.get(i).setIsSupportGym(0);//默认不支持
                    if (course != null) {
                        String[] gymIds = list.get(i).getGymIds().split(",");
                        for (String id : gymIds) {
                            if ((course.getGymId() + "").equals(id)) {
                                list.get(i).setIsSupportGym(1);
                                break;
                            }
                        }
                    }
                }
                if ("0".equals(list.get(i).getCourseIds())) {
                    list.get(i).setIsSupportCourse(1);
                } else {
                    list.get(i).setIsSupportCourse(0);//默认不支持
                    if (course != null) {
                        String[] courseIds = list.get(i).getCourseIds().split(",");
                        for (String id : courseIds) {
                            Map<String, Object> courseMap = new HashMap<>();
                            courseMap.put("courseId", id);
                            TCourse check = tCourseMapper.getCourseInfo(courseMap);
                            if (course.getCourseName().equals(check.getCourseName())) {
                                list.get(i).setIsSupportCourse(1);
                                break;
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < list.size(); i++) {
            //计算离返现达标还差多少天
            if (list.get(i).getIsReturnCash() == 1 && list.get(i).getReturnCash() == 0) {//有返现活动且还未返现
                //判断是否达到了返现标准
                List<TReturnCashCondition> conditions = list.get(i).getReturnCashCondition();//返现条件list
                int days = DateUtil.daysBetween(list.get(i).getCreateTime(), new Date());//已经获得会员卡多少天了
                boolean isOutOfDate = true;//该套餐是否所有返现条件都已失效
                int requiredDays = 0;//离返现截止日期还差多少天
                for (int j = 0; j < conditions.size(); j++) {
                    if (days < conditions.get(j).getDays()) {//在有效天数内，等于的时候已经过期
                        isOutOfDate = false;//未失效
                        int temp = conditions.get(j).getDays() - days;
                        if (requiredDays == 0) {
                            requiredDays = conditions.get(j).getDays();
                        }
                        if (temp <= requiredDays) {
                            requiredDays = temp;
                        }
                    }
                }
                list.get(i).setRequiredDays(requiredDays);
                //如果都已失效，更新返现状态
                if (isOutOfDate) {
                    Map<String, Object> cardMap = new HashMap<>();
                    TClubCard record = new TClubCard();//更新数据实体类
                    record.setCardId(list.get(i).getCardId());
                    record.setReturnCash(2);//是否已返现 0：否 1：是 2: 已失效
                    cardMap.put("record", record);
                    tClubCardMapper.updateClubCardInfo(cardMap);
                    list.get(i).setReturnCash(2);
                }
            }
            //判断会员卡老套餐余额是否失效
            if (list.get(i).getOldDeadline() != null) {
                //如果余额中还含有老套餐余额，并且老套餐已到失效日期，则将老余额扣除（失效）
                int oldBalance = list.get(i).getBalance() - list.get(i).getPackageMoney();//老套餐余额
                Date now = new Date();
                if (oldBalance > 0 && list.get(i).getOldDeadline().compareTo(now) <= 0) {
                    //扣除老套餐余额
                    TClubCard record = new TClubCard();//更新数据实体类
                    record.setCardId(list.get(i).getCardId());
                    record.setBalance(list.get(i).getPackageMoney());
                    Map<String, Object> cardMap = new HashMap<>();
                    cardMap.put("record", record);
                    tClubCardMapper.updateClubCardInfo(cardMap);

                    list.get(i).setBalance(list.get(i).getPackageMoney());//返回给前端的数据也是扣除失效余额后的余额
                    //插入余额过期记录
                    TOrder orderRecord = new TOrder();
                    orderRecord.setOrderType(3);//订单类型 1：购买课程  2：购买会员卡套餐  3：会员卡余额过期
                    orderRecord.setOpenid(openid);
                    orderRecord.setUserId(userId);
                    orderRecord.setAmount(oldBalance);
                    orderRecord.setCardId(list.get(i).getCardId());
                    orderRecord.setStatus(3);//订单状态 1：待付款 2：购买中 3：已完成 4：申请退款 5：已退款 6：退款异常
                    orderRecord.setCreateTime(list.get(i).getOldDeadline());//设置时间为老套餐过期时间
                    orderRecord.setUpdateTime(list.get(i).getOldDeadline());//设置时间为老套餐过期时间
                    tOrderMapper.insertOrder(orderRecord);
                }
            }
        }
        return list;
    }

    @Override
    public List<TClubCard> getClubCards(Map<String, Object> param) {
        return tClubCardMapper.getClubCards(param);
    }

    @Override
    public TClubCard getClubCardInfo(Map<String, Object> param) {
        return tClubCardMapper.getClubCardInfo(param);
    }

    @Override
    public List<SysUser> getSysUsers(Map<String, Object> param) {
        return sysUserMapper.getSysUsers(param);
    }

    @Override
    public List<SysUser> getSysUserList(PageEntity pageEntity) {
        return sysUserMapper.getSysUserList(pageEntity);
    }

    @Override
    public SysUser getSysUserInfo(Map<String, Object> param) {
        return sysUserMapper.getSysUserInfo(param);
    }

    @Override
    public int insertSysUser(SysUser param) {
        return sysUserMapper.insertSysUser(param);
    }

    @Override
    public int updateSysUserInfo(Map<String, Object> param) {
        return sysUserMapper.updateSysUserInfo(param);
    }

    @Override
    public List<SysFunction> getUserFunctions(Map<String, Object> param) {
        return sysFunctionMapper.getUserFunctions(param);
    }

    @Override
    public int addSysUserFunctions(Map<String, Object> param) {
        sysFunctionMapper.delSysUserFunctions(param);//先删除
        Integer sysUserId = Integer.parseInt((String) param.get("sysUserId"));
        String functionIds = (String) param.get("functionIds");
        List<SysUserFunction> list = new ArrayList<>();
        String[] ids = functionIds.split(",");
        for (String functionId : ids) {
            SysUserFunction temp = new SysUserFunction();
            temp.setSysUserId(sysUserId);
            temp.setFunctionId(Integer.parseInt(functionId));
            list.add(temp);
        }
        int result = sysFunctionMapper.addSysUserFunctions(list);//再添加
        return result;
    }

    @Override
    public Integer updateClubCardInfo(Map<String, Object> param) {
        return tClubCardMapper.updateClubCardInfo(param);
    }

    @Override
    public List<TBalanceItem> getBalanceItem(PageEntity pageEntity) {
        return tBalanceItemMapper.getBalanceItem(pageEntity);
    }

}
