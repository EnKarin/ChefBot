package io.github.enkarin.chefbot.service.pipelines.addendum;

import io.github.enkarin.chefbot.pipelinehandlers.addendum.ProcessingAddDishNameService;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.enkarin.chefbot.enums.ChatStatus.NEW_DISH_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

class ProcessingAddDishNameServiceTest extends TestBase {
    @Autowired
    private ProcessingAddDishNameService processingAddDishNameService;

    @Test
    void executeWithCorrectInput() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);

        assertThat(processingAddDishNameService.execute(USER_ID, "anyName").chatStatus()).isEqualTo(NEW_DISH_TYPE);
        assertThat(userRepository.findById(USER_ID).orElseThrow().getEditabledDish().getDishName()).isEqualTo("anyName");
    }
}
