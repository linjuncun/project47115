package com.hyperfit.dao;

import com.hyperfit.entity.TCourseAction;

import java.util.List;
import java.util.Map;

public interface TCourseActionMapper {

    /**
     * 查询课程动作列表
     */
    List<TCourseAction> getCourseActions(Map<String, Object> param);

    /**
     * 新增课程动作
     */
    int addCourseAction(TCourseAction record);

    /**
     * 更新课程动作
     */
    Integer updateCourseAction(TCourseAction record);

    /**
     * 删除课程动作
     */
    int delCourseAction(Map<String, Object> param);

}