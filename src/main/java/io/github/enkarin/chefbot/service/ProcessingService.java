package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.enums.ChatStatus;

public interface ProcessingService {
    BotAnswer execute(long chatId, String text);
    ChatStatus getCurrentStatus();
}
