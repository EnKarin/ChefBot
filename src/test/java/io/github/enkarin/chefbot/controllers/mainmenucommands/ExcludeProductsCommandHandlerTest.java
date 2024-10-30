package io.github.enkarin.chefbot.controllers.mainmenucommands;

import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class ExcludeProductsCommandHandlerTest extends TestBase {
    @Autowired
    private ExcludeProductsCommandHandler handler;

    @Test
    void execute() {
        createUser(ChatStatus.MAIN_MENU);

        handler.execute(USER_ID);

        assertThat(userService.findUser(USER_ID).getChatStatus()).isEqualTo(ChatStatus.EXCLUDE_PRODUCTS);
    }
}