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
    CUISINES(createCuisines());

    private final String[] answers;

    private static String[] createCuisines() {
        final List<String> cuisines = Arrays.stream(WorldCuisine.values()).map(WorldCuisine::getLocalizedValue).collect(Collectors.toList());
        cuisines.add("Любое");
        return cuisines.toArray(String[]::new);
    }
}
