package io.github.enkarin.chefbot.dto;

import io.github.enkarin.chefbot.enums.StandardUserAnswerOption;

public record BotAnswer(String messageText, String... userAnswerOptions) {
    public BotAnswer(final String messageText, final StandardUserAnswerOption userAnswerOptions) {
        this(messageText, userAnswerOptions.getAnswers());
    }
}
