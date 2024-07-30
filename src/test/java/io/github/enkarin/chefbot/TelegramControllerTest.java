package io.github.enkarin.chefbot;

import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class TelegramControllerTest extends TestBase {
    @Autowired
    private TelegramController telegramController;

    @Test
    void executeCommandStart() {
        assertThat(telegramController.executeCommand(CHAT_ID, "/start"))
                .isEqualTo("Приветствую! Здесь вы можете найти блюдо по вашим предпочтениям и поделиться своими рецептами с другими пользователями");
        assertThat(userRepository.existsById(CHAT_ID)).isTrue();
    }

    @Test
    void becomingModerator() {
        userRepository.save(User.builder().chatId(CHAT_ID).chatStatus(ChatStatus.MAIN_MENU).build());

        assertThat(telegramController.executeCommand(CHAT_ID, "/change_moderator_status")).isEqualTo("Вы стали модератором!");
    }

    @Test
    void leaveModerator() {
        userRepository.save(User.builder().chatId(CHAT_ID).chatStatus(ChatStatus.MAIN_MENU).moderator(true).build());

        assertThat(telegramController.executeCommand(CHAT_ID, "/change_moderator_status")).isEqualTo("Вы больше не модератор");
    }

    @Test
    void executeBackMainMenuFromMainMenu() {
        userRepository.save(User.builder().chatId(CHAT_ID).chatStatus(ChatStatus.MAIN_MENU).build());

        assertThat(telegramController.executeCommand(CHAT_ID, "/back_to_main_menu")).isEqualTo("Вы уже в главном меню");
    }

    @Test
    void executeBackMainMenuNotFromMainMenu() {
        final Dish dish = dishRepository.save(Dish.builder().id(1L).build());
        userRepository.save(User.builder().chatId(CHAT_ID).chatStatus(ChatStatus.PROCESSING).editabledDish(dish).build());

        assertThat(telegramController.executeCommand(CHAT_ID, "/back_to_main_menu")).isEqualTo("Вы возвращены в главное меню");
    }

    @Test
    void executeUndetectableCommand() {
        userRepository.save(User.builder().chatId(CHAT_ID).chatStatus(ChatStatus.MAIN_MENU).build());

        assertThat(telegramController.executeCommand(CHAT_ID, "/aboba")).isEqualTo("Указанной команды не существует");
    }

    @Test
    void callCommandNotFromMainMenu() {
        userRepository.save(User.builder().chatId(CHAT_ID).chatStatus(ChatStatus.PROCESSING).build());

        assertThat(telegramController.executeCommand(CHAT_ID, "/change_moderator_status"))
                .isEqualTo("Эта команда доступна только в главном меню. Вам необходимо продолжить ввод или вернуться в главное меню c помощью команды /back_to_main_menu");
    }

    @Test
    void callCommandThrowException() {
        assertThat(telegramController.executeCommand(CHAT_ID, "/change_moderator_status")).isEqualTo("Произошла непредвиденная ошибка");
    }
}
