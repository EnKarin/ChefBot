package io.github.enkarin.chefbot.service.pipelines.enrichingrecipes;

import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.pipelinehandlers.enrichingrecipes.EnrichingRecipesService;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class EnrichingRecipesServiceTest extends TestBase {
    @Autowired
    private EnrichingRecipesService enrichingRecipesService;

    @Test
    void execute() {
        createUser(ChatStatus.ENRICHING_RECIPES);
        initDishes();

        assertThat(enrichingRecipesService.execute(USER_ID, "sixth").chatStatus()).isEqualTo(ChatStatus.EXISTS_DISH_PUT_RECIPE);
        assertThat(userService.findUser(USER_ID).getEditabledDish().getDishName()).isEqualTo("sixth");
    }


}
