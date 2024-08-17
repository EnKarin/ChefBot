package io.github.enkarin.chefbot.controllers;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.exceptions.DishNameAlreadyExistsInCurrentUserException;
import io.github.enkarin.chefbot.service.ProcessingFacade;
import io.github.enkarin.chefbot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramController {
    private final UserService userService;
    private final ProcessingFacade processingFacade;

    public BotAnswer executeStartCommand(final long userId, final long chatId, final String username) {
        userService.createOrUpdateUser(userId, chatId, username);
        return new BotAnswer("Приветствую! Здесь вы можете найти блюдо по вашим предпочтениям и поделиться своими рецептами с другими пользователями");
    }

    public BotAnswer executeWorkerCommand(final long userId, final String text) {
        try {
            if (userService.getChatStatus(userId) == ChatStatus.MAIN_MENU) {
                return switch (text) {
                    case "/back_to_main_menu" -> new BotAnswer("Вы уже в главном меню");
                    case "/undo" -> new BotAnswer("Эта команда не доступна в главном меню");
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
        }
    }
}
