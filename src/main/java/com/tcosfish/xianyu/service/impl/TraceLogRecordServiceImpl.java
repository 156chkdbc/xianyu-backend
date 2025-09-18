package com.tcosfish.xianyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcosfish.xianyu.model.entity.TraceLogRecord;
import com.tcosfish.xianyu.service.TraceLogRecordService;
import com.tcosfish.xianyu.mapper.TraceLogRecordMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* @author Administrator
* @description 针对表【trace_log_record(TraceLog注解操作轨迹日志表)】的数据库操作Service实现
* @createDate 2025-09-11 11:37:20
*/
@Service
public class TraceLogRecordServiceImpl extends ServiceImpl<TraceLogRecordMapper, TraceLogRecord>
    implements TraceLogRecordService{
}




