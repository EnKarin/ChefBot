package io.github.enkarin.chefbot.enums;

import lombok.Getter;

@Getter
public enum WorldCuisine {
    ASIA("Азиатская"),
    MEDITERRANEAN("Средиземноморская"),
    INTERNATIONAL("Международная"),
    MIDDLE_EASTERN("Ближневосточная"),
    MEXICAN("Мексиканская"),
    RUSSIAN("Славянская"),
    OTHER("Что-то необычное");

    private final String localizedValue;

    WorldCuisine(final String local) {
        localizedValue = local;
    }
}
