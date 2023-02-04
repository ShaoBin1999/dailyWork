package com.bsren.job.core.thread;

import com.bsren.job.core.enums.TriggerStatusEnum;
import com.bsren.job.core.mapper.JobInfoMapper;
import com.bsren.job.core.model.JobInfo;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class JobScheduler {

    private Thread scheduleThread;

    private static final long PRE_READ_TIME = 5000;


    @Resource
    private JobInfoMapper jobInfoMapper;

    private boolean stop = false;

    /**
     * 1.先做循环的任务
     */
    public void start(){

        log.info("开始轮询数据库查找到期的任务");

        scheduleThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!stop){
                    long now = System.currentTimeMillis();
                    try {
                        List<JobInfo> jobInfos = jobInfoMapper.selectJobs(now + PRE_READ_TIME, 1000);
                        for (JobInfo jobInfo : jobInfos) {
                            long triggerNextTime = jobInfo.getTriggerNextTime();
                            if(now>=triggerNextTime){
                                int triggerCount = jobInfo.getTriggerCount();
                                if(triggerCount>0){
                                    jobInfo.setTriggerCount(triggerCount-1);
                                    if(triggerCount==1){
                                        jobInfo.setTriggerStatus(TriggerStatusEnum.FINISHED.getCode());
                                    }
                                }
                                trigger(jobInfo);
                            }else {
                                updateNextTriggerTime(jobInfo);
                            }
                        }
                        jobInfoMapper.updateBatch(jobInfos);
                    }catch (Exception e){
                        if(!stop){
                            log.error("job scheduler shutdown exception");
                        }
                    }finally {

                        //休眠一段时间
                        //TODO
                        long cost = System.currentTimeMillis()-now;
                        if(cost<1000){
                            try {
                                TimeUnit.MILLISECONDS.sleep(1000-cost);
                            } catch (InterruptedException e) {
                                if(!stop) {
                                    log.error(e.getMessage());
                                }
                            }
                        }
                    }

                }
            }
        });
        scheduleThread.setDaemon(true);
        scheduleThread.setName("jobScheduleThread");
        scheduleThread.start();
    }

    private void trigger(JobInfo jobInfo) {
    }

    private void updateNextTriggerTime(JobInfo jobInfo) {
        String triggerExpression = jobInfo.getTriggerExpression();
        if(triggerExpression==null){  //simple trigger
            long lastTime = jobInfo.getTriggerLastTime();
            long timeInterval = jobInfo.getTimeInterval();
            jobInfo.setTriggerNextTime(lastTime+timeInterval);
        }
    }

}
