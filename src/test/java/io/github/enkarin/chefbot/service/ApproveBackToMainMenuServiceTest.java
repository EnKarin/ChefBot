package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class ApproveBackToMainMenuServiceTest extends TestBase {
    @Autowired
    private ApproveBackToMainMenuService approveBackToMainMenuService;

    @Test
    void executeWithYes() {
        assertThat(approveBackToMainMenuService.execute(0, "Да")).isEqualTo(ChatStatus.MAIN_MENU);
    }

    @Test
    void executeWithUnexpectedInput() {
        assertThat(approveBackToMainMenuService.execute(1, "Амогус")).isEqualTo(ChatStatus.APPROVE_BACK_TO_MAIN_MENU);
    }
}
