package io.github.enkarin.chefbot.service.pipelines.edit;

import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class SelectEditingDishNameServiceTest extends TestBase {
    @Autowired
    private SelectEditingDishNameService service;

    @Test
    void execute() {
        createUser(ChatStatus.ENRICHING_RECIPES);
        initDishes();

        assertThat(service.execute(USER_ID, "fifth").chatStatus()).isEqualTo(ChatStatus.SELECT_EDITING_FIELD);
        assertThat(userService.findUser(USER_ID).getEditabledDish().getDishName()).isEqualTo("fifth");
    }
}
