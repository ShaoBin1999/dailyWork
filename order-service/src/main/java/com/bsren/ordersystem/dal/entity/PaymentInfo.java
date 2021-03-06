package com.bsren.ordersystem.dal.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("t_payment_info")
public class PaymentInfo extends BaseEntity{

    private String tradeNo;

    private BigDecimal amount;

    private String paymentStatus;

    @TableLogic
    private Integer isDeleted;
}
