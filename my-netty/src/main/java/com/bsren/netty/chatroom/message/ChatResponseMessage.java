package com.bsren.netty.chatroom.message;

import lombok.ToString;

@ToString
public class ChatResponseMessage extends AbstractResponseMessage{

    private String from;

    private String content;

    public ChatResponseMessage(boolean success, String description) {
        super(success, description);
    }

    public ChatResponseMessage(String from, String content) {
        this.from = from;
        this.content = content;
    }

    @Override
    public int getMessageType() {
        return ChatResponseMessage;
    }
}
