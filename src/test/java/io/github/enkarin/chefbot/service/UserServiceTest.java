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
                .extracting(User::getChatId)
                .isEqualTo(USER_ID);
    }


    @Test
    void findOrSaveShouldWorkIfUserNotPresent() {
        userService.createOfUpdateUser(USER_ID, CHAT_ID, USERNAME);

        assertThat(userRepository.findAll())
                .hasSize(1)
                .first()
                .extracting(User::getChatId)
                .isEqualTo(USER_ID);
    }

    @Test
    void getAllModeratorsShouldWork() {
        final long noModeratorId = USER_ID - 5;
        userRepository.save(User.builder().chatId(USER_ID).moderator(true).build());
        userRepository.save(User.builder().chatId(USER_ID - 1).moderator(true).build());
        userRepository.save(User.builder().chatId(USER_ID - 2).moderator(true).build());
        userService.createOfUpdateUser(noModeratorId, CHAT_ID, USERNAME);

        assertThat(userService.getAllModerators())
                .hasSize(3)
                .doesNotContain(noModeratorId);
    }

    @Test
    void getChatStatusShouldWork() {
        userService.createOfUpdateUser(USER_ID, CHAT_ID, USERNAME);

        assertThat(userService.getChatStatus(USER_ID))
                .isEqualTo(ChatStatus.MAIN_MENU);
    }

    @Test
    void backToMainMenu() {
        final Dish dish = dishRepository.save(Dish.builder().id(1L).build());
        userRepository.save(User.builder().id(USER_ID).chatStatus(ChatStatus.NEW_DISH_NAME).editabledDish(dish).build());

        userService.backToMainMenu(USER_ID);

        final User user = userRepository.findById(USER_ID).orElseThrow();
        assertThat(user.getEditabledDish()).isNull();
        assertThat(user.getChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
    }
}
