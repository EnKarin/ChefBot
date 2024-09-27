package io.github.enkarin.chefbot.service.pipelines;

import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.pipelinehandlers.MainMenuService;
import io.github.enkarin.chefbot.util.TestBase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MainMenuServiceTest extends TestBase {
    @Autowired
    private MainMenuService mainMenuService;

    @Test
    void execute() {
        Assertions.assertThat(mainMenuService.execute(0, null).chatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
    }
}
