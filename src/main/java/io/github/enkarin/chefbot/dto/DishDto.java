package io.github.enkarin.chefbot.dto;

import io.github.enkarin.chefbot.enums.WorldCuisine;
import lombok.Data;

import java.util.Set;

@Data
public class DishDto {
    private String name;
    private boolean spicy;
    private boolean soup;
    private WorldCuisine worldCuisine;
    private Set<String> products;

    @Override
    public String toString() {
        return String.format("""
                *%s*
                Острое: %b
                Суп: %b
                Кухня: %s
                Состав: %s""", name, spicy, soup, worldCuisine.getLocalizedValue(), String.join(", ", products));
    }
}
