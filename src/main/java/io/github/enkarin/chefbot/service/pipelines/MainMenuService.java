package io.github.enkarin.chefbot.service.pipelines;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import org.springframework.stereotype.Service;

@Service
public class MainMenuService implements ProcessingService {
    @Override
    public ExecutionResult execute(long userId, String text) {
        return new ExecutionResult(ChatStatus.MAIN_MENU);
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
