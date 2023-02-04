package com.bsren.netty.chatroom.message;

import lombok.ToString;

@ToString
public class LoginResponseMessage extends AbstractResponseMessage{

    public LoginResponseMessage(boolean success, String description) {
        super(success, description);
    }

    @Override
    public int getMessageType() {
        return LoginResponseMessage;
    }
}
