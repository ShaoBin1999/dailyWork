package com.bsren.job.core.mapper;

import com.bsren.job.core.model.JobInfo;
import com.bsren.job.core.mybatisplus.RootMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface JobInfoMapper extends RootMapper<JobInfo> {

    @Select("select * from job_info where trigger_status = 1 " +
            "and trigger_next_time < #{nowTime} " +
            "limit #{count};")
    List<JobInfo> selectJobs(@Param("nowTime") Long nowTime,
                             @Param("count") Integer count);
}
