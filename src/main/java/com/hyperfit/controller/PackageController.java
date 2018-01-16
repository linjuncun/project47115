package com.hyperfit.controller;

import com.alibaba.fastjson.JSON;
import com.hyperfit.dao.TClubCardMapper;
import com.hyperfit.entity.*;
import com.hyperfit.service.PackageService;
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
 * <p> 套餐控制器 </p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/11/23 20:38
 */
@Controller
@RequestMapping("/package")
public class PackageController extends BaseController {

    @Resource(name = "packageServiceImpl")
    private PackageService packageService;

    @Resource(name = "tClubCardMapperImpl")
    private TClubCardMapper tClubCardMapper;

    /**
     * 查询套餐列表
     */
    @RequestMapping("getPackageList.do")
    @ResponseBody
    public ApiModel getPackageList(Integer draw, Integer pageIndex, Integer pageSize, String openid, String cardType, String conditions) {
        ApiModel apiModel = new ApiModel();
        PageEntity param = new PageEntity();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, openid, user)) {
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("cardType", cardType);
            map.put("conditions", conditions);
            if (StringUtils.isNotBlank(openid)) {
                //微信端查询
                map.put("status", 1);//状态 1：上线 2：下线
            } else {
                // 判断是否拥有权限
                List<SysFunction> functions = getFunctions("46");
                boolean hasFunction = hasFunction(functions, "view");
                if (!hasFunction) {
                    apiModel.setCode("1");
                    apiModel.setMsg("无查看权限");
                    return apiModel;
                }
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
            List<TPackage> list = packageService.getPackageList(param);
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
            log.error("查询套餐列表失败：", e);
        }
        apiModel.setData(param);
        return apiModel;
    }

    /**
     * 微信端-查询套餐详情
     */
    @RequestMapping("getPackageDetail.do")
    @ResponseBody
    public ApiModel getPackageDetail(String openid, String packageId) {
        ApiModel apiModel = new ApiModel();
        try {
            if (StringUtils.isBlank(packageId)) {
                apiModel.setCode("1");
                apiModel.setMsg("参数错误");
                return apiModel;
            }
            TUser user = new TUser();
            if (!checkUser(apiModel, openid, user)) {
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("packageId", packageId);
            TPackage packageInfo = packageService.getPackageInfo(map);
            apiModel.setData(packageInfo);
            apiModel.setCode("0");
            apiModel.setMsg("查询成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("查询套餐详情失败：", e);
        }
        return apiModel;
    }

    /**
     * 查询会员卡办卡数量
     */
    @RequestMapping("getCardNumber.do")
    @ResponseBody
    public ApiModel getCardNumber() {
        ApiModel apiModel = new ApiModel();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            List<Object> list = packageService.getCardNumber(map);
            apiModel.setData(list);
            apiModel.setCode("0");
            apiModel.setMsg("查询成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("查询会员卡办卡数量失败：", e);
        }
        return apiModel;
    }

    /**
     * 后台-新增套餐
     */
    @RequestMapping("addPackage.do")
    @ResponseBody
    public ApiModel addPackage(TPackage record, String conditions) {
        ApiModel apiModel = new ApiModel();
        try {
            if (StringUtils.isAnyBlank(record.getPackageName(), record.getNotes()) || record.getCardType() == null || record.getRecharge() == null
                    || record.getArrive() == null || record.getIndate() == null) {
                apiModel.setCode("1");
                apiModel.setMsg("参数不能为空");
                return apiModel;
            }
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            // 判断是否拥有权限
            List<SysFunction> functions = getFunctions("46");
            boolean hasFunction = hasFunction(functions, "add");
            if (!hasFunction) {
                apiModel.setCode("1");
                apiModel.setMsg("无操作权限");
                return apiModel;
            }
            //名称查重
            Map<String, Object> map = new HashMap<>();
            map.put("packageName", record.getPackageName());
            map.put("cardType", record.getCardType());
            TPackage check = packageService.getPackageInfo(map);
            if (check != null) {
                apiModel.setCode("1");
                apiModel.setMsg("名称重复，请重新输入");
                return apiModel;
            }
            if (StringUtils.isNotBlank(conditions)) {
                List<TReturnCashCondition> list = JSON.parseArray(conditions, TReturnCashCondition.class);
                record.setReturnCashCondition(list);
            }
            packageService.insertPackage(record);
            apiModel.setCode("0");
            apiModel.setMsg("操作成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("新增套餐异常：" + e);
        }
        return apiModel;
    }

    /**
     * 修改套餐
     */
    @RequestMapping("updatePackage.do")
    @ResponseBody
    public ApiModel updatePackage(TPackage record, String conditions) {
        ApiModel apiModel = new ApiModel();
        try {
            if (record.getPackageId() == null) {
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
            if (record.getStatus() != null) {
                code = "stop";
            }
            List<SysFunction> functions = getFunctions("46");
            boolean hasFunction = hasFunction(functions, code);
            if (!hasFunction) {
                apiModel.setCode("1");
                apiModel.setMsg("无操作权限");
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            //查重
            if (StringUtils.isNotBlank(record.getPackageName())) {
                map.put("packageName", record.getPackageName());
                map.put("cardType", record.getCardType());
                TPackage check = packageService.getPackageInfo(map);
                if (check != null && check.getPackageId() != record.getPackageId()) {
                    apiModel.setCode("1");
                    apiModel.setMsg("名称重复，请重新输入");
                    return apiModel;
                }
            }
            map.clear();
            if (StringUtils.isNotBlank(conditions)) {
                List<TReturnCashCondition> list = JSON.parseArray(conditions, TReturnCashCondition.class);
                record.setReturnCashCondition(list);
            }
            map.put("record", record);
            packageService.updatePackageInfo(map);
            apiModel.setCode("0");
            apiModel.setMsg("操作成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("修改套餐异常：" + e);
        }
        return apiModel;
    }

    /**
     * 查询套餐会员卡列表
     */
    @RequestMapping("getPackageCardList.do")
    @ResponseBody
    public ApiModel getPackageCardList(Integer draw, Integer pageIndex, Integer pageSize, String packageId, String status,
                                       String startDate, String endDate, String balanceA, String balanceB) {
        ApiModel apiModel = new ApiModel();
        PageEntity param = new PageEntity();
        try {
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("packageId", packageId);
            map.put("status", status);
            map.put("startDate", startDate);
            map.put("endDate", endDate);
            map.put("balanceA", balanceA);
            map.put("balanceB", balanceB);
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
            List<TClubCard> list = packageService.getPackageCardList(param);
            PageUtil.objectToPage(param, list);
            apiModel.setCode("0");
            if (list.size() > 0) {
                apiModel.setMsg("查询成功");
            } else {
                apiModel.setMsg("查询结果为空");
            }
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：请联系管理员");
            log.error("查询套餐会员卡列表失败：", e);
        }
        apiModel.setData(param);
        return apiModel;
    }

    /**
     * 新增用户会员卡
     */
    @RequestMapping("addClubCard.do")
    @ResponseBody
    public ApiModel addClubCard(TClubCard card) {
        ApiModel apiModel = new ApiModel();
        try {
            if (card.getPackageId() == null || card.getUserId() == null) {
                apiModel.setCode("1");
                apiModel.setMsg("参数不能为空");
                return apiModel;
            }
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            // 判断是否拥有权限
            List<SysFunction> functions = getFunctions("46");
            boolean hasFunction = hasFunction(functions, "addCard");
            if (!hasFunction) {
                apiModel.setCode("1");
                apiModel.setMsg("无操作权限");
                return apiModel;
            }
            //判断用户是否存在该套餐的未被封禁的会员卡
            Map<String, Object> checkMap = new HashMap<>();
            checkMap.put("userId", card.getUserId());
            checkMap.put("packageId", card.getPackageId());
            checkMap.put("status", 1);//状态 1：正常 2：停用
            List<TClubCard> list = tClubCardMapper.getClubCardList(checkMap);
            if (list != null && list.size() > 0) {
                apiModel.setCode("1");
                apiModel.setMsg("该用户已存在此套餐的会员卡");
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("packageId", card.getPackageId());
            TPackage packageInfo = packageService.getPackageInfo(map);
            card.setBalance(packageInfo.getArrive());
            card.setPackageMoney(packageInfo.getArrive());
            card.setSysUserId(sysUserId);

            packageService.addClubCard(card);
            apiModel.setCode("0");
            apiModel.setMsg("操作成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("新增用户会员卡异常：" + e);
        }
        return apiModel;
    }

    /**
     * 修改用户会员卡
     */
    @RequestMapping("updateClubCard.do")
    @ResponseBody
    public ApiModel updateClubCard(TClubCard card) {
        ApiModel apiModel = new ApiModel();
        try {
            if (card.getCardId() == null) {
                apiModel.setCode("1");
                apiModel.setMsg("参数不能为空");
                return apiModel;
            }
            TUser user = new TUser();
            if (!checkUser(apiModel, "", user)) {
                return apiModel;
            }
            // 判断是否拥有权限
            List<SysFunction> functions = getFunctions("46");
            boolean hasFunction = hasFunction(functions, "stopCard");
            if (!hasFunction) {
                apiModel.setCode("1");
                apiModel.setMsg("无操作权限");
                return apiModel;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("record", card);
            packageService.updateClubCard(map);
            apiModel.setCode("0");
            apiModel.setMsg("操作成功");
        } catch (Exception e) {
            apiModel.setCode("1");
            apiModel.setMsg("系统异常：" + e.getMessage());
            log.error("修改用户会员卡异常：" + e);
        }
        return apiModel;
    }

}