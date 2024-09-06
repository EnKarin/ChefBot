package io.github.enkarin.chefbot.dto;

import java.util.Objects;
import java.util.Set;

public record ModerationResultDto(String dishName, long ownerChat, Set<ModerationRequestMessageDto> messageForRemove, String declineCause) {
    public static ModerationResultDto createApproveResult(final String dishName, final long ownerChat, final Set<ModerationRequestMessageDto> messageForRemove) {
        return new ModerationResultDto(dishName, ownerChat, messageForRemove, null);
    }

    public static ModerationResultDto createDeclineResult(final String dishName,
                                                          final long ownerChat,
                                                          final Set<ModerationRequestMessageDto> messageForRemove,
                                                          final String declineCause) {
        return new ModerationResultDto(dishName, ownerChat, messageForRemove, declineCause);
    }

    public boolean isApprove() {
        return Objects.isNull(declineCause);
    }
}
