package io.github.enkarin.chefbot.enums;

import lombok.Getter;

import java.util.Locale;

@Getter
public enum WorldCuisine {
    ASIA("Азиатская"),
    MEDITERRANEAN("Средиземноморская"),
    INTERNATIONAL("Международная"),
    MIDDLE_EASTERN("Ближневосточная"),
    MEXICAN("Мексиканская"),
    SLAVIC("Славянская"),
    OTHER("Что-то необычное");

    private final String localizedValue;

    WorldCuisine(final String local) {
        localizedValue = local;
    }

    public static WorldCuisine getCuisine(final String localizedValue) {
        return switch (localizedValue.toLowerCase(Locale.ROOT)) {
            case "азиатская" -> ASIA;
            case "средиземноморская" -> MEDITERRANEAN;
            case "международная" -> INTERNATIONAL;
            case "ближневосточная" -> MIDDLE_EASTERN;
            case "мексиканская" -> MEXICAN;
            case "славянская" -> SLAVIC;
            case "что-то необычное" -> OTHER;
            default -> throw new IllegalArgumentException("Unexpected value: " + localizedValue.toLowerCase(Locale.ROOT));
        };
    }
}
