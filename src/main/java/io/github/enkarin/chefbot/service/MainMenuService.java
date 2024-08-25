package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.enums.ChatStatus;
import org.springframework.stereotype.Service;

@Service
public class MainMenuService implements ProcessingService {
    @Override
    public ChatStatus execute(long userId, String text) {
        return ChatStatus.MAIN_MENU;
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return BotAnswer.createBotAnswerWithoutKeyboard("Вы в главном меню. Выберете следующую команду для выполнения.");
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.MAIN_MENU;
    }
}
