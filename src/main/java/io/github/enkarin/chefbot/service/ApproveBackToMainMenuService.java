package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.UserAnswerOption;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ApproveBackToMainMenuService implements ProcessingService {
    private final UserService userService;

    @Override
    public ChatStatus execute(final long userId, final String text) {
        return switch (text.toLowerCase(Locale.ROOT)) {
            case "да" -> ChatStatus.MAIN_MENU;
            case "нет" -> userService.getPreviousChatStatus(userId);
            default -> getCurrentStatus();
        };
    }

    @Override
    public BotAnswer getMessageForUser() {
        return new BotAnswer("Вы хотите вернуться в главное меню? Весь прогресс текущей операции будет утерян.", UserAnswerOption.YES_OR_NO);
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.APPROVE_BACK_TO_MAIN_MENU;
    }
}
