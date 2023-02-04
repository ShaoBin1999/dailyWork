package com.bsren.netty.chatroom.message;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@ToString
@NoArgsConstructor
public class RpcResponseMessage extends Message{

    private Object returnValue;

    private Exception exception;

    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_RESPONSE;
    }
}
