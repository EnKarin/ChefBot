package io.github.enkarin.chefbot.dto;

import io.github.enkarin.chefbot.enums.StandardUserAnswerOption;

import java.util.Optional;

public record BotAnswer(String messageText, Optional<String[]> userAnswerOptions) {
    public BotAnswer(final String messageText) {
        this(messageText, Optional.of(new String[]{}));
    }

    public BotAnswer(final String messageText, final StandardUserAnswerOption userAnswerOptions) {
        this(messageText, Optional.of(userAnswerOptions.getAnswers()));
    }

    public BotAnswer(final String messageText, final String... userAnswerOptions) {
        this(messageText, Optional.of(userAnswerOptions));
    }

    public static BotAnswer createBotAnswerWithoutKeyboard(final String messageText) {
        return new BotAnswer(messageText, Optional.empty());
    }
}
