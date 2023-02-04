package com.bsren.job.core;
import com.bsren.job.core.mapper.JobInfoMapper;
import com.bsren.job.core.model.JobInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MybatisPlusTest {


    @Resource
    private JobInfoMapper jobInfoMapper;

    @Test
    public void batchInsert(){
        List<JobInfo> list = jobInfoMapper.selectList(null);

    }

    @Test
    public void batchUpdate(){

    }

}
