package com.bsren.job.core.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("job_info")
public class JobInfo {

    private Long id;                      // 主键id

    private String jobDesc;                 // 任务描述

    private String jobGroup;                // 任务分组

    private Long executorId;                // 执行器id

    private Date addTime;                   // 添加时间
    private Date updateTime;                // 更新时间

    private String author;                  // 创建人

    private Integer triggerStatus;		// 调度状态：0-停止，1-运行

    private Long triggerLastTime;	// 上次调度时间

    private Long triggerNextTime;	// 下次调度时间

    private Long timeInterval;

    private Integer triggerCount;

    private String triggerExpression;

    private boolean finished;       // 是否完成
}
