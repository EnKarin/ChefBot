package io.github.enkarin.chefbot.service.pipelines;

import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.service.DishService;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class ApproveBackToMainMenuServiceTest extends TestBase {
    @Autowired
    private ApproveBackToMainMenuService approveBackToMainMenuService;

    @Autowired
    private DishService dishService;

    @BeforeEach
    void initUser() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
    }

    @Test
    void executeWithYes() {
        assertThat(approveBackToMainMenuService.execute(USER_ID, "Да")).isEqualTo(ChatStatus.MAIN_MENU);
    }

    @Test
    void executeWithUnexpectedInput() {
        assertThat(approveBackToMainMenuService.execute(USER_ID, "Амогус")).isEqualTo(ChatStatus.APPROVE_BACK_TO_MAIN_MENU);
    }

    @Test
    void executeWithYesShouldRemoveEditableDish() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        dishService.initDishName(USER_ID, "Рагу");

        approveBackToMainMenuService.execute(USER_ID, "Да");

        assertThat(userService.findUser(USER_ID).getEditabledDish()).isNull();
    }

    @Test
    void executeWithNo() {
        userService.switchToNewStatus(USER_ID, ChatStatus.NEW_DISH_NAME);
        userService.switchToNewStatus(USER_ID, ChatStatus.APPROVE_BACK_TO_MAIN_MENU);

        assertThat(approveBackToMainMenuService.execute(USER_ID, "Нет")).isEqualTo(ChatStatus.NEW_DISH_NAME);
    }
}
