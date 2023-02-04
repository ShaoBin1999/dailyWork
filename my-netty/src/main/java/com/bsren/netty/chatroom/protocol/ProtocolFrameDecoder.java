package com.bsren.netty.chatroom.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;


public class ProtocolFrameDecoder extends LengthFieldBasedFrameDecoder {


    public ProtocolFrameDecoder(){
        this(1024,12,4,0,0);
    }

    /**
     *
     * @param maxFrameLength 最大字节数
     * @param lengthFieldOffset 长度字段的偏移量
     * @param lengthFieldLength 长度字段的长度
     * @param lengthAdjustment  header和body之间的字节数
     * @param initialBytesToStrip 读取完整个数据包后需要从头丢弃的字节数
     */
    public ProtocolFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}
