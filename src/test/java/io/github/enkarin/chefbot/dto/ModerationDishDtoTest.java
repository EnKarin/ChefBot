package io.github.enkarin.chefbot.dto;

import io.github.enkarin.chefbot.enums.DishType;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ModerationDishDtoTest {

    @Test
    void testToString() {
        final String stringDto = ModerationDishDto.builder()
                .spicy(false)
                .worldCuisine(WorldCuisine.INTERNATIONAL)
                .type(DishType.SOUP)
                .name("Бульон")
                .products(Set.of("Курица", "Соль", "Вода"))
                .build().toString();
        assertThat(stringDto).startsWith("""
                *Бульон*
                Острое: нет
                Тип: суп
                Кухня: международная
                Состав:""");
        assertThat(stringDto).contains("Курица", "Вода", "Соль");
    }
}
