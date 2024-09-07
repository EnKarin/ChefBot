package io.github.enkarin.chefbot.service.pipelines.addendum;

import io.github.enkarin.chefbot.service.DishService;
import io.github.enkarin.chefbot.service.SearchFilterService;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.enkarin.chefbot.enums.ChatStatus.GET_NEED_DISH_RECIPE;
import static io.github.enkarin.chefbot.enums.ChatStatus.NEW_DISH_FOODSTUFF;
import static org.assertj.core.api.Assertions.assertThat;

class ProcessingAddDishFoodstuffServiceTest extends TestBase {
    @Autowired
    private ProcessingAddDishFoodstuffService foodstuffService;

    @Autowired
    private DishService dishService;

    @Autowired
    private SearchFilterService searchFilterService;

    @Test
    void executeWithEmptyInput() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        dishService.initDishName(USER_ID, "dish");
        searchFilterService.createSearchFilter(USER_ID);

        assertThat(foodstuffService.execute(USER_ID, "")).isEqualTo(NEW_DISH_FOODSTUFF);
        assertThat(searchFilterService.searchDishWithCurrentFilter(USER_ID).stream().flatMap(displayDishDto -> displayDishDto.productsName().stream()))
                .isEmpty();
    }

    @Test
    void executeWithNewLineDelimiter() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        dishService.initDishName(USER_ID, "dish");
        searchFilterService.createSearchFilter(USER_ID);

        assertThat(foodstuffService.execute(USER_ID, """
                Три ведра укропа
                чесночёк
                Рис""")).isEqualTo(GET_NEED_DISH_RECIPE);
        assertThat(searchFilterService.searchDishWithCurrentFilter(USER_ID).stream().flatMap(displayDishDto -> displayDishDto.productsName().stream()))
                .containsOnly("Три ведра укропа", "Чесночёк", "Рис");
    }

    @Test
    void executeWithCommaDelimiter() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        dishService.initDishName(USER_ID, "dish");
        searchFilterService.createSearchFilter(USER_ID);

        assertThat(foodstuffService.execute(USER_ID, "Охапка дров, плюмбус")).isEqualTo(GET_NEED_DISH_RECIPE);
        assertThat(searchFilterService.searchDishWithCurrentFilter(USER_ID).stream().flatMap(displayDishDto -> displayDishDto.productsName().stream()))
                .containsOnly("Охапка дров", "Плюмбус");
    }
}
