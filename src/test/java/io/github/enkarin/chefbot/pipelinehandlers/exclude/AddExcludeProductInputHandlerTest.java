package io.github.enkarin.chefbot.pipelinehandlers.exclude;

import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.service.ExcludeUserProductsService;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class AddExcludeProductInputHandlerTest extends TestBase {
    @Autowired
    private AddExcludeProductInputHandler handler;

    @Autowired
    private ExcludeUserProductsService excludeUserProductsService;

    @Test
    void execute() {
        createUser(ChatStatus.ADD_EXCLUDE_PRODUCTS);
        initDishes();

        handler.execute(USER_ID, "first, second");

        assertThat(excludeUserProductsService.findExcludeProducts(USER_ID)).containsOnly("secondProduct", "firstProduct");
    }
}
