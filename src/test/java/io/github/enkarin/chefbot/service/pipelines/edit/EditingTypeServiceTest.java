package io.github.enkarin.chefbot.service.pipelines.edit;

import io.github.enkarin.chefbot.controllers.pipelines.edit.EditingTypeService;
import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.DishType;
import io.github.enkarin.chefbot.service.DishService;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.enkarin.chefbot.enums.ChatStatus.NEW_DISH_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

class EditingTypeServiceTest extends TestBase {
    @Autowired
    private EditingTypeService editingTypeService;

    @Autowired
    private DishService dishService;

    @ParameterizedTest
    @EnumSource(DishType.class)
    void executeShouldWork(final DishType dishType) {
        createUser(ChatStatus.EDITING_TYPE);
        dishService.initDishName(USER_ID, "Кимчи");

        assertThat(editingTypeService.execute(USER_ID, dishType.getLocalisedName()).chatStatus()).isEqualTo(ChatStatus.DISH_NEED_PUBLISH);

        assertThat(userRepository.findById(USER_ID))
                .isPresent()
                .get()
                .extracting(User::getEditabledDish)
                .extracting(Dish::getType)
                .isEqualTo(dishType);
    }

    @Test
    void executeWithUndetectableInput() {
        createUser(NEW_DISH_TYPE);
        dishService.initDishName(USER_ID, "dish");

        assertThat(editingTypeService.execute(USER_ID, "aboba").chatStatus()).isEqualTo(ChatStatus.EDITING_TYPE);
        assertThat(userRepository.findById(USER_ID).orElseThrow().getEditabledDish().getType()).isNull();
    }
}
