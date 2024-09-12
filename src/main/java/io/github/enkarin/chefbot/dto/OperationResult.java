package io.github.enkarin.chefbot.dto;

import java.util.Optional;

public record OperationResult(BotAnswer botAnswer, Optional<?> systemAction) {
    public OperationResult(final BotAnswer answer) {
        this(answer, Optional.empty());
    }

    public OperationResult(final BotAnswer botAnswer, final Object systemAction) {
        this(botAnswer, Optional.of(systemAction));
    }
}
