package io.github.enkarin.chefbot.dto;

import lombok.Data;

@Data
public class RequestMessageInfoDto {
    private int messageId;
    private long chatId;

    public RequestMessageInfoDto(final int messageId) {
        this.messageId = messageId;
    }
}
