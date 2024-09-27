package io.github.enkarin.chefbot.service.pipelines.edit;

import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.pipelinehandlers.edit.SelectEditingFieldService;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static io.github.enkarin.chefbot.enums.ChatStatus.EDITING_FOODSTUFF;
import static io.github.enkarin.chefbot.enums.ChatStatus.EDITING_KITCHEN;
import static io.github.enkarin.chefbot.enums.ChatStatus.EDITING_NAME;
import static io.github.enkarin.chefbot.enums.ChatStatus.EDITING_RECIPE;
import static io.github.enkarin.chefbot.enums.ChatStatus.EDITING_SPICY;
import static io.github.enkarin.chefbot.enums.ChatStatus.EDITING_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

class SelectEditingFieldServiceTest extends TestBase {
    @Autowired
    private SelectEditingFieldService selectEditingFieldService;

    @ParameterizedTest
    @MethodSource("provideTextAndResult")
    void executeShouldWork(final String text, final ChatStatus status) {
        assertThat(selectEditingFieldService.execute(USER_ID, text).chatStatus()).isEqualTo(status);
    }

    static Stream<Arguments> provideTextAndResult() {
        return Stream.of(
                Arguments.of("Список продуктов", EDITING_FOODSTUFF),
                Arguments.of("Острота", EDITING_SPICY),
                Arguments.of("Название", EDITING_NAME),
                Arguments.of("Тип", EDITING_TYPE),
                Arguments.of("Кухня", EDITING_KITCHEN),
                Arguments.of("Рецепт", EDITING_RECIPE)
        );
    }
}
