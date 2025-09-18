package com.tcosfish.xianyu.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * TraceLog注解操作轨迹日志表
 * @author tcosfish
 * @TableName trace_log_record
 */
@TableName(value ="trace_log_record")
@Data
public class TraceLogRecord {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 链路追踪ID（Sleuth/SkyWalking）
     */
    private String traceId;

    /**
     * 用户ID（未登录填0）
     */
    private Long userId;

    /**
     * 客户端公网IP
     */
    private String ip;

    /**
     * 类简名
     */
    private String clazz;

    /**
     * 方法名
     */
    private String method;

    /**
     * 业务描述（@TraceLog.desc）
     */
    private String description;

    /**
     * 入参JSON（脱敏后）
     */
    private String params;

    /**
     * 返回值JSON（异常时为空）
     */
    private String result;

    /**
     * 是否成功 1-成功 0-失败
     */
    private Integer success;

    /**
     * 异常简要信息
     */
    private String errMsg;

    /**
     * 耗时（毫秒）
     */
    private Integer cost;

    /**
     * 记录创建时间
     */
    private Date createTime;
}