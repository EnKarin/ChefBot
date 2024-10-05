package io.github.enkarin.chefbot.service.pipelines.searchbyproduct;

import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.StandardUserAnswerOption;
import io.github.enkarin.chefbot.exceptions.DishesNotFoundException;
import io.github.enkarin.chefbot.pipelinehandlers.searchbyproduct.FindDishByProductsResponseService;
import io.github.enkarin.chefbot.service.DishService;
import io.github.enkarin.chefbot.service.SearchProductService;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FindDishByProductsResponseServiceTest extends TestBase {
    @Autowired
    private FindDishByProductsResponseService findDishByProductsResponseService;

    @Autowired
    private SearchProductService searchProductService;

    @Autowired
    private DishService dishService;

    @Test
    void executeWithReturnToMainMenu() {
        assertThat(findDishByProductsResponseService.execute(USER_ID, "Вернуться в главное меню")).extracting(ExecutionResult::chatStatus).isEqualTo(ChatStatus.MAIN_MENU);
    }

    @Test
    void executeWithNextLoad() {
        assertThat(findDishByProductsResponseService.execute(USER_ID, "Ещё")).extracting(ExecutionResult::chatStatus).isEqualTo(ChatStatus.FIND_DISH_BY_PRODUCTS_RESPONSE);
    }

    @Test
    void getMessageUser() {
        createUser(ChatStatus.FIND_DISH_BY_PRODUCTS_RESPONSE);
        initDishes();
        searchProductService.saveProductsForCurrentSearchFilter(USER_ID, "second", "product");

        assertThat(findDishByProductsResponseService.getMessageForUser(USER_ID)).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).startsWith("*second:*\n-secondProduct");
            assertThat(botAnswer.userAnswerOptions().orElseThrow()).isEqualTo(StandardUserAnswerOption.MORE_OR_STOP.getAnswers());
        });
    }

    @Test
    void getMessageUserMustCorrectConcatDishes() {
        createUser(ChatStatus.FIND_DISH_BY_PRODUCTS_RESPONSE);
        initDishes();
        searchProductService.saveProductsForCurrentSearchFilter(USER_ID, "se");

        assertThat(findDishByProductsResponseService.getMessageForUser(USER_ID).messageText()).isEqualTo("""
                *second:*
                -secondProduct
                
                *seventh:*
                -seventhProduct
                Рецепт приготовления:
                Дать настояться месяцок""");
    }

    @Test
    void getMessageUserWithEmptyOutput() {
        createUser(ChatStatus.FIND_DISH_BY_PRODUCTS_RESPONSE);
        initDishes();
        searchProductService.saveProductsForCurrentSearchFilter(USER_ID, "testo");

        assertThatThrownBy(() -> findDishByProductsResponseService.getMessageForUser(USER_ID)).isInstanceOf(DishesNotFoundException.class);
    }
}
