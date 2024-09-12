package io.github.enkarin.chefbot.service.pipelines;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;

public interface ProcessingService {
    ExecutionResult execute(long userId, String text);
    BotAnswer getMessageForUser(long userId);
    ChatStatus getCurrentStatus();
}
