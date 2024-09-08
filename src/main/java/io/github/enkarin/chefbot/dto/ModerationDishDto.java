package io.github.enkarin.chefbot.dto;

import io.github.enkarin.chefbot.enums.DishType;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import lombok.Builder;
import lombok.Data;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@Data
@Builder
public class ModerationDishDto {
    private long requestId;
    private long ownerChatId;
    private String name;
    private boolean spicy;
    private DishType type;
    private WorldCuisine worldCuisine;
    private Set<String> products;
    private String recipe;

    @Override
    public String toString() {
        return String.format("""
                        *%s*
                        Острое: %s
                        Тип: %s
                        Кухня: %s
                        Состав: %s
                        %s""",
                name,
                spicy ? "да" : "нет",
                type.getLocalisedName().toLowerCase(Locale.ROOT),
                worldCuisine.getLocalizedValue().toLowerCase(Locale.ROOT),
                String.join(", ", products),
                Objects.isNull(recipe) ? "" : "Рецепт: ".concat(recipe));
    }
}
