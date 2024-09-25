package io.github.enkarin.chefbot.service.pipelines.searchbyproduct;

import io.github.enkarin.chefbot.controllers.pipelines.searchbyproduct.RequestProductsForFindDishService;
import io.github.enkarin.chefbot.entity.SearchProduct;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.repository.SearchProductRepository;
import io.github.enkarin.chefbot.service.SearchFilterService;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class RequestProductsForFindDishServiceTest extends TestBase {
    @Autowired
    private RequestProductsForFindDishService requestProductsForFindDishService;

    @Autowired
    private SearchFilterService searchFilterService;

    @Autowired
    private SearchProductRepository searchProductRepository;

    @Test
    void execute() {
        createUser(ChatStatus.REQUEST_PRODUCTS_FOR_FIND_DISH);
        searchFilterService.createSearchFilter(USER_ID);

        requestProductsForFindDishService.execute(USER_ID, "отруби, топлёный жир");

        assertThat(searchProductRepository.findAll()).extracting(SearchProduct::getName).containsOnly("Отруби", "Топлёный жир");
    }
}
