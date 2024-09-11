package io.github.enkarin.chefbot.service.pipelines.addendum;

import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.service.DishService;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class AddNewDishRecipeTest extends TestBase {
    @Autowired
    private AddNewDishRecipe addNewDishRecipe;

    @Autowired
    private DishService dishService;

    @Test
    void execute() {
        createUser(ChatStatus.NEW_DISH_RECIPE);
        dishService.initDishName(USER_ID, "Суп");

        assertThat(addNewDishRecipe.execute(USER_ID, "Варить").chatStatus()).isEqualTo(ChatStatus.NEW_DISH_NEED_PUBLISH);
        assertThat(userRepository.findById(USER_ID).orElseThrow().getEditabledDish().getRecipe()).isEqualTo("Варить");
    }
}
