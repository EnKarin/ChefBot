package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.UserAnswerOption;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class NewDishNeedPublishService implements ProcessingService {
    @Override
    public ChatStatus execute(long userId, String text) {
        return switch (text.toLowerCase(Locale.ROOT)) {
            case "да" -> null;
            case "нет" -> ChatStatus.MAIN_MENU;
            default -> getCurrentStatus();
        };
    }

    @Override
    public BotAnswer getMessageForUser() {
        return new BotAnswer("Хотите опубликовать это блюдо, чтобы оно было доступно всем пользователям?", UserAnswerOption.YES_OR_NO);
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.NEW_DISH_NEED_PUBLISH;
    }
}