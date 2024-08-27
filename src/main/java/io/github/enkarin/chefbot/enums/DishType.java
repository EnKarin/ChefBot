package io.github.enkarin.chefbot.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DishType {
    SALAD("Салат"), SNACK("Закуска"), SOUP("Суп"), MAIN_DISH("Основное блюдо"), PASTRY("Выпечка");

    private final String localisedName;

    public static DishType parse(final String localisedName) {
        for (final DishType type : values()) {
            if (type.getLocalisedName().equalsIgnoreCase(localisedName)) {
                return type;
            }
        }
        throw new IllegalArgumentException(localisedName);
    }
}
