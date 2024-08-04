package io.github.enkarin.chefbot.dto;

import io.github.enkarin.chefbot.enums.UserAnswerOption;

public record BotAnswer(String messageText, UserAnswerOption userAnswerOption, long publishDishId) {
    public BotAnswer(final String messageText) {
        this(messageText, UserAnswerOption.DEFAULT, -1);
    }

    public static BotAnswer createBotAnswerWithoutKeyboard(final String messageText) {
        return new BotAnswer(messageText, UserAnswerOption.NONE, -1);
    }

    public BotAnswer(final String messageText, final UserAnswerOption userAnswerOption) {
        this(messageText, userAnswerOption, -1);
    }
}
