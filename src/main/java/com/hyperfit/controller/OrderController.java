package com.hyperfit.controller;

import com.hyperfit.entity.TOrder;
import com.hyperfit.entity.TUser;
import com.hyperfit.service.OrderService;
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
 * <p> 订单控制器 </p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/11/24 17:51
 */
@Controller
@RequestMapping("/order")
public class OrderController extends BaseController {

    @Resource(name = "orderServiceImpl")
    private OrderService orderService;

    /**
     * 查询用户订单列表
     */
    @RequestMapping("getOrderList.do")
    @ResponseBody
    public ApiModel getOrderList(Integer draw, Integer pageIndex, Integer pageSize, String openid, String cardId,
                                 String queryUserId, String gymId, String orderType, String status, String queryCardNo,
                                 String couponId, String startDate, String endDate, String conditions, String queryType) {
        ApiModel apiModel = new ApiModel();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, openid, user)) {
                return apiModel;
            }
            PageEntity param = new PageEntity();
            Map<String, Object> map = new HashMap<>();
            map.put("openid", openid);
            map.put("cardId", cardId);
            map.put("userId", queryUserId);
            map.put("gymId", gymId);
            map.put("orderType", orderType);
            map.put("status", status);
            map.put("queryCardNo", queryCardNo);
            map.put("couponId", couponId);
            map.put("startDate", startDate);
            map.put("endDate", endDate);
            map.put("conditions", conditions);
            map.put("queryType", queryType);
            if (StringUtils.isNotBlank(openid)) { //微信端查询
                //验证是否绑定手机号
                if (StringUtils.isBlank(user.getPhone())) {
                    apiModel.setCode("-3");
                    apiModel.setMsg("未绑定手机号");
                    return apiModel;
                }
                map.put("queryType", 1);//查询充值成功、退款成功、余额失效的订单
            }
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
            List<TOrder> list = orderService.getOrderList(param);
            PageUtil.objectToPage(param, list);
            apiModel.setCode("0");
            if (list.size() > 0) {
                apiModel.setMsg("查询成功");
            } else {
                apiModel.setMsg("查询结果为空");
            }
            apiModel.setData(param);
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("查询订单列表失败：", e);
        }
        return apiModel;
    }
}
