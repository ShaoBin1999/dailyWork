package com.bsren.ordersystem.dal.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("t_refund_info")
public class RefundInfo extends BaseEntity{

    private String tradeNo;

    private String refundNo;

    private String refundId;

    private BigDecimal preAmount;

    private BigDecimal amount;

    private String reason;

    private String refundStatus;

}
