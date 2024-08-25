package io.github.enkarin.chefbot.dto;

import io.github.enkarin.chefbot.enums.UserAnswerOption;
import lombok.Builder;

@Builder
public record BotAnswer(String messageText, UserAnswerOption userAnswerOption) {
    public BotAnswer(final String messageText) {
        this(messageText, UserAnswerOption.DEFAULT);
    }

    public static BotAnswer createBotAnswerWithoutKeyboard(final String messageText) {
        return new BotAnswer(messageText, UserAnswerOption.NONE);
    }
}
