package com.bsren.netty.chatroom.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class GroupChatRequestMessage extends Message{

    private String from;

    private String groupName;

    private String content;

    @Override
    public int getMessageType() {
        return GroupChatRequestMessage;
    }
}
