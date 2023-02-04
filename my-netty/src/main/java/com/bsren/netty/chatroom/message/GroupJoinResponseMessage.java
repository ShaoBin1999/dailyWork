package com.bsren.netty.chatroom.message;

import lombok.ToString;

@ToString
public class GroupJoinResponseMessage extends AbstractResponseMessage{
    public GroupJoinResponseMessage(boolean success, String description) {
        super(success, description);
    }

    @Override
    public int getMessageType() {
        return GroupJoinResponseMessage;
    }
}
