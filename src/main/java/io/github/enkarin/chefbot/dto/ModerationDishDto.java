package io.github.enkarin.chefbot.dto;

import io.github.enkarin.chefbot.enums.WorldCuisine;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class ModerationDishDto {
    private long requestId;
    private long ownerChatId;
    private String name;
    private boolean spicy;
    private boolean soup;
    private WorldCuisine worldCuisine;
    private Set<String> products;

    @Override
    public String toString() {
        return String.format("""
                *Запрос №%d*
                Название: %s
                Острое: %b
                Суп: %b
                Кухня: %s
                Состав: %s""", requestId, name, spicy, soup, worldCuisine.getLocalizedValue(), String.join(", ", products));
    }
}
