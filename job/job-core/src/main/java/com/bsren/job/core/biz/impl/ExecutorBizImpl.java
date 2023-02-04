package com.bsren.job.core.biz.impl;

import com.bsren.job.core.biz.ExecutorBiz;
import com.bsren.job.core.biz.model.KillParam;
import com.bsren.job.core.biz.model.LogParam;
import com.bsren.job.core.biz.model.ReturnT;
import com.bsren.job.core.biz.model.TriggerParam;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExecutorBizImpl implements ExecutorBiz {


    @Override
    public ReturnT<String> beat() {
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> run(TriggerParam triggerParam) {
        return null;
    }

    @Override
    public ReturnT<String> kill(KillParam killParam) {
        return null;
    }

    @Override
    public ReturnT<String> log(LogParam logParam) {
        return null;
    }
}
