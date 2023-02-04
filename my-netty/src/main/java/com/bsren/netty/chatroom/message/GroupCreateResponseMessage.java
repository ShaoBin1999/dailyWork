package com.bsren.netty.chatroom.message;

import lombok.ToString;

@ToString
public class GroupCreateResponseMessage extends AbstractResponseMessage{

    public GroupCreateResponseMessage(boolean success, String description) {
        super(success, description);
    }

    @Override
    public int getMessageType() {
        return GroupCreateResponseMessage;
    }
}
