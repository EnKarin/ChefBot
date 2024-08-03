package io.github.enkarin.chefbot;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.UserAnswerOption;
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
        userService.createOfUpdateUser(userId, chatId, username);
        return new BotAnswer("Приветствую! Здесь вы можете найти блюдо по вашим предпочтениям и поделиться своими рецептами с другими пользователями");
    }

    public BotAnswer executeWorkerCommand(final long userId, final String text) {
        try {
            if (userService.getChatStatus(userId) == ChatStatus.MAIN_MENU) {
                return switch (text) {
                    case "/back_to_main_menu" -> new BotAnswer("Вы уже в главном меню");
                    default -> new BotAnswer("Указанной команды не существует");
                };
            } else {
                if ("/back_to_main_menu".equals(text)) {
                    userService.switchToNewStatus(userId, ChatStatus.APPROVE_BACK_TO_MAIN_MENU);
                    return new BotAnswer("Вы хотите вернуться в главное меню? Весь прогресс текущей операции будет утерян.", UserAnswerOption.YES_OR_NO);
                } else {
                    return new BotAnswer("Эта команда доступна только в главном меню. " +
                            "Вам необходимо продолжить ввод или вернуться в главное меню c помощью команды /back_to_main_menu");
                }
            }
        } catch (Exception e) {
            log.error(e.toString());
            return new BotAnswer("Произошла непредвиденная ошибка");
        }
    }

    public BotAnswer processingNonCommandInput(final long userId, final String text) {
        return processingFacade.execute(userId, userService.getChatStatus(userId), text);
    }
}
