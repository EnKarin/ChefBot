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

import static io.github.enkarin.chefbot.enums.ChatStatus.NEW_DISH_SOUP;
import static io.github.enkarin.chefbot.enums.ChatStatus.NEW_DISH_SPICY;
import static org.assertj.core.api.Assertions.assertThat;

class ProcessingAddDishSoupServiceTest extends TestBase {

    @Autowired
    private ProcessingAddDishSoupService addDishSoupService;

    @Autowired
    private DishService dishService;

    @ParameterizedTest
    @MethodSource("provideTextAndResult")
    void executeShouldWork(final String text, final ChatStatus status, final boolean isSoup) {
        createUser(NEW_DISH_SOUP);
        dishService.initDishName(USER_ID, "Кимчи");

        assertThat(addDishSoupService.execute(USER_ID, text))
                .isEqualTo(status);

        assertThat(userRepository.findById(USER_ID))
                .isPresent()
                .get()
                .extracting(User::getEditabledDish)
                .extracting(Dish::isSoup)
                .isEqualTo(isSoup);
    }

    static Stream<Arguments> provideTextAndResult() {
        return Stream.of(
                Arguments.of("Да", NEW_DISH_SPICY, true),
                Arguments.of("Нет", NEW_DISH_SPICY, false),
                Arguments.of("test", NEW_DISH_SOUP, false)
        );
    }
}
