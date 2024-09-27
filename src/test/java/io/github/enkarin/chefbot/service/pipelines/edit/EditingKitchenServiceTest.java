package io.github.enkarin.chefbot.service.pipelines.edit;

import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import io.github.enkarin.chefbot.pipelinehandlers.edit.EditingKitchenService;
import io.github.enkarin.chefbot.service.DishService;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.enkarin.chefbot.enums.ChatStatus.NEW_DISH_KITCHEN;
import static org.assertj.core.api.Assertions.assertThat;

class EditingKitchenServiceTest extends TestBase {
    @Autowired
    private EditingKitchenService editingKitchenService;

    @Autowired
    private DishService dishService;

    @ParameterizedTest
    @EnumSource(WorldCuisine.class)
    void executeShouldWork(final WorldCuisine cuisine) {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        dishService.initDishName(USER_ID, "dish");

        assertThat(editingKitchenService.execute(USER_ID, cuisine.getLocalizedValue()).chatStatus()).isEqualTo(ChatStatus.DISH_NEED_PUBLISH);
        assertThat(userRepository.findById(USER_ID).orElseThrow().getEditabledDish().getCuisine()).isEqualTo(cuisine);
    }

    @Test
    void executeWithUndetectableInput() {
        createUser(NEW_DISH_KITCHEN);
        dishService.initDishName(USER_ID, "dish");

        assertThat(editingKitchenService.execute(USER_ID, "aboba").chatStatus()).isEqualTo(ChatStatus.EDITING_KITCHEN);
        assertThat(userRepository.findById(USER_ID).orElseThrow().getEditabledDish().getCuisine()).isNull();
    }
}
