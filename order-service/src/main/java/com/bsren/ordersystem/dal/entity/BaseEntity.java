package com.bsren.ordersystem.dal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class BaseEntity {

    @TableId(value="key",type = IdType.AUTO)
    private String key;

    private String userId;

    private String orderNo;

    private String paymentType;

    private String tradeType;

    private Date updateTime;

    private Date createTime;
}
