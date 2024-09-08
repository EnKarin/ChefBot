package io.github.enkarin.chefbot.dto;

import io.github.enkarin.chefbot.enums.DishType;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ModerationDishDtoTest {
    @Test
    void toStringWithoutRecipe() {
        final String stringDto = ModerationDishDto.builder()
                .spicy(false)
                .worldCuisine(WorldCuisine.INTERNATIONAL)
                .type(DishType.SOUP)
                .name("Бульон")
                .products(Set.of("Курица", "Соль", "Вода"))
                .build()
                .toString();
        assertThat(stringDto).startsWith("""
                *Бульон*
                Острое: нет
                Тип: суп
                Кухня: международная
                Состав:""");
        assertThat(stringDto).contains("Курица", "Вода", "Соль");
    }

    @Test
    void toStringWithRecipe() {
        final String stringDto = ModerationDishDto.builder()
                .spicy(false)
                .worldCuisine(WorldCuisine.INTERNATIONAL)
                .type(DishType.SOUP)
                .name("Бульон")
                .products(Set.of("Курица", "Лапша"))
                .recipe("Варить полчаса")
                .build()
                .toString();
        assertThat(stringDto).startsWith("""
                *Бульон*
                Острое: нет
                Тип: суп
                Кухня: международная
                Состав:""");
        assertThat(stringDto).contains("Курица", "Лапша");
        assertThat(stringDto).endsWith("Рецепт: Варить полчаса");
    }
}
