package io.github.enkarin.chefbot.service.pipelines.addendum;

import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.pipelinehandlers.DishNeedPublishService;
import io.github.enkarin.chefbot.repository.ModerationRequestRepository;
import io.github.enkarin.chefbot.service.DishService;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class DishNeedPublishServiceTest extends TestBase {
    @Autowired
    private DishNeedPublishService service;

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

        assertThat(service.execute(USER_ID, "Да").chatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
        assertThat(moderationRequestRepository.count()).isEqualTo(1);
    }

    @Test
    void executeWithNo() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        dishService.initDishName(USER_ID, "1 dish");

        assertThat(service.execute(USER_ID, "нет").chatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
    }

    @Test
    void executeWithIncorrectInput() {
        assertThat(service.execute(USER_ID, "aboba").chatStatus()).isEqualTo(ChatStatus.DISH_NEED_PUBLISH);
    }
}
