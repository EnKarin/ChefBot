package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class UserServiceTest extends TestBase {

    @Autowired
    private UserService userService;

    @Test
    void createUserShouldWork() {
        userService.createUser(CHAT_ID);

        assertThat(userRepository.findById(CHAT_ID))
                .isPresent();
    }

    @Test
    void changeModeratorStatusSetTrueShouldWork() {
        createModerator(CHAT_ID);

        assertThat(userRepository.findById(CHAT_ID))
                .isPresent()
                .get()
                .extracting(User::isModerator)
                .isEqualTo(true);
    }

    @Test
    void changeModeratorStatusShouldNotThrowExceptionIfUserNotFound() {
        assertThatCode(() -> userService.changeModeratorStatus(CHAT_ID))
                .doesNotThrowAnyException();
    }

    @Test
    void getAllModeratorsShouldWork() {
        final long noModeratorId = CHAT_ID - 5;
        createModerator(CHAT_ID);
        createModerator(CHAT_ID - 1);
        createModerator(CHAT_ID - 2);
        userService.createUser(noModeratorId);

        assertThat(userService.getAllModerators())
                .hasSize(3)
                .doesNotContain(noModeratorId);
    }

    private void createModerator(final long chatId) {
        userService.createUser(chatId);
        userService.changeModeratorStatus(chatId);
    }
}
