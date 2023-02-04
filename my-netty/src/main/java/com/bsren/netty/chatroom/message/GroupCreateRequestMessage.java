package com.bsren.netty.chatroom.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@AllArgsConstructor
@Getter
@ToString
public class GroupCreateRequestMessage extends Message{

    private String name;

    private Set<String> groupMembers;

    private String creator;

    @Override
    public int getMessageType() {
        return GroupCreateRequestMessage;
    }
}
