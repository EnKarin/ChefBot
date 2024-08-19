package io.github.enkarin.chefbot.dto;

import java.util.Set;

public record ModerationResultDto(String dishName, long ownerChat, Set<ModerationRequestMessageDto> messageForRemove, boolean approve) {
    public static ModerationResultDto createApproveResult(final String dishName, final long ownerChat, final Set<ModerationRequestMessageDto> messageForRemove) {
        return new ModerationResultDto(dishName, ownerChat, messageForRemove, true);
    }

    public static ModerationResultDto createDeclineResult(final String dishName, final long ownerChat, final Set<ModerationRequestMessageDto> messageForRemove) {
        return new ModerationResultDto(dishName, ownerChat, messageForRemove, false);
    }
}
