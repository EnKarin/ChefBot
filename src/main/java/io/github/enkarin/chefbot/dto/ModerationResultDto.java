package io.github.enkarin.chefbot.dto;

import java.util.Set;

public record ModerationResultDto(String name, long toChat, Set<ModerationRequestMessageDto> messageForRemove, boolean approve) {
    public static ModerationResultDto createApproveResult(final String name, final long toChat, final Set<ModerationRequestMessageDto> messageForRemove) {
        return new ModerationResultDto(name, toChat, messageForRemove, true);
    }

    public static ModerationResultDto createDeclineResult(final String name, final long toChat, final Set<ModerationRequestMessageDto> messageForRemove) {
        return new ModerationResultDto(name, toChat, messageForRemove, false);
    }
}
