package io.github.enkarin.chefbot.service.pipelines.edit;

import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class EditingNameServiceTest extends TestBase {
    @Autowired
    private EditingNameService editingNameService;

    @Test
    void executeWithCorrectInput() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);

        assertThat(editingNameService.execute(USER_ID, "anyName").chatStatus()).isEqualTo(ChatStatus.DISH_NEED_PUBLISH);
        assertThat(userRepository.findById(USER_ID).orElseThrow().getEditabledDish().getDishName()).isEqualTo("anyName");
    }
}
