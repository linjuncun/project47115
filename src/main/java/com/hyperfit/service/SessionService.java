package com.hyperfit.service;

import java.util.Map;

/**
 * 微信session不可靠，故用模拟session实现相关功能（也可用redis替代）
 */
public interface SessionService {

    /**
     * 获取session值
     */
    String getSessionByKey(Map<String, Object> param);

    /**
     * 存储session
     */
    int addSession(Map<String, Object> param);
}
