package io.github.enkarin.chefbot.dto;

import java.util.Objects;
import java.util.Set;

public record ModerationResultDto(String dishName, long ownerChat, Set<ModerationRequestMessageDto> messageForRemove, String declineCause) {
    public ModerationResultDto(final String dishName, final long ownerChat, final Set<ModerationRequestMessageDto> messageForRemove) {
        this(dishName, ownerChat, messageForRemove, null);
    }

    public boolean isApprove() {
        return Objects.isNull(declineCause);
    }
}
