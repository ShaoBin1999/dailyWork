package com.bsren.netty.chatroom.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class GroupMembersRequestMessage extends Message{

    private String groupName;


    @Override
    public int getMessageType() {
        return GroupMembersRequestMessage;
    }
}
