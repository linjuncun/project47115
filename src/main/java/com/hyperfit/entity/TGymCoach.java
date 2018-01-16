package com.hyperfit.entity;

/**
 * t_gym_coach表实体类
 */
public class TGymCoach {
    /**
     * 健身房id
     */
    private Integer gymId;

    /**
     * 教练id
     */
    private Integer userId;

    /**
     * 健身房id
     */
    public Integer getGymId() {
        return gymId;
    }

    /**
     * 健身房id
     */
    public void setGymId(Integer gymId) {
        this.gymId = gymId;
    }

    /**
     * 教练id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 教练id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}