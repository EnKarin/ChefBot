package io.github.enkarin.chefbot.service.pipelines.search;

import io.github.enkarin.chefbot.entity.SearchFilter;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.pipelinehandlers.search.ProcessingSearchSpicyService;
import io.github.enkarin.chefbot.service.SearchFilterService;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static io.github.enkarin.chefbot.enums.ChatStatus.SELECT_DISH_KITCHEN;
import static io.github.enkarin.chefbot.enums.ChatStatus.SELECT_DISH_SPICY;
import static org.assertj.core.api.Assertions.assertThat;

class ProcessingSearchSpicyServiceTest extends TestBase {

    @Autowired
    private ProcessingSearchSpicyService searchSpicyService;
    @Autowired
    private SearchFilterService searchFilterService;

    @BeforeEach
    void init() {
        createUser(SELECT_DISH_SPICY);
        searchFilterService.createSearchFilter(USER_ID);
    }

    @ParameterizedTest
    @MethodSource("provideTextAndResult")
    void executeShouldWork(final String text, final ChatStatus status, final Boolean isSpicy) {
        assertThat(searchSpicyService.execute(USER_ID, text).chatStatus()).isEqualTo(status);
        assertThat(userRepository.findById(USER_ID))
                .isPresent()
                .get()
                .extracting(User::getSearchFilter)
                .extracting(SearchFilter::getSpicy)
                .isEqualTo(isSpicy);
    }

    static Stream<Arguments> provideTextAndResult() {
        return Stream.of(
                Arguments.of("Да", SELECT_DISH_KITCHEN, true),
                Arguments.of("Нет", SELECT_DISH_KITCHEN, false),
                Arguments.of("любое", SELECT_DISH_KITCHEN, null)
        );
    }


    @Test
    void executeShouldWorkWithUnknownInput() {
        assertThat(searchSpicyService.execute(USER_ID, "unknown").chatStatus()).isEqualTo(SELECT_DISH_SPICY);
        assertThat(userRepository.findById(USER_ID))
                .isPresent()
                .get()
                .extracting(User::getSearchFilter)
                .extracting(SearchFilter::getSpicy)
                .isNull();
    }

}
