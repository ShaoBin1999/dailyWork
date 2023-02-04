package com.bsren.netty.chatroom.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class GroupChatResponseMessage extends AbstractResponseMessage{


    String from;

    String group;


    public GroupChatResponseMessage(boolean success, String description) {
        super(success, description);
    }

    @Override
    public int getMessageType() {
        return GroupChatResponseMessage;
    }
}
