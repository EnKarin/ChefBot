package io.github.enkarin.chefbot.service.pipelines.addendum;

import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.pipelinehandlers.addendum.ProcessingNeedDishRecipe;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static io.github.enkarin.chefbot.enums.ChatStatus.DISH_NEED_PUBLISH;
import static io.github.enkarin.chefbot.enums.ChatStatus.GET_NEED_DISH_RECIPE;
import static org.assertj.core.api.Assertions.assertThat;

class ProcessingNeedDishRecipeTest extends TestBase {
    @Autowired
    private ProcessingNeedDishRecipe processingNeedDishRecipe;

    @ParameterizedTest
    @MethodSource("provideTextAndResult")
    void execute(final String text, final ChatStatus status) {
        createUser(GET_NEED_DISH_RECIPE);

        assertThat(processingNeedDishRecipe.execute(USER_ID, text).chatStatus()).isEqualTo(status);
    }

    static Stream<Arguments> provideTextAndResult() {
        return Stream.of(
                Arguments.of("Да", ChatStatus.NEW_DISH_RECIPE),
                Arguments.of("Нет", DISH_NEED_PUBLISH),
                Arguments.of("test", GET_NEED_DISH_RECIPE));
    }
}
