package com.bsren.ordersystem.dal.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("t_order_info")
public class OrderInfo extends BaseEntity{

    private String tradeNo;

    private String productType;

    private String productTitle;

    private String productId;

    private Integer quantity;//商品数量

    private BigDecimal productPrice; //商品单价（元）

    private BigDecimal amount; //订单金额（元）

    private String orderStatus;//订单状态
}
