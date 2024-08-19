package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.repository.ModerationRequestRepository;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class NewDishNeedPublishServiceTest extends TestBase {
    @Autowired
    private NewDishNeedPublishService service;

    @Autowired
    private DishService dishService;

    @Autowired
    private ModerationRequestRepository moderationRequestRepository;

    @AfterEach
    void clean() {
        moderationRequestRepository.deleteAll();
    }

    @Test
    void executeWithYes() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        dishService.initDishName(USER_ID, "1 dish");

        assertThat(service.execute(USER_ID, "Да")).isEqualTo(ChatStatus.MAIN_MENU);
        assertThat(moderationRequestRepository.count()).isEqualTo(1);
    }

    @Test
    void executeWithNo() {
        assertThat(service.execute(USER_ID, "нет")).isEqualTo(ChatStatus.MAIN_MENU);
    }

    @Test
    void executeWithIncorrectInput() {
        assertThat(service.execute(USER_ID, "aboba")).isEqualTo(ChatStatus.NEW_DISH_NEED_PUBLISH);
    }
}
