package io.github.enkarin.chefbot;

import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramController {
    private final UserService userService;

    public String executeCommand(final long chatId, final String text) {
        try {
            if ("/start".equals(text)) {
                userService.findOrSaveUser(chatId);
                return "Приветствую! Здесь вы можете найти блюдо по вашим предпочтениям и поделиться своими рецептами с другими пользователями";
            }
            if (userService.getChatStatus(chatId) == ChatStatus.MAIN_MENU) {
                return switch (text) {
                    case "/change_moderator_status" -> userService.changeModeratorStatus(chatId) ? "Вы стали модератором!" : "Вы больше не модератор";
                    case "/back_to_main_menu" -> "Вы уже в главном меню";
                    default -> "Указанной команды не существует";
                };
            } else {
                if ("/back_to_main_menu".equals(text)) {
                    userService.backToMainMenu(chatId);
                    return "Вы возвращены в главное меню";
                } else {
                    return "Эта команда доступна только в главном меню. Вам необходимо продолжить ввод или вернуться в главное меню c помощью команды /back_to_main_menu";
                }
            }
        } catch (Exception e) {
            log.error(e.toString());
            return "Произошла непредвиденная ошибка";
        }
    }
}
