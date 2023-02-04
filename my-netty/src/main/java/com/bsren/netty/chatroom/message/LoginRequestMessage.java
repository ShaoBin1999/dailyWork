package com.bsren.netty.chatroom.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class LoginRequestMessage extends Message{

    private String name;

    private String password;

    @Override
    public int getMessageType() {
        return LoginRequestMessage;
    }
}
