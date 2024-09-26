package io.github.enkarin.chefbot.service.pipelines.edit;

import io.github.enkarin.chefbot.controllers.pipelines.edit.EditingSpicyService;
import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.service.DishService;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static io.github.enkarin.chefbot.enums.ChatStatus.DISH_NEED_PUBLISH;
import static io.github.enkarin.chefbot.enums.ChatStatus.EDITING_SPICY;
import static io.github.enkarin.chefbot.enums.ChatStatus.SELECT_DISH_SPICY;
import static org.assertj.core.api.Assertions.assertThat;

class EditingSpicyServiceTest extends TestBase {
    @Autowired
    private EditingSpicyService editingSpicyService;

    @Autowired
    private DishService dishService;

    @ParameterizedTest
    @MethodSource("provideTextAndResult")
    void executeShouldWork(final String text, final ChatStatus status, final boolean isSpicy) {
        createUser(SELECT_DISH_SPICY);
        dishService.initDishName(USER_ID, "Кимчи");

        assertThat(editingSpicyService.execute(USER_ID, text).chatStatus()).isEqualTo(status);

        assertThat(userRepository.findById(USER_ID))
                .isPresent()
                .get()
                .extracting(User::getEditabledDish)
                .extracting(Dish::isSpicy)
                .isEqualTo(isSpicy);
    }

    static Stream<Arguments> provideTextAndResult() {
        return Stream.of(
                Arguments.of("Да", DISH_NEED_PUBLISH, true),
                Arguments.of("Нет", DISH_NEED_PUBLISH, false),
                Arguments.of("test", EDITING_SPICY, false)
        );
    }
}
