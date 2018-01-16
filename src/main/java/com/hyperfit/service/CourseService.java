package com.hyperfit.service;

import com.hyperfit.entity.TCourse;
import com.hyperfit.entity.TCourseAction;
import com.hyperfit.entity.TUser;
import com.hyperfit.util.PageEntity;

import java.util.List;
import java.util.Map;

/**
 * <p> 课程service</p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/11/20 20:07
 */
public interface CourseService {

    /**
     * 查询课程分页列表
     */
    List<TCourse> getCourseList(PageEntity pageEntity);

    /**
     * 查询课程列表
     */
    List<TCourse> getCourses(Map<String, Object> param);

    /**
     * 查询用户课程列表
     */
    List<TCourse> getUserCourse(PageEntity pageEntity);

    /**
     * 查询课程预约用户列表
     */
    List<TUser> getCourseUser(PageEntity pageEntity);

    /**
     * 查询课程信息
     */
    TCourse getCourseInfo(Map<String, Object> param);

    /**
     * 批量新增课程
     */
    int insertCourseBatch(Map<String, Object> param) throws Exception;

    /**
     * 新增课程
     */
    int insertCourse(TCourse record);

    /**
     * 更新课程信息
     */
    Integer updateCourseInfo(Map<String, Object> param) throws Exception;

    /**
     * 查询课程动作列表
     */
    List<TCourseAction> getCourseActions(Map<String, Object> param);

    /**
     * 查询不重复课程（同一种课程只展示一条数据，不按上课时间都展示出来）
     */
    List<TCourse> getCourseNoRepeat(Map<String, Object> param);
}
