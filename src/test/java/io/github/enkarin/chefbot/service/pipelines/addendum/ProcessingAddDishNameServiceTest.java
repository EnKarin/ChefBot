package io.github.enkarin.chefbot.service.pipelines.addendum;

import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.enkarin.chefbot.enums.ChatStatus.NEW_DISH_NAME;
import static io.github.enkarin.chefbot.enums.ChatStatus.NEW_DISH_SOUP;
import static org.assertj.core.api.Assertions.assertThat;

class ProcessingAddDishNameServiceTest extends TestBase {
    @Autowired
    private ProcessingAddDishNameService processingAddDishNameService;

    @Test
    void executeWithEmptyInput() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);

        assertThat(processingAddDishNameService.execute(USER_ID, "")).isEqualTo(NEW_DISH_NAME);
        assertThat(userRepository.findById(USER_ID).orElseThrow().getEditabledDish()).isNull();
    }

    @Test
    void executeWithCorrectInput() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);

        assertThat(processingAddDishNameService.execute(USER_ID, "anyName")).isEqualTo(NEW_DISH_SOUP);
        assertThat(userRepository.findById(USER_ID).orElseThrow().getEditabledDish().getDishName()).isEqualTo("anyName");
    }
}