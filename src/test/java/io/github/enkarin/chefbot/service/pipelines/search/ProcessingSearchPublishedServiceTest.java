package io.github.enkarin.chefbot.service.pipelines.search;

import io.github.enkarin.chefbot.entity.SearchFilter;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.service.SearchFilterService;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static io.github.enkarin.chefbot.enums.ChatStatus.EXECUTE_RANDOM_SEARCH;
import static io.github.enkarin.chefbot.enums.ChatStatus.EXECUTE_SEARCH;
import static io.github.enkarin.chefbot.enums.ChatStatus.SELECT_DISH_PUBLISHED;
import static org.assertj.core.api.Assertions.assertThat;

class ProcessingSearchPublishedServiceTest extends TestBase {

    @Autowired
    private ProcessingSearchPublishedService searchPublishedService;

    @Autowired
    private SearchFilterService searchFilterService;

    @ParameterizedTest
    @MethodSource("provideTextAndResult")
    void executeShouldWork(final String text, final ChatStatus status, final Boolean searchInPublished) {
        createUser(SELECT_DISH_PUBLISHED);
        searchFilterService.createSearchFilter(USER_ID);

        assertThat(searchPublishedService.execute(USER_ID, text).chatStatus()).isEqualTo(status);
        assertThat(userRepository.findById(USER_ID))
                .isPresent()
                .get()
                .extracting(User::getSearchFilter)
                .extracting(SearchFilter::isSearchFromPublicDish)
                .isEqualTo(searchInPublished);
    }

    static Stream<Arguments> provideTextAndResult() {
        return Stream.of(
                Arguments.of("Все блюда", EXECUTE_SEARCH, true),
                Arguments.of("Все личные блюда", EXECUTE_SEARCH, false),
                Arguments.of("Случайное блюдо", EXECUTE_RANDOM_SEARCH, true),
                Arguments.of("Случайное личное блюдо", EXECUTE_RANDOM_SEARCH, false),
                Arguments.of("gg", SELECT_DISH_PUBLISHED, false)
        );
    }
}
