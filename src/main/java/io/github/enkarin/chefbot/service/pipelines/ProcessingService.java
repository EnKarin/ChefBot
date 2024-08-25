package io.github.enkarin.chefbot.service.pipelines;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.enums.ChatStatus;

public interface ProcessingService {
    ChatStatus execute(long userId, String text);
    BotAnswer getMessageForUser(long userId);
    ChatStatus getCurrentStatus();
}
