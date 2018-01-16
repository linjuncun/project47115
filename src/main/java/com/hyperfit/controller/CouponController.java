package com.hyperfit.controller;

import com.hyperfit.entity.SysFunction;
import com.hyperfit.entity.TCoupon;
import com.hyperfit.entity.TUser;
import com.hyperfit.service.CouponService;
import com.hyperfit.util.ApiModel;
import com.hyperfit.util.PageEntity;
import com.hyperfit.util.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p> 优惠券控制器 </p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/11/22 15:02
 */
@Controller
@RequestMapping("/coupon")
public class CouponController extends BaseController {

    @Resource(name = "couponServiceImpl")
    private CouponService couponService;

    /**
     * 微信端-查询用户优惠券列表
     *
     * @param queryType 查询类型  1：查询未过期优惠券 2：查询已过期优惠券
     */
    @RequestMapping("getUserCoupon.do")
    @ResponseBody
    public ApiModel getUserCoupon(Integer draw, Integer pageIndex, Integer pageSize, String openid, String queryType,
                                  String couponId, String startDate, String endDate, String conditions) {
        ApiModel apiModel = new ApiModel();
        PageEntity param = new PageEntity();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, openid, user)) {
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            if (StringUtils.isNotBlank(openid)) {//微信端请求
                //验证是否绑定手机号
                if (StringUtils.isBlank(user.getPhone())) {
                    apiModel.setCode("-3");
                    apiModel.setMsg("未绑定手机号");
                    return apiModel;
                }
                map.put("status", 1);//前端只展示未使用的优惠券，已过期的优惠券也是指未使用而过期的
            }
            map.put("userId", user.getUserId());
            map.put("queryType", queryType);
            map.put("couponId", couponId);
            map.put("startDate", startDate);
            map.put("endDate", endDate);
            map.put("conditions", conditions);
            param.setMap(map);
            if (pageIndex != null) {
                param.setPageIndex(pageIndex);
            }
            if (pageSize != null) {
                param.setPageSize(pageSize);
            }
            if (draw != null) {
                param.setDraw(draw);
            }
            List<TCoupon> list = couponService.getUserCoupon(param);
            PageUtil.objectToPage(param, list);
            apiModel.setCode("0");
            if (list.size() > 0) {
                apiModel.setMsg("查询成功");
            } else {
                apiModel.setMsg("查询结果为空");
            }
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("查询用户优惠券列表失败：", e);
        }
        apiModel.setData(param);
        return apiModel;
    }

    /**
     * 发放用户优惠券
     */
    @RequestMapping("addUserCoupon.do")
    @ResponseBody
    public ApiModel addUserCoupon(String userIds, String couponId) {
        ApiModel apiModel = new ApiModel();
        try {
            if (StringUtils.isAnyBlank(userIds, couponId)) {
                apiModel.setCode("1");
                apiModel.setMsg("参数不能为空");
                return apiModel;
            }
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            // 判断是否拥有权限
            List<SysFunction> functions = getFunctions("39");
            boolean hasFunction = hasFunction(functions, "coupon");
            if (!hasFunction) {
                apiModel.setCode("1");
                apiModel.setMsg("无操作权限");
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("userIds", userIds);
            map.put("couponId", couponId);
            couponService.addUserCoupon(map);
            apiModel.setCode("0");
            apiModel.setMsg("操作成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("发放用户优惠券异常：" + e);
        }
        return apiModel;
    }

    /**
     * 查询优惠券详情
     */
    @RequestMapping("getCouponDetail.do")
    @ResponseBody
    public ApiModel getCouponDetail(String couponId) {
        ApiModel apiModel = new ApiModel();
        try {
            if (StringUtils.isBlank(couponId)) {
                apiModel.setCode("1");
                apiModel.setMsg("参数错误");
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("couponId", couponId);
            TCoupon coupon = couponService.getCouponInfo(map);
            apiModel.setData(coupon);
            apiModel.setCode("0");
            apiModel.setMsg("查询成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("查询优惠券详情失败：", e);
        }
        return apiModel;
    }

    /**
     * 查询优惠券分页列表
     */
    @RequestMapping("getCouponList.do")
    @ResponseBody
    public ApiModel getCouponList(Integer draw, Integer pageIndex, Integer pageSize) {
        ApiModel apiModel = new ApiModel();
        PageEntity param = new PageEntity();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            // 判断是否拥有权限
            List<SysFunction> functions = getFunctions("53");
            boolean hasFunction = hasFunction(functions, "view");
            if (!hasFunction) {
                apiModel.setCode("1");
                apiModel.setMsg("无查看权限");
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            param.setMap(map);
            if (draw != null) {
                param.setDraw(draw);
            }
            if (pageIndex != null) {
                param.setPageIndex(pageIndex);
            }
            if (pageSize != null) {
                param.setPageSize(pageSize);
            }
            List<TCoupon> list = couponService.getCouponList(param);
            PageUtil.objectToPage(param, list);
            apiModel.setCode("0");
            if (list.size() > 0) {
                apiModel.setMsg("查询成功");
            } else {
                apiModel.setMsg("查询结果为空");
            }
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("查询优惠券列表分页失败：", e);
        }
        apiModel.setData(param);
        return apiModel;
    }

    /**
     * 查询优惠券列表
     */
    @RequestMapping("getCoupons.do")
    @ResponseBody
    public ApiModel getCoupons(String sendType) {
        ApiModel apiModel = new ApiModel();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("sendType", sendType);
            List<TCoupon> list = couponService.getCoupons(map);
            apiModel.setCode("0");
            if (list.size() > 0) {
                apiModel.setMsg("查询成功");
            } else {
                apiModel.setMsg("查询结果为空");
            }
            apiModel.setData(list);
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("查询优惠券列表失败：", e);
        }
        return apiModel;
    }

    /**
     * 新增优惠券
     */
    @RequestMapping("addCoupon.do")
    @ResponseBody
    public ApiModel addCoupon(TCoupon coupon) {
        ApiModel apiModel = new ApiModel();
        try {
            if (StringUtils.isAnyBlank(coupon.getCouponName()) || coupon.getCouponValue() == null || coupon.getIndate() == null) {
                apiModel.setCode("1");
                apiModel.setMsg("参数不能为空");
                return apiModel;
            }
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            // 判断是否拥有权限
            List<SysFunction> functions = getFunctions("53");
            boolean hasFunction = hasFunction(functions, "add");
            if (!hasFunction) {
                apiModel.setCode("1");
                apiModel.setMsg("无操作权限");
                return apiModel;
            }
            //名称查重
            Map<String, Object> map = new HashMap<>();
            map.put("couponName", coupon.getCouponName());
            TCoupon couponChk = couponService.getCouponInfo(map);
            if (couponChk != null) {
                apiModel.setCode("1");
                apiModel.setMsg("名称重复，请重新输入");
                return apiModel;
            }
            couponService.insertCoupon(coupon);
            apiModel.setCode("0");
            apiModel.setMsg("操作成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("新增优惠券异常：" + e);
        }
        return apiModel;
    }

    /**
     * 修改优惠券
     */
    @RequestMapping("updateCoupon.do")
    @ResponseBody
    public ApiModel updateCoupon(TCoupon coupon) {
        ApiModel apiModel = new ApiModel();
        try {
            if (coupon.getCouponId() == null) {
                apiModel.setCode("1");
                apiModel.setMsg("参数不能为空");
                return apiModel;
            }
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            // 判断是否拥有权限
            String code = "edit";
            if (coupon.getCouponStatus() != null && coupon.getCouponStatus() == 2) {
                code = "del";
            }
            List<SysFunction> functions = getFunctions("53");
            boolean hasFunction = hasFunction(functions, code);
            if (!hasFunction) {
                apiModel.setCode("1");
                apiModel.setMsg("无操作权限");
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            if (coupon.getCouponStatus() == null) {//删除操作不用查重
                //名称查重
                map.put("couponName", coupon.getCouponName());
                TCoupon couponChk = couponService.getCouponInfo(map);
                if (couponChk != null && coupon.getCouponId() != couponChk.getCouponId()) {
                    apiModel.setCode("1");
                    apiModel.setMsg("名称重复，请重新输入");
                    return apiModel;
                }
            }
            map.put("record", coupon);
            couponService.updateCouponInfo(map);
            apiModel.setCode("0");
            apiModel.setMsg("操作成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("修改优惠券异常：" + e);
        }
        return apiModel;
    }


}
