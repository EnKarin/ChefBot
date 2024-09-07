package io.github.enkarin.chefbot;

import io.github.enkarin.chefbot.controllers.TelegramController;
import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ModerationRequestMessageDto;
import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.UserAnswerOption;
import io.github.enkarin.chefbot.util.ModerationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class TelegramControllerTest extends ModerationTest {
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
        createUser(ChatStatus.MAIN_MENU);

        assertThat(telegramController.executeWorkerCommand(USER_ID, "/change_moderator_status")).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("Указанной команды не существует");
            assertThat(botAnswer.userAnswerOption()).isEqualTo(UserAnswerOption.DEFAULT);
        });
    }

    @Test
    void executeBackMainMenuFromMainMenu() {
        createUser(ChatStatus.MAIN_MENU);

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
        createUser(ChatStatus.NEW_DISH_NAME);

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

        assertThat(telegramController.approveModerationRequest(Long.toString(moderationRequestsId[0]))).satisfies(moderationResultDto -> {
            assertThat(moderationResultDto.isApprove()).isTrue();
            assertThat(moderationResultDto.dishName()).isEqualTo("firstDish");
            assertThat(moderationResultDto.messageForRemove()).extracting(ModerationRequestMessageDto::chatId).contains(10L, 11L);
        });
        assertThat(moderationRequestRepository.existsById(moderationRequestsId[0])).isFalse();
        assertThat(dishRepository.findAll()).anySatisfy(dish -> {
            assertThat(dish.getDishName()).isEqualTo("firstDish");
            assertThat(dish.isPublished()).isTrue();
        });
    }

    @Test
    void declineModerationRequest() {
        moderationInit();

        telegramController.declineModerationRequest(USER_ID, Long.toString(moderationRequestsId[1]));
        assertThat(userService.findUser(USER_ID).getChatStatus()).isEqualTo(ChatStatus.WRITE_DECLINE_MODERATION_REQUEST);
    }

    @Test
    void searchDishShouldUpdateChatStatus() {
        createUser(ChatStatus.MAIN_MENU);

        assertThat(telegramController.executeWorkerCommand(USER_ID, "/search_dish"))
                .extracting(BotAnswer::messageText, BotAnswer::userAnswerOption)
                .containsOnly("Выберете тип искомого блюда", UserAnswerOption.DISH_TYPES_WITH_ANY_CASE);
        assertThat(userRepository.findById(USER_ID))
                .isPresent()
                .get()
                .extracting(User::getChatStatus)
                .isEqualTo(ChatStatus.SELECT_DISH_TYPE);
    }

    @Test
    void searchRecipeShouldUpdateChatStatus() {
        createUser(ChatStatus.MAIN_MENU);

        assertThat(telegramController.executeWorkerCommand(USER_ID, "/search_recipe"))
                .extracting(BotAnswer::messageText, BotAnswer::userAnswerOption)
                .containsOnly("Выберете тип искомого блюда", UserAnswerOption.DISH_TYPES_WITH_ANY_CASE);
        assertThat(userRepository.findById(USER_ID))
                .isPresent()
                .get()
                .extracting(User::getChatStatus)
                .isEqualTo(ChatStatus.SELECT_DISH_TYPE_WITH_RECIPE_SEARCH);
    }

    @Test
    void addDishShouldUpdateShatStatus() {
        createUser(ChatStatus.MAIN_MENU);

        assertThat(telegramController.executeWorkerCommand(USER_ID, "/add_dish"))
                .extracting(BotAnswer::messageText, BotAnswer::userAnswerOption)
                .containsOnly("Введите название блюда", UserAnswerOption.NONE);
        assertThat(userRepository.findById(USER_ID))
                .isPresent()
                .get()
                .extracting(User::getChatStatus)
                .isEqualTo(ChatStatus.NEW_DISH_NAME);
    }

    @Test
    void processingNonCommandInputShouldWorkWithDishNotFoundEx() {
        createUser(ChatStatus.SELECT_DISH_TYPE);
        telegramController.processingNonCommandInput(USER_ID, "Суп");
        userService.switchToNewStatus(USER_ID, ChatStatus.SELECT_DISH_PUBLISHED);

        assertThat(telegramController.processingNonCommandInput(USER_ID, "все блюда"))
                .extracting(BotAnswer::messageText)
                .isEqualTo("Подходящие блюда не найдены. Вы возвращены в главное меню.");
        assertThat(userRepository.findById(USER_ID))
                .isPresent()
                .get()
                .extracting(User::getChatStatus)
                .isEqualTo(ChatStatus.MAIN_MENU);
    }
}
