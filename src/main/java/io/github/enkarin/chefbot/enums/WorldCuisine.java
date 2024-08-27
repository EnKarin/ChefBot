package io.github.enkarin.chefbot.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Locale;

@Getter
@RequiredArgsConstructor
public enum WorldCuisine {
    ASIA("Азиатская"),
    MEDITERRANEAN("Средиземноморская"),
    INTERNATIONAL("Международная"),
    MIDDLE_EASTERN("Ближневосточная"),
    MEXICAN("Мексиканская"),
    SLAVIC("Славянская"),
    OTHER("Что-то необычное");

    private final String localizedValue;

    public static WorldCuisine getCuisine(final String localizedValue) {
        for (final WorldCuisine worldCuisine : values()) {
            if (worldCuisine.getLocalizedValue().equalsIgnoreCase(localizedValue)) {
                return worldCuisine;
            }
        }
        throw new IllegalArgumentException("Unexpected value: " + localizedValue.toLowerCase(Locale.ROOT));
    }
}
