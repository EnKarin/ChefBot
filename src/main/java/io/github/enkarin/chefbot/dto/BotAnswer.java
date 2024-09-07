package io.github.enkarin.chefbot.dto;

import io.github.enkarin.chefbot.enums.StandardUserAnswerOption;

import java.util.Optional;

public record BotAnswer(String messageText, Optional<String[]> userAnswerOption) {
    public BotAnswer(final String messageText) {
        this(messageText, Optional.of(new String[]{}));
    }

    public BotAnswer(final String messageText, final StandardUserAnswerOption userAnswerOption) {
        this(messageText, Optional.of(userAnswerOption.getAnswers()));
    }

    public BotAnswer(final String messageText, final String[] userAnswerOption) {
        this(messageText, Optional.of(userAnswerOption));
    }

    public static BotAnswer createBotAnswerWithoutKeyboard(final String messageText) {
        return new BotAnswer(messageText, Optional.empty());
    }
}
