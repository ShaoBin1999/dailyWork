package com.bsren.javaStd.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TestEnum {
    NOTPAY("未支付"),
    SUCCESS("支付成功"),

    REFUND("转入退款"),
    CLOSED("已关闭"),

    CANCEL("用户已取消"),

    REVOKED("已撤销（仅付款码支付会返回）"),   //存储时转到已关闭
    USERPAYING("用户支付中（仅付款码支付会返回）"), //存储时转到未支付

    PAYERROR("支付失败（仅付款码支付会返回）");

    private final String type;
}

