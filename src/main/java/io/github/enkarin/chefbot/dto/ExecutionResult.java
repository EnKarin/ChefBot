package io.github.enkarin.chefbot.dto;

import io.github.enkarin.chefbot.enums.ChatStatus;

import java.util.Optional;

public record ExecutionResult(ChatStatus chatStatus, Optional<?> systemAction) {
    public ExecutionResult(final ChatStatus chatStatus) {
        this(chatStatus, Optional.empty());
    }

    public ExecutionResult(final ChatStatus chatStatus, final Object systemAction) {
        this(chatStatus, Optional.of(systemAction));
    }
}
