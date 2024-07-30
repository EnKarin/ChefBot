package io.github.enkarin.chefbot.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserAnswerOption {
    NONE(null),
    YES_NO(new String[]{"Да", "Нет"});

    private final String[] answers;
}
