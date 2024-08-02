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
        userService.findOrSaveUser(CHAT_ID);

        assertThat(userRepository.findById(CHAT_ID))
                .isPresent();
    }

    @Test
    void findOrSaveUserShouldWorkForSecondCall() {
        userService.findOrSaveUser(CHAT_ID);
        userService.findOrSaveUser(CHAT_ID);

        assertThat(userRepository.findAll())
                .hasSize(1)
                .first()
                .extracting(User::getChatId)
                .isEqualTo(CHAT_ID);
    }


    @Test
    void findOrSaveShouldWorkIfUserNotPresent() {
        userService.findOrSaveUser(CHAT_ID);

        assertThat(userRepository.findAll())
                .hasSize(1)
                .first()
                .extracting(User::getChatId)
                .isEqualTo(CHAT_ID);
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
    void getAllModeratorsShouldWork() {
        final long noModeratorId = CHAT_ID - 5;
        createModerator(CHAT_ID);
        createModerator(CHAT_ID - 1);
        createModerator(CHAT_ID - 2);
        userService.findOrSaveUser(noModeratorId);

        assertThat(userService.getAllModerators())
                .hasSize(3)
                .doesNotContain(noModeratorId);
    }

    @Test
    void getChatStatusShouldWork() {
        userService.findOrSaveUser(CHAT_ID);

        assertThat(userService.getChatStatus(CHAT_ID))
                .isEqualTo(ChatStatus.MAIN_MENU);
    }

    @Test
    void backToMainMenu() {
        final Dish dish = dishRepository.save(Dish.builder().dishName("Рагу").build());
        userRepository.save(User.builder().chatId(CHAT_ID).chatStatus(ChatStatus.NEW_DISH_NAME).editabledDish(dish).build());

        userService.backToMainMenu(CHAT_ID);

        final User user = userRepository.findById(CHAT_ID).orElseThrow();
        assertThat(user.getEditabledDish()).isNull();
        assertThat(user.getChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
    }

    private void createModerator(final long chatId) {
        userService.findOrSaveUser(chatId);
        userService.changeModeratorStatus(chatId);
    }
}
