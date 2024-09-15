package io.github.enkarin.chefbot.service.pipelines.edit;

import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.service.DishService;
import io.github.enkarin.chefbot.service.SearchFilterService;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class EditingFoodstuffServiceTest extends TestBase {
    @Autowired
    private EditingFoodstuffService foodstuffService;

    @Autowired
    private DishService dishService;

    @Autowired
    private SearchFilterService searchFilterService;

    @Test
    void executeWithNewLineDelimiter() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        dishService.initDishName(USER_ID, "dish");
        searchFilterService.createSearchFilter(USER_ID);

        assertThat(foodstuffService.execute(USER_ID, """
                Три ведра укропа
                чесночёк
                Рис""").chatStatus()).isEqualTo(ChatStatus.DISH_NEED_PUBLISH);
        assertThat(searchFilterService.searchDishWithCurrentFilter(USER_ID).stream().flatMap(displayDishDto -> displayDishDto.getProductsName().stream()))
                .containsOnly("Три ведра укропа", "Чесночёк", "Рис");
    }

    @Test
    void executeWithCommaDelimiter() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        dishService.initDishName(USER_ID, "dish");
        searchFilterService.createSearchFilter(USER_ID);

        assertThat(foodstuffService.execute(USER_ID, "Охапка дров, плюмбус").chatStatus()).isEqualTo(ChatStatus.DISH_NEED_PUBLISH);
        assertThat(searchFilterService.searchDishWithCurrentFilter(USER_ID).stream().flatMap(displayDishDto -> displayDishDto.getProductsName().stream()))
                .containsOnly("Охапка дров", "Плюмбус");
    }
}
