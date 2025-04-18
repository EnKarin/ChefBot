package io.github.enkarin.chefbot.service.pipelines;

import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.pipelinehandlers.ApproveBackToMainMenuService;
import io.github.enkarin.chefbot.repository.SearchFilterRepository;
import io.github.enkarin.chefbot.repository.SearchProductRepository;
import io.github.enkarin.chefbot.service.DishService;
import io.github.enkarin.chefbot.service.SearchFilterService;
import io.github.enkarin.chefbot.service.SearchProductService;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class ApproveBackToMainMenuServiceTest extends TestBase {
    @Autowired
    private ApproveBackToMainMenuService approveBackToMainMenuService;

    @Autowired
    private DishService dishService;

    @Autowired
    private SearchFilterService searchFilterService;

    @Autowired
    private SearchFilterRepository searchFilterRepository;

    @Autowired
    private SearchProductService searchProductService;

    @Autowired
    private SearchProductRepository searchProductRepository;

    @BeforeEach
    void initUser() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
    }

    @Test
    void executeWithYes() {
        assertThat(approveBackToMainMenuService.execute(USER_ID, "Да").chatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
    }

    @Test
    void executeWithUnexpectedInput() {
        assertThat(approveBackToMainMenuService.execute(USER_ID, "Амогус").chatStatus()).isEqualTo(ChatStatus.APPROVE_BACK_TO_MAIN_MENU);
    }

    @Test
    void executeWithYesShouldRemoveEditableDish() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        dishService.initDishName(USER_ID, "Рагу");
        userService.switchToNewStatus(USER_ID, ChatStatus.APPROVE_BACK_TO_MAIN_MENU);

        approveBackToMainMenuService.execute(USER_ID, "Да");

        assertThat(dishService.findDishNamesWithoutRecipeForUser(USER_ID)).hasSize(1);
    }

    @Test
    void executeWithNo() {
        userService.switchToNewStatus(USER_ID, ChatStatus.NEW_DISH_NAME);
        userService.switchToNewStatus(USER_ID, ChatStatus.APPROVE_BACK_TO_MAIN_MENU);

        assertThat(approveBackToMainMenuService.execute(USER_ID, "Нет").chatStatus()).isEqualTo(ChatStatus.NEW_DISH_NAME);
    }

    @Test
    void executeWithExistsSearchFilter() {
        searchFilterService.createSearchFilter(USER_ID);

        assertThat(approveBackToMainMenuService.execute(USER_ID, "да").chatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
        assertThat(searchFilterRepository.count()).isEqualTo(0);
    }

    @Test
    void clearSearchProducts() {
        searchProductService.saveProductsForCurrentSearchFilter(USER_ID, "first", "second");

        searchProductService.dropSearchProductForUser(USER_ID);

        assertThat(searchProductRepository.count()).isZero();
    }
}
