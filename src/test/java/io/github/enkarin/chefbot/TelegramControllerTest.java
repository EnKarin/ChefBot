package io.github.enkarin.chefbot;

import io.github.enkarin.chefbot.controllers.TelegramController;
import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ModerationRequestMessageDto;
import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.ModerationRequest;
import io.github.enkarin.chefbot.entity.ModerationRequestMessage;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.UserAnswerOption;
import io.github.enkarin.chefbot.repository.ModerationRequestMessageRepository;
import io.github.enkarin.chefbot.repository.ModerationRequestRepository;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TelegramControllerTest extends TestBase {
    @Autowired
    private TelegramController telegramController;

    @Autowired
    private ModerationRequestRepository moderationRequestRepository;

    @Autowired
    private ModerationRequestMessageRepository moderationRequestMessageRepository;

    private final long[] moderationRequestsId = new long[4];

    @AfterEach
    void clean() {
        moderationRequestRepository.deleteAll();
    }

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

    @Test
    void approveModerationRequest() {
        moderationInit();

        assertThat(telegramController.approveModerationRequest("Подтвердить запрос №" + moderationRequestsId[0])).satisfies(moderationResultDto -> {
            assertThat(moderationResultDto.approve()).isTrue();
            assertThat(moderationResultDto.dishName()).isEqualTo("firstDish");
            assertThat(moderationResultDto.messageForRemove()).extracting(ModerationRequestMessageDto::chatId).contains(10L, 11L);
        });
        assertThat(moderationRequestRepository.existsById(moderationRequestsId[0])).isFalse();
    }

    @Test
    void declineModerationRequest() {
        moderationInit();

        assertThat(telegramController.declineModerationRequest("Отклонить запрос №" + moderationRequestsId[1])).satisfies(moderationResultDto -> {
            assertThat(moderationResultDto.approve()).isFalse();
            assertThat(moderationResultDto.dishName()).isEqualTo("secondDish");
            assertThat(moderationResultDto.messageForRemove()).extracting(ModerationRequestMessageDto::chatId).contains(20L, 22L);
        });
        assertThat(moderationRequestRepository.existsById(moderationRequestsId[1])).isFalse();
    }

    private void moderationInit() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        final User user = userRepository.save(User.builder().moderator(true).id(USER_ID - 1).chatId(CHAT_ID - 1).username(USERNAME + 1).build());
        final ModerationRequest firstModerationRequest = moderationRequestRepository.save(ModerationRequest.builder()
                .fresh(false)
                .moderationDish(dishRepository.save(Dish.builder().owner(user).dishName("firstDish").build()))
                .build());
        moderationRequestMessageRepository.saveAll(List.of(ModerationRequestMessage.builder().messageId(1).chatId(10).currentModerationRequest(firstModerationRequest).build(),
                ModerationRequestMessage.builder().messageId(1).chatId(11).currentModerationRequest(firstModerationRequest).build()));
        moderationRequestsId[0] = firstModerationRequest.getId();
        final ModerationRequest secondModerationRequest = moderationRequestRepository.save(ModerationRequest.builder()
                .fresh(false)
                .moderationDish(dishRepository.save(Dish.builder().owner(user).dishName("secondDish").build()))
                .build());
        moderationRequestMessageRepository.saveAll(List.of(ModerationRequestMessage.builder().messageId(2).chatId(20).currentModerationRequest(secondModerationRequest).build(),
                ModerationRequestMessage.builder().messageId(2).chatId(22).currentModerationRequest(secondModerationRequest).build()));
        moderationRequestsId[1] = secondModerationRequest.getId();
        final ModerationRequest thirdModerationRequest = moderationRequestRepository.save(ModerationRequest.builder()
                .fresh(true)
                .moderationDish(dishRepository.save(Dish.builder().owner(user).dishName("thirdDish").build()))
                .build());
        moderationRequestMessageRepository.saveAll(List.of(ModerationRequestMessage.builder().messageId(3).chatId(30).currentModerationRequest(thirdModerationRequest).build(),
                ModerationRequestMessage.builder().messageId(3).chatId(33).currentModerationRequest(thirdModerationRequest).build()));
        moderationRequestsId[2] = thirdModerationRequest.getId();
        final ModerationRequest fourthModerationRequest = moderationRequestRepository.save(ModerationRequest.builder()
                .fresh(true)
                .moderationDish(dishRepository.save(Dish.builder().owner(user).dishName("fourthDish").build()))
                .build());
        moderationRequestMessageRepository.saveAll(List.of(ModerationRequestMessage.builder().messageId(4).chatId(40).currentModerationRequest(fourthModerationRequest).build(),
                ModerationRequestMessage.builder().messageId(4).chatId(44).currentModerationRequest(fourthModerationRequest).build()));
        moderationRequestsId[3] = fourthModerationRequest.getId();
    }
}
