package io.github.enkarin.chefbot.pipelineHandlers;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;

public interface NonCommandInputHandler {
    ExecutionResult execute(long userId, String text);
    BotAnswer getMessageForUser(long userId);
    ChatStatus getCurrentStatus();
}
