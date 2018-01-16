package com.hyperfit.dao;

import com.hyperfit.entity.SysConfig;

import java.util.List;
import java.util.Map;
/**
 * <p> 系统配置dao </p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/11/18 15:07
 */
public interface SysConfigMapper {

    /**
     * 查询系统配置信息
     */
    SysConfig getSysConfig(Map<String, Object> param);

    /**
     * 获取session值
     */
    String getSessionByKey(Map<String, Object> param);

    /**
     * 新增session
     */
    int addSession(Map<String, Object> param);

    /**
     * 更新session
     */
    int updateSession(Map<String, Object> param);

    /**
     * 修改系统配置
     */
    int updateSysConfig(Map<String, Object> param);

    /**
     * 查询用户消费概览
     */
    Object getUserView(Map<String, Object> param);

    /**
     * 查询用户预约情况报表
     */
    List<Map<String,Object>> getOrderView(Map<String, Object> param);
    /**
     * 查询用户上课时间报表
     */
    List<Map<String,Object>> getClassHourView(Map<String, Object> param);

    /**
     * 查询总收入
     */
    Object getTotalView(Map<String, Object> param);

    /**
     * 查询会员卡销售收入报表
     */
    List<Map<String,Object>> getCardView(Map<String, Object> param);

    /**
     * 查询课程总预览
     */
    Object getCourseTotal(Map<String, Object> param);

    /**
     * 查询课程数量报表
     */
    List<Map<String,Object>> getCourseNumView(Map<String, Object> param);

    /**
     * 查询课程服务人次报表
     */
    List<Map<String,Object>> getCourseOrderView(Map<String, Object> param);
    /**
     * 查询会员转化报表
     */
    Object getUserCardView(Map<String, Object> param);

    /**
     * 查询当前会员卡状态报表
     */
    Object getCardStatusView(Map<String, Object> param);
}

