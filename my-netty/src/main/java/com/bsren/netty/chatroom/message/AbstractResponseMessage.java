package com.bsren.netty.chatroom.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
public abstract class AbstractResponseMessage extends Message{

    private boolean success = true;

    private String description;
}
