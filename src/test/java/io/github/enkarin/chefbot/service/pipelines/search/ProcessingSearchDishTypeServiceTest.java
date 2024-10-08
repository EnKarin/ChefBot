package io.github.enkarin.chefbot.service.pipelines.search;

import io.github.enkarin.chefbot.entity.SearchFilter;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.DishType;
import io.github.enkarin.chefbot.pipelinehandlers.search.ProcessingSearchDishTypeService;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.enkarin.chefbot.enums.ChatStatus.SELECT_DISH_SPICY;
import static io.github.enkarin.chefbot.enums.ChatStatus.SELECT_DISH_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

class ProcessingSearchDishTypeServiceTest extends TestBase {

    @Autowired
    private ProcessingSearchDishTypeService searchDishTypeService;

    @BeforeEach
    void setUp() {
        createUser(SELECT_DISH_TYPE);
    }

    @ParameterizedTest
    @EnumSource(DishType.class)
    void executeShouldWork(final DishType dishType) {
        assertThat(searchDishTypeService.execute(USER_ID, dishType.getLocalisedName()).chatStatus()).isEqualTo(SELECT_DISH_SPICY);
        assertThat(userRepository.findById(USER_ID))
                .isPresent()
                .get()
                .extracting(User::getSearchFilter)
                .extracting(SearchFilter::getDishType)
                .isEqualTo(dishType);
    }

    @Test
    void executeShouldWorkWithUnknownInput() {
        assertThat(searchDishTypeService.execute(USER_ID, "unknown").chatStatus()).isEqualTo(SELECT_DISH_SPICY);
        assertThat(userRepository.findById(USER_ID))
                .isPresent()
                .get()
                .extracting(User::getSearchFilter)
                .extracting(SearchFilter::getDishType)
                .isNull();
    }
}
