package io.github.enkarin.chefbot.dto;

import io.github.enkarin.chefbot.enums.UserAnswerOption;

public record BotAnswer(String messageText, UserAnswerOption userAnswerOption) {
    public BotAnswer(final String messageText) {
        this(messageText, UserAnswerOption.NONE);
    }
}
