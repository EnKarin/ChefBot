package io.github.enkarin.chefbot.service.pipelines.edit;

import io.github.enkarin.chefbot.controllers.pipelines.edit.EditingNameService;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.service.DishService;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EditingNameServiceTest extends TestBase {
    @Autowired
    private EditingNameService editingNameService;

    @Autowired
    private DishService dishService;

    @Test
    void executeWithExistsDish() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);

        assertThatThrownBy(() -> editingNameService.execute(USER_ID, "anyName")).isInstanceOf(NullPointerException.class);
    }

    @Test
    void executeWithCorrectInput() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        dishService.initDishName(USER_ID, "dish1");

        assertThat(editingNameService.execute(USER_ID, "anyName").chatStatus()).isEqualTo(ChatStatus.DISH_NEED_PUBLISH);
        assertThat(userRepository.findById(USER_ID).orElseThrow().getEditabledDish().getDishName()).isEqualTo("anyName");
    }
}
