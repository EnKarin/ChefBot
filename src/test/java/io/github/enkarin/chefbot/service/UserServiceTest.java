package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class UserServiceTest extends TestBase {
    @Autowired
    private UserService userService;

    @Autowired
    private DishService dishService;

    @Test
    void findOrSaveShouldWork() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);

        assertThat(userRepository.findById(USER_ID)).isPresent();
    }

    @Test
    void createOrUpdateUserShouldWorkForSecondCall() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);

        assertThat(userRepository.findAll())
                .hasSize(1)
                .first()
                .extracting(User::getId)
                .isEqualTo(USER_ID);
    }


    @Test
    void findOrSaveShouldWorkIfUserNotPresent() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);

        assertThat(userRepository.findAll())
                .hasSize(1)
                .first()
                .extracting(User::getId)
                .isEqualTo(USER_ID);
    }

    @Test
    void getAllModeratorsWithoutNonExistUserShouldWork() {
        final long noModeratorId = USER_ID - 5;
        userRepository.save(User.builder().id(USER_ID).chatId(CHAT_ID).username("a").moderator(true).build());
        userRepository.save(User.builder().id(USER_ID - 1).chatId(CHAT_ID - 1).username("b").moderator(true).build());
        userRepository.save(User.builder().id(USER_ID - 2).chatId(CHAT_ID - 2).username("c").moderator(true).build());
        userService.createOrUpdateUser(noModeratorId, CHAT_ID - 5, USERNAME);

        assertThat(userService.getAllModeratorsWithoutCurrentUser(0)).hasSize(3).doesNotContain(noModeratorId);
    }

    @Test
    void getAllModeratorsWithoutCurrentUserShouldWork() {
        final long noModeratorId = USER_ID - 5;
        userRepository.save(User.builder().id(USER_ID).chatId(CHAT_ID).username("a").moderator(true).build());
        userRepository.save(User.builder().id(USER_ID - 1).chatId(CHAT_ID - 1).username("b").moderator(true).build());
        userRepository.save(User.builder().id(USER_ID - 2).chatId(CHAT_ID - 2).username("c").moderator(true).build());
        userService.createOrUpdateUser(noModeratorId, CHAT_ID - 5, USERNAME);

        assertThat(userService.getAllModeratorsWithoutCurrentUser(CHAT_ID))
                .hasSize(2)
                .doesNotContain(noModeratorId, USER_ID);
    }

    @Test
    void getChatStatusShouldWork() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);

        assertThat(userService.getChatStatus(USER_ID)).isEqualTo(ChatStatus.MAIN_MENU);
    }

    @Test
    void switchToNewStatusShouldWork() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);

        userService.switchToNewStatus(USER_ID, ChatStatus.REMOVE_DISH);

        assertThat(userRepository.findById(USER_ID).orElseThrow()).satisfies(user -> {
            assertThat(user.getChatStatus()).isEqualTo(ChatStatus.REMOVE_DISH);
            assertThat(user.getPreviousChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
        });
    }

    @Test
    void switchToCurrentStatusShouldWork() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        userService.switchToNewStatus(USER_ID, ChatStatus.REMOVE_DISH);

        userService.switchToNewStatus(USER_ID, ChatStatus.REMOVE_DISH);

        assertThat(userRepository.findById(USER_ID).orElseThrow()).satisfies(user -> {
            assertThat(user.getChatStatus()).isEqualTo(ChatStatus.REMOVE_DISH);
            assertThat(user.getPreviousChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
        });
    }

    @Test
    void switchToNewStatus() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);

        userService.switchToNewStatus(USER_ID, ChatStatus.REMOVE_DISH);

        assertThat(userRepository.findById(USER_ID).orElseThrow().getPreviousChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
    }

    @Test
    void backToMainMenu() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        userService.switchToNewStatus(USER_ID, ChatStatus.NEW_DISH_NAME);
        dishService.initDishName(USER_ID, "Рагу");

        userService.switchToNewStatus(USER_ID, ChatStatus.MAIN_MENU);

        assertThat(userRepository.findById(USER_ID).orElseThrow())
                .satisfies(user -> {
                    assertThat(user.getEditabledDish()).isNull();
                    assertThat(user.getChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
                    assertThat(user.getPreviousChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
                });
        assertThat(dishRepository.count()).isEqualTo(1);
    }

    @Test
    void canUndoIsTrue() {
        userRepository.save(User.builder().id(USER_ID).chatId(CHAT_ID).username("a").chatStatus(ChatStatus.NEW_DISH_NAME).previousChatStatus(ChatStatus.MAIN_MENU).build());

        assertThat(userService.canUndo(USER_ID)).isTrue();
    }

    @Test
    void canUndoIsFalse() {
        userRepository.save(User.builder().id(USER_ID).chatId(CHAT_ID).username("a").chatStatus(ChatStatus.NEW_DISH_NAME).previousChatStatus(ChatStatus.NEW_DISH_NAME).build());

        assertThat(userService.canUndo(USER_ID)).isFalse();
    }

    @Test
    void backToPreviousStatus() {
        userRepository.save(User.builder().id(USER_ID).chatId(CHAT_ID).username("a").chatStatus(ChatStatus.NEW_DISH_NAME).previousChatStatus(ChatStatus.MAIN_MENU).build());

        assertThat(userService.backToPreviousStatus(USER_ID)).isEqualTo(ChatStatus.MAIN_MENU);

        assertThat(userRepository.findById(USER_ID).orElseThrow()).satisfies(user -> {
            assertThat(user.getChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
            assertThat(user.getPreviousChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
        });
    }

    @Test
    void getPreviousStatus() {
        userRepository.save(User.builder().id(USER_ID).chatId(CHAT_ID).username("a").chatStatus(ChatStatus.NEW_DISH_NAME).previousChatStatus(ChatStatus.MAIN_MENU).build());

        assertThat(userService.getPreviousChatStatus(USER_ID)).isEqualTo(ChatStatus.MAIN_MENU);
    }
}
