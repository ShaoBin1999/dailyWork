package com.bsren.netty.chatroom.message;

import lombok.ToString;

@ToString
public class GroupQuitResponseMessage extends AbstractResponseMessage{

    public GroupQuitResponseMessage(boolean success, String description) {
        super(success, description);
    }

    @Override
    public int getMessageType() {
        return GroupQuitResponseMessage;
    }
}
