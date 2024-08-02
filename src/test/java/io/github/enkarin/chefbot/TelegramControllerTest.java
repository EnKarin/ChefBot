package io.github.enkarin.chefbot;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.UserAnswerOption;
import io.github.enkarin.chefbot.service.ProcessingFacade;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;

class TelegramControllerTest extends TestBase {
    @MockBean
    private ProcessingFacade processingFacade;

    @Autowired
    private TelegramController telegramController;

    @Test
    void executeWorkerCommandStart() {
        final BotAnswer botAnswer = telegramController.executeWorkerCommand(USER_ID, "/start");

        assertThat(botAnswer.messageText()).isEqualTo("Приветствую! Здесь вы можете найти блюдо по вашим предпочтениям и поделиться своими рецептами с другими пользователями");
        assertThat(botAnswer.userAnswerOption()).isEqualTo(UserAnswerOption.NONE);
        assertThat(userRepository.existsById(USER_ID)).isTrue();
    }

    @Test
    void becomingModerator() {
        userRepository.save(User.builder().id(USER_ID).chatStatus(ChatStatus.MAIN_MENU).build());

        final BotAnswer botAnswer = telegramController.executeWorkerCommand(USER_ID, "/change_moderator_status");

        assertThat(botAnswer.messageText()).isEqualTo("Вы стали модератором!");
        assertThat(botAnswer.userAnswerOption()).isEqualTo(UserAnswerOption.NONE);
    }

    @Test
    void leaveModerator() {
        userRepository.save(User.builder().id(USER_ID).chatStatus(ChatStatus.MAIN_MENU).moderator(true).build());

        final BotAnswer botAnswer = telegramController.executeWorkerCommand(USER_ID, "/change_moderator_status");

        assertThat(botAnswer.messageText()).isEqualTo("Вы больше не модератор");
        assertThat(botAnswer.userAnswerOption()).isEqualTo(UserAnswerOption.NONE);
    }

    @Test
    void executeBackMainMenuFromMainMenu() {
        userRepository.save(User.builder().id(USER_ID).chatStatus(ChatStatus.MAIN_MENU).build());

        final BotAnswer botAnswer = telegramController.executeWorkerCommand(USER_ID, "/back_to_main_menu");

        assertThat(botAnswer.messageText()).isEqualTo("Вы уже в главном меню");
        assertThat(botAnswer.userAnswerOption()).isEqualTo(UserAnswerOption.NONE);
    }

    @Test
    void executeBackMainMenuNotFromMainMenu() {
        final Dish dish = dishRepository.save(Dish.builder().id(1L).build());
        userRepository.save(User.builder().id(USER_ID).chatStatus(ChatStatus.NEW_DISH_NAME).editabledDish(dish).build());

        final BotAnswer botAnswer = telegramController.executeWorkerCommand(USER_ID, "/back_to_main_menu");

        assertThat(botAnswer.messageText()).isEqualTo("Вы возвращены в главное меню");
        assertThat(botAnswer.userAnswerOption()).isEqualTo(UserAnswerOption.NONE);
    }

    @Test
    void executeUndetectableCommand() {
        userRepository.save(User.builder().id(USER_ID).chatStatus(ChatStatus.MAIN_MENU).build());

        final BotAnswer botAnswer = telegramController.executeWorkerCommand(USER_ID, "/aboba");

        assertThat(botAnswer.messageText()).isEqualTo("Указанной команды не существует");
        assertThat(botAnswer.userAnswerOption()).isEqualTo(UserAnswerOption.NONE);
    }

    @Test
    void callCommandNotFromMainMenu() {
        userRepository.save(User.builder().id(USER_ID).chatStatus(ChatStatus.NEW_DISH_NAME).build());

        final BotAnswer botAnswer = telegramController.executeWorkerCommand(USER_ID, "/change_moderator_status");

        assertThat(botAnswer.messageText())
                .isEqualTo("Эта команда доступна только в главном меню. Вам необходимо продолжить ввод или вернуться в главное меню c помощью команды /back_to_main_menu");
        assertThat(botAnswer.userAnswerOption()).isEqualTo(UserAnswerOption.NONE);
    }

    @Test
    void callCommandThrowException() {
        final BotAnswer botAnswer = telegramController.executeWorkerCommand(USER_ID, "/change_moderator_status");

        assertThat(botAnswer.messageText()).isEqualTo("Произошла непредвиденная ошибка");
        assertThat(botAnswer.userAnswerOption()).isEqualTo(UserAnswerOption.NONE);
    }

    @Test
    void processingNotCommandInput() {
        userRepository.save(User.builder().id(USER_ID).chatStatus(ChatStatus.NEW_DISH_NAME).build());

        telegramController.processingNonCommandInput(USER_ID, "test text");

        Mockito.verify(processingFacade).execute(USER_ID, ChatStatus.NEW_DISH_NAME, "test text");
    }
}
