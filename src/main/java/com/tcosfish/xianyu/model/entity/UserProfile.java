package com.tcosfish.xianyu.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import lombok.Data;

/**
 * 用户-冷数据
 * @author tcosfish
 * @TableName user_profile
 */
@TableName(value ="user_profile")
@Data
public class UserProfile {
    /**
     * 
     */
    @TableId
    private Long userId;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 简介
     */
    private String bio;

    /**
     * 0女 1男 2保密
     */
    private Integer gender;

    /**
     * 学校
     */
    private String school;

    /**
     * 学院
     */
    private String college;

    /**
     * 年级
     */
    private String grade;

    /**
     * 学号
     */
    private String studentNo;

    /**
     * 假删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 
     */
    private Date createtime;

    /**
     * 
     */
    private Date updatetime;
}