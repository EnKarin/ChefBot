package io.github.enkarin.chefbot.service.pipelines.addendum;

import io.github.enkarin.chefbot.pipelinehandlers.addendum.ProcessingAddDishFoodstuffHandler;
import io.github.enkarin.chefbot.service.DishService;
import io.github.enkarin.chefbot.service.SearchFilterService;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.enkarin.chefbot.enums.ChatStatus.GET_NEED_DISH_RECIPE;
import static io.github.enkarin.chefbot.enums.ChatStatus.NEW_DISH_FOODSTUFF;
import static org.assertj.core.api.Assertions.assertThat;

class ProcessingAddDishFoodstuffHandlerTest extends TestBase {
    @Autowired
    private ProcessingAddDishFoodstuffHandler foodstuffService;

    @Autowired
    private DishService dishService;

    @Autowired
    private SearchFilterService searchFilterService;

    @Test
    void executeWithEmptyInput() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        dishService.initDishName(USER_ID, "dish");
        searchFilterService.createSearchFilter(USER_ID);

        assertThat(foodstuffService.execute(USER_ID, "").chatStatus()).isEqualTo(NEW_DISH_FOODSTUFF);
        assertThat(searchFilterService.searchDishWithCurrentFilter(USER_ID).stream()
                .flatMap(displayDishDto -> displayDishDto.getProductsName().stream()))
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
                Рис""").chatStatus()).isEqualTo(GET_NEED_DISH_RECIPE);
        assertThat(searchFilterService.searchDishWithCurrentFilter(USER_ID).stream()
                .flatMap(displayDishDto -> displayDishDto.getProductsName().stream()))
                .containsOnly("Три ведра укропа", "Чесночёк", "Рис");
    }

    @Test
    void executeWithCommaDelimiter() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        dishService.initDishName(USER_ID, "dish");
        searchFilterService.createSearchFilter(USER_ID);

        assertThat(foodstuffService.execute(USER_ID, "Охапка дров, плюмбус").chatStatus()).isEqualTo(GET_NEED_DISH_RECIPE);
        assertThat(searchFilterService.searchDishWithCurrentFilter(USER_ID).stream()
                .flatMap(displayDishDto -> displayDishDto.getProductsName().stream()))
                .containsOnly("Охапка дров", "Плюмбус");
    }

    @Test
    void executeWithQuantityProduct() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        dishService.initDishName(USER_ID, "dish");
        searchFilterService.createSearchFilter(USER_ID);

        assertThat(foodstuffService.execute(USER_ID, "Охапка дров - 1 штука, плюмбус: 2 кило").chatStatus()).isEqualTo(GET_NEED_DISH_RECIPE);
        assertThat(searchFilterService.searchDishWithCurrentFilter(USER_ID).stream()
                .flatMap(displayDishDto -> displayDishDto.getProductsName().stream()))
                .containsOnly("Охапка дров: 1 штука", "Плюмбус: 2 кило");
    }
}
