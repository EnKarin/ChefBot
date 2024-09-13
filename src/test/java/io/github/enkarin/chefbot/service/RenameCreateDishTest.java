package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.exceptions.DishNameAlreadyExistsInCurrentUserException;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RenameCreateDishTest extends TestBase {
    @Autowired
    private DishService dishService;

    @Test
    void renameDishShouldWork() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        dishService.initDishName(USER_ID, "Харчи");

        dishService.renameCreatingDish(USER_ID, "Рагу");

        assertThat(userRepository.findById(USER_ID))
                .isPresent()
                .get()
                .satisfies(u -> {
                    assertThat(u.getEditabledDish()).isNotNull();
                    assertThat(u.getEditabledDish().getDishName()).isEqualTo("Рагу");
                    assertThat(u.getEditabledDish().getType()).isNull();
                    assertThat(u.getEditabledDish().isSpicy()).isFalse();
                });
    }

    @Test
    void renameDishShouldThrowExceptionWhereEditableDishNotFound() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);

        assertThatThrownBy(() -> dishService.renameCreatingDish(USER_ID, "Рагу")).isInstanceOf(NullPointerException.class);
    }

    @Test
    void renameDishShouldThrowExceptionWhereDishNameAlreadyExists() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        initDishes();
        dishService.putEditableDish(USER_ID, "fifth");

        assertThatThrownBy(() -> dishService.renameCreatingDish(USER_ID, "sixth")).isInstanceOf(DishNameAlreadyExistsInCurrentUserException.class);
    }
}
