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
                *%s*
                Острое: %s
                Суп: %s
                Кухня: %s
                Состав: %s""", name, spicy ? "да" : "нет", soup ? "да" : "нет", worldCuisine.getLocalizedValue(), String.join(", ", products));
    }
}
