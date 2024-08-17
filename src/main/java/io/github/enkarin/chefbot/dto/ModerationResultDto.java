package io.github.enkarin.chefbot.dto;

import java.util.Set;

public record ModerationResultDto(String name, long ownerChat, Set<ModerationRequestMessageDto> messageForRemove, boolean approve) {
    public static ModerationResultDto createApproveResult(final String name, final long ownerChat, final Set<ModerationRequestMessageDto> messageForRemove) {
        return new ModerationResultDto(name, ownerChat, messageForRemove, true);
    }

    public static ModerationResultDto createDeclineResult(final String name, final long ownerChat, final Set<ModerationRequestMessageDto> messageForRemove) {
        return new ModerationResultDto(name, ownerChat, messageForRemove, false);
    }
}
