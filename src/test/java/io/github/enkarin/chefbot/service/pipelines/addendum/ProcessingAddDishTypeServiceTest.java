package io.github.enkarin.chefbot.service.pipelines.addendum;

import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.DishType;
import io.github.enkarin.chefbot.pipelinehandlers.addendum.ProcessingAddDishTypeService;
import io.github.enkarin.chefbot.service.DishService;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.enkarin.chefbot.enums.ChatStatus.NEW_DISH_SPICY;
import static io.github.enkarin.chefbot.enums.ChatStatus.NEW_DISH_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

class ProcessingAddDishTypeServiceTest extends TestBase {

    @Autowired
    private ProcessingAddDishTypeService dishTypeService;

    @Autowired
    private DishService dishService;

    @ParameterizedTest
    @EnumSource(DishType.class)
    void executeShouldWork(final DishType dishType) {
        createUser(NEW_DISH_TYPE);
        dishService.initDishName(USER_ID, "Кимчи");

        assertThat(dishTypeService.execute(USER_ID, dishType.getLocalisedName()).chatStatus()).isEqualTo(NEW_DISH_SPICY);

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

        assertThat(dishTypeService.execute(USER_ID, "aboba").chatStatus()).isEqualTo(NEW_DISH_TYPE);
        assertThat(userRepository.findById(USER_ID).orElseThrow().getEditabledDish().getType()).isNull();
    }
}
