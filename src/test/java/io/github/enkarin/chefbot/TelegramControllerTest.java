package io.github.enkarin.chefbot;

import io.github.enkarin.chefbot.controllers.TelegramController;
import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.UserAnswerOption;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TelegramControllerTest extends TestBase {

    @Autowired
    private TelegramController telegramController;

    @Test
    void executeWorkerCommandStart() {
        final BotAnswer botAnswer = telegramController.executeStartCommand(USER_ID, CHAT_ID, USERNAME);

        assertThat(botAnswer.messageText()).isEqualTo("Приветствую! Здесь вы можете найти блюдо по вашим предпочтениям и поделиться своими рецептами с другими пользователями");
        assertThat(botAnswer.userAnswerOption()).isEqualTo(UserAnswerOption.DEFAULT);
        assertThat(userRepository.existsById(USER_ID)).isTrue();
    }

    @Test
    void callUndetectableCommand() {
        userRepository.save(User.builder().id(USER_ID).chatStatus(ChatStatus.MAIN_MENU).build());

        assertThat(telegramController.executeWorkerCommand(USER_ID, "/change_moderator_status")).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("Указанной команды не существует");
            assertThat(botAnswer.userAnswerOption()).isEqualTo(UserAnswerOption.DEFAULT);
        });
    }

    @Test
    void executeBackMainMenuFromMainMenu() {
        userRepository.save(User.builder().id(USER_ID).chatStatus(ChatStatus.MAIN_MENU).build());

        assertThat(telegramController.executeWorkerCommand(USER_ID, "/back_to_main_menu")).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("Вы уже в главном меню");
            assertThat(botAnswer.userAnswerOption()).isEqualTo(UserAnswerOption.DEFAULT);
        });
    }

    @Test
    void executeBackMainMenuNotFromMainMenu() {
        final Dish dish = dishRepository.save(Dish.builder().dishName("Рагу").build());
        userRepository.save(User.builder().id(USER_ID).chatStatus(ChatStatus.NEW_DISH_NAME).editabledDish(dish).build());

        assertThat(telegramController.executeWorkerCommand(USER_ID, "/back_to_main_menu")).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("Вы хотите вернуться в главное меню? Весь прогресс текущей операции будет утерян.");
            assertThat(botAnswer.userAnswerOption()).isEqualTo(UserAnswerOption.YES_OR_NO);
        });
    }

    @Test
    void callCommandNotFromMainMenu() {
        userRepository.save(User.builder().id(USER_ID).chatStatus(ChatStatus.NEW_DISH_NAME).build());

        assertThat(telegramController.executeWorkerCommand(USER_ID, "/change_moderator_status")).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("Эта команда не доступна вне главного меню");
            assertThat(botAnswer.userAnswerOption()).isEqualTo(UserAnswerOption.DEFAULT);
        });
    }

    @Test
    void callCommandThrowException() {
        assertThat(telegramController.executeWorkerCommand(USER_ID, "/change_moderator_status")).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("Произошла непредвиденная ошибка");
            assertThat(botAnswer.userAnswerOption()).isEqualTo(UserAnswerOption.DEFAULT);
        });
    }

    @Test
    void processingNotCommandInput() {
        userRepository.save(User.builder().id(USER_ID).chatStatus(ChatStatus.NEW_DISH_NAME).build());

        assertThatThrownBy(() -> telegramController.processingNonCommandInput(USER_ID, "test text"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void callUndoFromMainMenu() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);

        assertThat(telegramController.executeWorkerCommand(USER_ID, "/undo").messageText()).isEqualTo("Эта команда не доступна в главном меню");
    }

    @Test
    void callUndoNotFromMainMenu() {
        userRepository.save(User.builder().id(USER_ID).chatStatus(ChatStatus.NEW_DISH_NAME).previousChatStatus(ChatStatus.NEW_DISH_NAME).build());

        assertThat(telegramController.executeWorkerCommand(USER_ID, "/undo").messageText()).isEqualTo("Отменить действие можно лишь один раз подряд");
    }
}
