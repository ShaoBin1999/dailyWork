package com.bsren.netty.chatroom.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class GroupJoinRequestMessage extends Message{

    private String groupName;

    private String userName;

    @Override
    public int getMessageType() {
        return GroupJoinRequestMessage;
    }
}
