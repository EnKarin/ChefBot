package io.github.enkarin.chefbot;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.enums.ChatStatus;
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

    public BotAnswer executeCommand(final long chatId, final String text) {
        try {
            if ("/start".equals(text)) {
                userService.findOrSaveUser(chatId);
                return new BotAnswer("Приветствую! Здесь вы можете найти блюдо по вашим предпочтениям и поделиться своими рецептами с другими пользователями");
            }
            if (userService.getChatStatus(chatId) == ChatStatus.MAIN_MENU) {
                return switch (text) {
                    case "/change_moderator_status" -> new BotAnswer(userService.changeModeratorStatus(chatId) ? "Вы стали модератором!" : "Вы больше не модератор");
                    case "/back_to_main_menu" -> new BotAnswer("Вы уже в главном меню");
                    default -> new BotAnswer("Указанной команды не существует");
                };
            } else {
                if ("/back_to_main_menu".equals(text)) {
                    userService.backToMainMenu(chatId);
                    return new BotAnswer("Вы возвращены в главное меню");
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

    public BotAnswer processingNonCommandInput(final long chatId, final String text) {
        return processingFacade.execute(chatId, userService.getChatStatus(chatId), text);
    }
}
