package io.github.enkarin.chefbot.controllers;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ModerationResultDto;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.exceptions.DishNameAlreadyExistsInCurrentUserException;
import io.github.enkarin.chefbot.exceptions.DishesNotFoundException;
import io.github.enkarin.chefbot.service.ModerationService;
import io.github.enkarin.chefbot.service.UserService;
import io.github.enkarin.chefbot.service.pipelines.ProcessingFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
                return switch (text) {
                    case "/back_to_main_menu" -> new BotAnswer("Вы уже в главном меню");
                    case "/search_dish" -> processingFacade.goToStatus(userId, ChatStatus.SELECT_DISH_TYPE);
                    case "/search_recipe" -> processingFacade.goToStatus(userId, ChatStatus.SELECT_DISH_TYPE_WITH_RECIPE_SEARCH);
                    case "/add_dish" -> processingFacade.goToStatus(userId, ChatStatus.NEW_DISH_NAME);
                    case "/undo" -> new BotAnswer("Эта команда не доступна в главном меню");
                    case "/enriching_recipes" -> processingFacade.goToStatus(userId, ChatStatus.ENRICHING_RECIPES);
                    default -> new BotAnswer("Указанной команды не существует");
                };
            } else {
                return switch (text) {
                    case "/back_to_main_menu" -> processingFacade.goToStatus(userId, ChatStatus.APPROVE_BACK_TO_MAIN_MENU);
                    case "/undo" -> processingFacade.undo(userId);
                    default -> new BotAnswer("Эта команда не доступна вне главного меню");
                };
            }
        } catch (Exception e) {
            log.error(e.toString());
            return new BotAnswer("Произошла непредвиденная ошибка");
        }
    }

    public BotAnswer processingNonCommandInput(final long userId, final String text) {
        try {
            return processingFacade.execute(userId, text);
        } catch (DishNameAlreadyExistsInCurrentUserException e) {
            return new BotAnswer(e.getMessage());
        } catch (DishesNotFoundException e) {
            processingFacade.goToStatus(userId, ChatStatus.MAIN_MENU);
            return BotAnswer.createBotAnswerWithoutKeyboard(e.getMessage());
        }
    }

    public ModerationResultDto approveModerationRequest(final String callbackData) {
        return moderationService.approveRequest(Long.parseLong(callbackData));
    }

    public void declineModerationRequest(final long userId, final String callbackData) {
        moderationService.startModerate(userId, Long.parseLong(callbackData));
        processingFacade.goToStatus(userId, ChatStatus.WRITE_DECLINE_MODERATION_REQUEST);
    }
}
