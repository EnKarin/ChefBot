package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class UserServiceTest extends TestBase {

    @Autowired
    private UserService userService;

    @Test
    void findOrSaveShouldWork() {
        userService.createOfUpdateUser(USER_ID, CHAT_ID, USERNAME);

        assertThat(userRepository.findById(USER_ID))
                .isPresent();
    }

    @Test
    void createOfUpdateUserShouldWorkForSecondCall() {
        userService.createOfUpdateUser(USER_ID, CHAT_ID, USERNAME);
        userService.createOfUpdateUser(USER_ID, CHAT_ID, USERNAME);

        assertThat(userRepository.findAll())
                .hasSize(1)
                .first()
                .extracting(User::getId)
                .isEqualTo(USER_ID);
    }


    @Test
    void findOrSaveShouldWorkIfUserNotPresent() {
        userService.createOfUpdateUser(USER_ID, CHAT_ID, USERNAME);

        assertThat(userRepository.findAll())
                .hasSize(1)
                .first()
                .extracting(User::getId)
                .isEqualTo(USER_ID);
    }

    @Test
    void getAllModeratorsShouldWork() {
        final long noModeratorId = USER_ID - 5;
        userRepository.save(User.builder().id(USER_ID).chatId(CHAT_ID).username("a").moderator(true).build());
        userRepository.save(User.builder().id(USER_ID - 1).chatId(CHAT_ID - 1).username("b").moderator(true).build());
        userRepository.save(User.builder().id(USER_ID - 2).chatId(CHAT_ID - 2).username("c").moderator(true).build());
        userService.createOfUpdateUser(noModeratorId, CHAT_ID - 5, USERNAME);

        assertThat(userService.getAllModerators())
                .hasSize(3)
                .doesNotContain(noModeratorId);
    }

    @Test
    void getChatStatusShouldWork() {
        userService.createOfUpdateUser(USER_ID, CHAT_ID, USERNAME);

        assertThat(userService.getChatStatus(USER_ID)).isEqualTo(ChatStatus.MAIN_MENU);
    }

    @Test
    void switchToNewStatusShouldWork() {
        userService.createOfUpdateUser(USER_ID, CHAT_ID, USERNAME);

        userService.switchToNewStatus(USER_ID, ChatStatus.REMOVE_DISH);

        final User user = userRepository.findById(USER_ID).orElseThrow();
        assertThat(user.getChatStatus()).isEqualTo(ChatStatus.REMOVE_DISH);
        assertThat(user.getPreviousChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
    }

    @Test
    void switchToCurrentStatusShouldWork() {
        userService.createOfUpdateUser(USER_ID, CHAT_ID, USERNAME);
        userService.switchToNewStatus(USER_ID, ChatStatus.REMOVE_DISH);

        userService.switchToNewStatus(USER_ID, ChatStatus.REMOVE_DISH);

        final User user = userRepository.findById(USER_ID).orElseThrow();
        assertThat(user.getChatStatus()).isEqualTo(ChatStatus.REMOVE_DISH);
        assertThat(user.getPreviousChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
    }

    @Test
    void switchToNewStatus() {
        userService.createOfUpdateUser(USER_ID, CHAT_ID, USERNAME);

        userService.switchToNewStatus(USER_ID, ChatStatus.REMOVE_DISH);

        assertThat(userRepository.findById(USER_ID).orElseThrow().getPreviousChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
    }

    @Test
    void backToMainMenu() {
        final Dish dish = dishRepository.save(Dish.builder().dishName("Рагу").build());
        userRepository.save(User.builder().id(USER_ID).chatStatus(ChatStatus.NEW_DISH_NAME).editabledDish(dish).build());

        userService.switchToNewStatus(USER_ID, ChatStatus.MAIN_MENU);

        final User user = userRepository.findById(USER_ID).orElseThrow();
        assertThat(user.getEditabledDish()).isNull();
        assertThat(user.getChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
        assertThat(user.getPreviousChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
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

        final User user = userRepository.findById(USER_ID).orElseThrow();
        assertThat(user.getChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
        assertThat(user.getPreviousChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
    }

    @Test
    void getPreviousStatus() {
        userRepository.save(User.builder().id(USER_ID).chatId(CHAT_ID).username("a").chatStatus(ChatStatus.NEW_DISH_NAME).previousChatStatus(ChatStatus.MAIN_MENU).build());

        assertThat(userService.getPreviousChatStatus(USER_ID)).isEqualTo(ChatStatus.MAIN_MENU);
    }
}
