package com.bsren.job.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TriggerStatusEnum {

    INITIAL(0,"初始状态"),

    RUNNING(1,"运行中"),

    PAUSE(2,"暂停中"),

    END(3,"被终止"),

    FINISHED(4,"完成");

    private Integer code;

    private String status;
}
