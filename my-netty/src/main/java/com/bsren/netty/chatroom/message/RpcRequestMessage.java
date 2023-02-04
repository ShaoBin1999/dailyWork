package com.bsren.netty.chatroom.message;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RpcRequestMessage extends Message {

    private String interfaceName;

    private String methodName;

    private Class<?> returnType;

    private Class[] paramTypes;

    private Object[] paramValues;

    public RpcRequestMessage(int sequenceId,String interfaceName,
                             String methodName,Class<?> returnType,
                             Class[] paramTypes,Object[] paramValues){
        super.setSequenceId(sequenceId);
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.returnType = returnType;
        this.paramTypes = paramTypes;
        this.paramValues = paramValues;
    }





    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_REQUEST;
    }
}
