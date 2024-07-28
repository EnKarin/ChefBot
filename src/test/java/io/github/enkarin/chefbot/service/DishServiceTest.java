package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class DishServiceTest extends TestBase {

    @Autowired
    private DishService dishService;

    @Test
    void initDishShouldWork() {
        final String dishName = "Рагу";
        userService.createUser(CHAT_ID);
        dishService.initDishName(CHAT_ID, dishName);

        assertThat(userRepository.findById(CHAT_ID))
                .isPresent()
                .get()
                .satisfies(u -> {
                    assertThat(u.getChatStatus())
                            .isEqualTo(ChatStatus.PROCESSING);
                    assertThat(u.getEditabledDish())
                            .isNotNull();
                    assertThat(dishRepository.findById(u.getEditabledDish().getId()))
                            .isPresent()
                            .get()
                            .extracting(Dish::getDishName)
                            .isEqualTo(dishName);
                });
    }

    @Test
    void deleteDishShouldWork() {
        userService.createUser(CHAT_ID);
        dishService.initDishName(CHAT_ID, "Рагу");

        dishService.deleteDish(CHAT_ID);

        assertThat(userRepository.findById(CHAT_ID))
                .isPresent()
                .get()
                .extracting(User::getEditabledDish, User::getChatStatus)
                .containsOnly(null, ChatStatus.MAIN_MENU);
        assertThat(dishRepository.findAll())
                .isEmpty();
    }
}
