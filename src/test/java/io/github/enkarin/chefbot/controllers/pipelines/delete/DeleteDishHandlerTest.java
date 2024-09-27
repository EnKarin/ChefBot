package io.github.enkarin.chefbot.controllers.pipelines.delete;

import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.exceptions.DishesNotFoundException;
import io.github.enkarin.chefbot.pipelineHandlers.delete.DeleteDishHandler;
import io.github.enkarin.chefbot.service.DishService;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeleteDishHandlerTest extends TestBase {
    @Autowired
    private DeleteDishHandler deleteDishHandler;

    @Autowired
    private DishService dishService;

    @Test
    void executeWishOwnedDish() {
        createUser(ChatStatus.DELETE_DISH);
        initDishes();

        deleteDishHandler.execute(USER_ID, "fifth");

        assertThat(dishService.findDishByName(USER_ID, "fifth")).isEmpty();
    }

    @Test
    void executeWithNotOwnedDish() {
        createUser(ChatStatus.DELETE_DISH);
        initDishes();

        assertThatThrownBy(() -> deleteDishHandler.execute(USER_ID, "first")).isInstanceOf(DishesNotFoundException.class);
    }
}
