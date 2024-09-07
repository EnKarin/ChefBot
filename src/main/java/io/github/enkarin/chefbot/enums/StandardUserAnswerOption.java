package io.github.enkarin.chefbot.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum StandardUserAnswerOption {
    NONE(null),
    DEFAULT(null),
    YES_OR_NO(new String[]{"Да", "Нет"}),
    YES_NO_OR_ANY(new String[]{"Да", "Нет", "Любое"}),
    MORE_OR_STOP(new String[]{"Вывести еще", "Вернуться в главное меню"}),
    SEARCH_DISH_OPTIONS(new String[]{"Случайное блюдо", "Все блюда", "Случайное личное блюдо", "Все личные блюда"}),
    CUISINES(Arrays.stream(WorldCuisine.values()).map(WorldCuisine::getLocalizedValue).toArray(String[]::new)),
    CUISINES_WITH_ANY_CASE(createCuisinesWithAnyCase()),
    DISH_TYPES(Arrays.stream(DishType.values()).map(DishType::getLocalisedName).toArray(String[]::new)),
    DISH_TYPES_WITH_ANY_CASE(createDishTypesWithAnyCase());

    private final String[] answers;

    private static String[] createCuisinesWithAnyCase() {
        final List<String> cuisines = Arrays.stream(WorldCuisine.values()).map(WorldCuisine::getLocalizedValue).collect(Collectors.toList());
        cuisines.add("Любая");
        return cuisines.toArray(String[]::new);
    }

    private static String[] createDishTypesWithAnyCase() {
        final List<String> cuisines = Arrays.stream(DishType.values()).map(DishType::getLocalisedName).collect(Collectors.toList());
        cuisines.add("Любой");
        return cuisines.toArray(String[]::new);
    }
}
