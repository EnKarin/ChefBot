package io.github.enkarin.chefbot.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DishType {
    SALAD("салат"), SNACK("закуска"), SOUP("суп"), MAIN_DISH("основное блюдо"), PASTRY("выпечка");

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
