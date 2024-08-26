package io.github.enkarin.chefbot.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum UserAnswerOption {
    NONE(null),
    DEFAULT(null),
    YES_OR_NO(new String[]{"Да", "Нет"}),
    YES_NO_OR_ANY(new String[]{"Да", "Нет", "Любое"}),
    MORE_OR_STOP(new String[]{"Вывести еще", "Вернуться в главное меню"}),
    CUISINES(createCuisines()),
    CUISINES_WITH_ANY_CASE(createCuisinesWithAnyCase());

    private final String[] answers;

    private static String[] createCuisinesWithAnyCase() {
        final List<String> cuisines = Arrays.stream(WorldCuisine.values()).map(WorldCuisine::getLocalizedValue).collect(Collectors.toList());
        cuisines.add("Любая");
        return cuisines.toArray(String[]::new);
    }

    private static String[] createCuisines() {
        return Arrays.stream(WorldCuisine.values()).map(WorldCuisine::getLocalizedValue).toArray(String[]::new);
    }
}
