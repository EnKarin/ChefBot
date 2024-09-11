package io.github.enkarin.chefbot.controllers;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ModerationRequestMessageDto;
import io.github.enkarin.chefbot.dto.ModerationResultDto;
import io.github.enkarin.chefbot.dto.OperationResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.exceptions.DishNameAlreadyExistsInCurrentUserException;
import io.github.enkarin.chefbot.exceptions.DishesNotFoundException;
import io.github.enkarin.chefbot.service.ModerationService;
import io.github.enkarin.chefbot.service.UserService;
import io.github.enkarin.chefbot.service.pipelines.ProcessingFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramController {
    private final UserService userService;
    private final ProcessingFacade processingFacade;
    private final ModerationService moderationService;

    public BotAnswer executeStartCommand(final long userId, final long chatId, final String username) {
        userService.createOrUpdateUser(userId, chatId, username);
        return new BotAnswer("Приветствую! Здесь вы можете найти блюдо по вашим предпочтениям и поделиться своими рецептами с другими пользователями");
    }

    public BotAnswer executeWorkerCommand(final long userId, final String text) {
        try {
            if (userService.getChatStatus(userId) == ChatStatus.MAIN_MENU) {
                return executeCommandFromMainMenu(userId, text);
            } else {
                return executeCommandNotFromMainMenu(userId, text);
            }
        } catch (RuntimeException e) {
            log.error(e.toString());
            return new BotAnswer("Произошла непредвиденная ошибка");
        }
    }

    private BotAnswer executeCommandNotFromMainMenu(final long userId, final String text) {
        return switch (text) {
            case "/back_to_main_menu" -> processingFacade.goToStatus(userId, ChatStatus.APPROVE_BACK_TO_MAIN_MENU);
            case "/undo" -> processingFacade.undo(userId);
            default -> new BotAnswer("Эта команда не доступна вне главного меню");
        };
    }

    private BotAnswer executeCommandFromMainMenu(final long userId, final String text) {
        return switch (text) {
            case "/back_to_main_menu" -> new BotAnswer("Вы уже в главном меню");
            case "/search_dish" -> processingFacade.goToStatus(userId, ChatStatus.SELECT_DISH_TYPE);
            case "/search_recipe" -> processingFacade.goToStatus(userId, ChatStatus.SELECT_DISH_TYPE_WITH_RECIPE_SEARCH);
            case "/add_dish" -> processingFacade.goToStatus(userId, ChatStatus.NEW_DISH_NAME);
            case "/undo" -> new BotAnswer("Эта команда не доступна в главном меню");
            case "/enriching_recipes" -> processingFacade.goToStatus(userId, ChatStatus.ENRICHING_RECIPES);
            default -> new BotAnswer("Указанной команды не существует");
        };
    }

    public OperationResult processingNonCommandInput(final long userId, final String text) {
        try {
            return processingFacade.execute(userId, text);
        } catch (DishNameAlreadyExistsInCurrentUserException e) {
            return new OperationResult(new BotAnswer(e.getMessage()));
        } catch (DishesNotFoundException e) {
            processingFacade.goToStatus(userId, ChatStatus.MAIN_MENU);
            return new OperationResult(BotAnswer.createBotAnswerWithoutKeyboard(e.getMessage()));
        }
    }

    public ModerationResultDto approveModerationRequest(final String callbackData) {
        return moderationService.approveRequest(Long.parseLong(callbackData));
    }

    public BotAnswer declineModerationRequest(final long userId, final String callbackData) {
        moderationService.startModerate(userId, Long.parseLong(callbackData));
        return processingFacade.goToStatus(userId, ChatStatus.WRITE_DECLINE_MODERATION_REQUEST);
    }

    public Set<Long> findAvailableModeratorsId(final long chatId) {
        return userService.getAllModeratorsWithoutCurrentUser(chatId);
    }

    public void addRequestMessages(final long requestId, final Set<ModerationRequestMessageDto> moderationRequestMessageDtoSet) {
        moderationService.addRequestMessages(requestId, moderationRequestMessageDtoSet);
    }
}
