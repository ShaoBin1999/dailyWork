package com.bsren.netty.chatroom.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class ChatRequestMessage extends Message{

    private String content;

    private String from;

    private String to;

    @Override
    public int getMessageType() {
        return ChatRequestMessage;
    }
}
