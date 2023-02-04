package com.bsren.job.core.biz.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CallbackParam {

    private long logId;
    private long logDateTim;

    private int handleCode;
    private String handleMsg;
}
