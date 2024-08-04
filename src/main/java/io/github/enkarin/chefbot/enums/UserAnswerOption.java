package io.github.enkarin.chefbot.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum UserAnswerOption {
    NONE(null),
    DEFAULT(null),
    YES_OR_NO(new String[]{"Да", "Нет"}),
    CUISINES(Arrays.stream(WorldCuisine.values()).map(WorldCuisine::getLocalizedValue).toArray(String[]::new));

    private final String[] answers;
}
