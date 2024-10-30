package io.github.enkarin.chefbot.pipelinehandlers.exclude;

import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.service.ExcludeUserProductsService;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class DeleteExcludeProductsContainsNameInputHandlerTest extends TestBase {
    @Autowired
    private DeleteExcludeProductsContainsNameInputHandler handler;

    @Autowired
    private ExcludeUserProductsService excludeUserProductsService;

    @Test
    void execute() {
        createUser(ChatStatus.DELETE_EXCLUDE_PRODUCTS_CONTAINS_NAME);
        initDishes();
        excludeUserProductsService.addExcludeProducts(USER_ID, "first", "second", "third");

        handler.execute(USER_ID, "firstProduct, thirdProd");

        assertThat(excludeUserProductsService.findExcludeProducts(USER_ID)).containsOnly("secondProduct");
    }
}
