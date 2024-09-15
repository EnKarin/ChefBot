package io.github.enkarin.chefbot;

import io.github.enkarin.chefbot.controllers.TelegramController;
import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ModerationRequestMessageDto;
import io.github.enkarin.chefbot.dto.OperationResult;
import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.ModerationRequestMessage;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.StandardUserAnswerOption;
import io.github.enkarin.chefbot.mappers.ModerationRequestMessageEntityDtoMapper;
import io.github.enkarin.chefbot.util.ModerationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class TelegramControllerTest extends ModerationTest {
    @Autowired
    private TelegramController telegramController;

    @Autowired
    private ModerationRequestMessageEntityDtoMapper mapper;

    @Test
    void executeWorkerCommandStart() {
        final BotAnswer botAnswer = telegramController.executeStartCommand(USER_ID, CHAT_ID, USERNAME);

        assertThat(botAnswer.messageText()).isEqualTo("Приветствую! Здесь вы можете найти блюдо по вашим предпочтениям и поделиться своими рецептами с другими пользователями");
        assertThat(botAnswer.userAnswerOptions().orElseThrow()).hasSize(0);
        assertThat(userRepository.existsById(USER_ID)).isTrue();
    }

    @Test
    void callUndetectableCommand() {
        createUser(ChatStatus.MAIN_MENU);

        assertThat(telegramController.executeWorkerCommand(USER_ID, "/change_moderator_status")).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("Указанной команды не существует");
            assertThat(botAnswer.userAnswerOptions().orElseThrow()).hasSize(0);
        });
    }

    @Test
    void executeBackMainMenuFromMainMenu() {
        createUser(ChatStatus.MAIN_MENU);

        assertThat(telegramController.executeWorkerCommand(USER_ID, "/back_to_main_menu")).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("Вы уже в главном меню");
            assertThat(botAnswer.userAnswerOptions().orElseThrow()).hasSize(0);
        });
    }

    @Test
    void executeBackMainMenuNotFromMainMenu() {
        final Dish dish = dishRepository.save(Dish.builder().dishName("Рагу").build());
        userRepository.save(User.builder().id(USER_ID).chatStatus(ChatStatus.NEW_DISH_NAME).editabledDish(dish).build());

        assertThat(telegramController.executeWorkerCommand(USER_ID, "/back_to_main_menu")).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("Вы хотите вернуться в главное меню?");
            assertThat(botAnswer.userAnswerOptions()).isEqualTo(Optional.of(StandardUserAnswerOption.YES_OR_NO.getAnswers()));
        });
    }

    @Test
    void callCommandNotFromMainMenu() {
        createUser(ChatStatus.NEW_DISH_NAME);

        assertThat(telegramController.executeWorkerCommand(USER_ID, "/change_moderator_status")).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("Эта команда не доступна вне главного меню");
            assertThat(botAnswer.userAnswerOptions().orElseThrow()).hasSize(0);
        });
    }

    @Test
    void callCommandThrowException() {
        assertThat(telegramController.executeWorkerCommand(USER_ID, "/change_moderator_status")).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("Произошла непредвиденная ошибка");
            assertThat(botAnswer.userAnswerOptions().orElseThrow()).hasSize(0);
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
                .extracting(BotAnswer::messageText, BotAnswer::userAnswerOptions)
                .containsOnly("Выберете тип искомого блюда", Optional.of(StandardUserAnswerOption.DISH_TYPES_WITH_ANY_CASE.getAnswers()));
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
                .extracting(BotAnswer::messageText, BotAnswer::userAnswerOptions)
                .containsOnly("Выберете тип искомого блюда", Optional.of(StandardUserAnswerOption.DISH_TYPES_WITH_ANY_CASE.getAnswers()));
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
                .extracting(BotAnswer::messageText, BotAnswer::userAnswerOptions)
                .containsOnly("Введите название блюда", Optional.empty());
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
                .extracting(OperationResult::botAnswer)
                .extracting(BotAnswer::messageText)
                .isEqualTo("Подходящие блюда не найдены. Вы возвращены в главное меню.");
        assertThat(userRepository.findById(USER_ID)).isPresent().get()
                .extracting(User::getChatStatus)
                .isEqualTo(ChatStatus.MAIN_MENU);
    }

    @Test
    void enrichingRecipesCommand() {
        createUser(ChatStatus.MAIN_MENU);
        initDishes();

        assertThat(telegramController.executeWorkerCommand(USER_ID, "/enriching_recipes")).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("Выберете добавленное вами ранее блюдо для добавления рецепта");
            assertThat(botAnswer.userAnswerOptions().orElseThrow()).containsOnly("fifth", "sixth");
        });
        assertThat(userService.findUser(USER_ID).getChatStatus()).isEqualTo(ChatStatus.ENRICHING_RECIPES);
    }

    @Test
    void findAvailableModeratorsId() {
        final long noModeratorId = USER_ID - 5;
        userRepository.save(User.builder().id(USER_ID).chatId(CHAT_ID).username("a").moderator(true).build());
        userRepository.save(User.builder().id(USER_ID - 1).chatId(CHAT_ID - 1).username("b").moderator(true).build());
        userRepository.save(User.builder().id(USER_ID - 2).chatId(CHAT_ID - 2).username("c").moderator(true).build());
        userService.createOrUpdateUser(noModeratorId, CHAT_ID - 5, USERNAME);

        assertThat(telegramController.findAvailableModeratorsId(CHAT_ID))
                .hasSize(2)
                .doesNotContain(noModeratorId, USER_ID);
    }

    @Test
    void addRequestMessages() {
        moderationInit();
        final Set<ModerationRequestMessageDto> messageDtoSet = moderationRequestMessageRepository
            .saveAll(List.of(ModerationRequestMessage.builder().messageId(13).chatId(130).build(), ModerationRequestMessage.builder().messageId(13).chatId(133).build()))
            .stream()
            .map(mapper::entityToDto)
            .collect(Collectors.toSet());

        telegramController.addRequestMessages(moderationRequestsId[0], messageDtoSet);

        assertThat(moderationRequestMessageRepository.findAll()).extracting(ModerationRequestMessage::getChatId).contains(130L, 133L, 10L, 11L);
        assertThat(telegramController.approveModerationRequest(Long.toString(moderationRequestsId[0])).messageForRemove())
                .flatExtracting(Function.identity())
                .contains(messageDtoSet.toArray());
    }

    @Test
    void editDish() {
        createUser(ChatStatus.MAIN_MENU);

        assertThat(telegramController.executeWorkerCommand(USER_ID, "/edit_dish")).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("Введите название блюда, которое вы хотите отредактировать");
            assertThat(botAnswer.userAnswerOptions().orElseThrow()).isEmpty();
        });
        assertThat(userService.findUser(USER_ID).getChatStatus()).isEqualTo(ChatStatus.SELECT_EDITING_DISH_NAME);
    }
}
