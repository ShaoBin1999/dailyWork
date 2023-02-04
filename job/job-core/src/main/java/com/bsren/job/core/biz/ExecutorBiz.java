package com.bsren.job.core.biz;

import com.bsren.job.core.biz.model.KillParam;
import com.bsren.job.core.biz.model.LogParam;
import com.bsren.job.core.biz.model.ReturnT;
import com.bsren.job.core.biz.model.TriggerParam;


/**
 * 定义了executor需要执行来自客户端的执行种类
 */
public interface ExecutorBiz {

    ReturnT<String> beat();

    //TODO 客户端如何收集执行器的信息呢
//    ReturnT<String> idleBeat();

    ReturnT<String> run(TriggerParam triggerParam);

    //todo 只有一个参数的killParam
    ReturnT<String> kill(KillParam killParam);

    ReturnT<String> log(LogParam logParam);
}
