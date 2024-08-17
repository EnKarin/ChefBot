package io.github.enkarin.chefbot.dto;

import lombok.Data;

@Data
public class ModerationRequestMessageDto {
    private int messageId;
    private long chatId;

    public ModerationRequestMessageDto(final int messageId) {
        this.messageId = messageId;
    }
}
