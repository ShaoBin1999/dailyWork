package com.bsren.netty.chatroom.message;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class GroupMembersResponseMessage extends AbstractResponseMessage{

    private String members;

    @Override
    public int getMessageType() {
        return GroupMembersResponseMessage;
    }
}
