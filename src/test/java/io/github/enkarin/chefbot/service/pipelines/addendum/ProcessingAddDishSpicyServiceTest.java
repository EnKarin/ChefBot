package io.github.enkarin.chefbot.service.pipelines.addendum;

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

import static io.github.enkarin.chefbot.enums.ChatStatus.NEW_DISH_KITCHEN;
import static io.github.enkarin.chefbot.enums.ChatStatus.NEW_DISH_SOUP;
import static io.github.enkarin.chefbot.enums.ChatStatus.NEW_DISH_SPICY;
import static org.assertj.core.api.Assertions.assertThat;

class ProcessingAddDishSpicyServiceTest extends TestBase {

    @Autowired
    private ProcessingAddDishSpicyService addDishSpicyService;

    @Autowired
    private DishService dishService;

    @ParameterizedTest
    @MethodSource("provideTextAndResult")
    void executeShouldWork(final String text, final ChatStatus status, final boolean isSpicy) {
        createUser(NEW_DISH_SOUP);
        dishService.initDishName(USER_ID, "Кимчи");

        assertThat(addDishSpicyService.execute(USER_ID, text))
                .isEqualTo(status);

        assertThat(userRepository.findById(USER_ID))
                .isPresent()
                .get()
                .extracting(User::getEditabledDish)
                .extracting(Dish::isSpicy)
                .isEqualTo(isSpicy);
    }

    static Stream<Arguments> provideTextAndResult() {
        return Stream.of(
                Arguments.of("Да", NEW_DISH_KITCHEN, true),
                Arguments.of("Нет", NEW_DISH_KITCHEN, false),
                Arguments.of("test", NEW_DISH_SPICY, false)
        );
    }
}