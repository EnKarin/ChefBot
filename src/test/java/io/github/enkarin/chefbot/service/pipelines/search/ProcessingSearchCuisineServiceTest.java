package io.github.enkarin.chefbot.service.pipelines.search;

import io.github.enkarin.chefbot.entity.SearchFilter;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import io.github.enkarin.chefbot.service.SearchFilterService;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.enkarin.chefbot.enums.ChatStatus.SELECT_DISH_KITCHEN;
import static io.github.enkarin.chefbot.enums.ChatStatus.SELECT_DISH_PUBLISHED;
import static org.assertj.core.api.Assertions.assertThat;

class ProcessingSearchCuisineServiceTest extends TestBase {

    @Autowired
    private ProcessingSearchCuisineService searchCuisineService;

    @Autowired
    private SearchFilterService searchFilterService;

    @BeforeEach
    void init() {
        createUser(SELECT_DISH_KITCHEN);
        searchFilterService.createSearchFilter(USER_ID);
    }

    @ParameterizedTest
    @EnumSource(WorldCuisine.class)
    void executeShouldWork(final WorldCuisine cuisine) {
        assertThat(searchCuisineService.execute(USER_ID, cuisine.getLocalizedValue())).isEqualTo(SELECT_DISH_PUBLISHED);
        assertThat(userRepository.findById(USER_ID))
                .isPresent()
                .get()
                .extracting(User::getSearchFilter)
                .extracting(SearchFilter::getCuisine)
                .isEqualTo(cuisine);
    }

    @Test
    void executeShouldWorkWithUnknownText() {
        assertThat(searchCuisineService.execute(USER_ID, "unknown")).isEqualTo(SELECT_DISH_PUBLISHED);
        assertThat(userRepository.findById(USER_ID).orElseThrow().getSearchFilter().getCuisine()).isNull();
    }
}
