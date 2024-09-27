package io.github.enkarin.chefbot.pipelinehandlers.edit;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.pipelinehandlers.NonCommandInputHandler;
import io.github.enkarin.chefbot.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EditingNameService implements NonCommandInputHandler {
    private final DishService dishService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        dishService.renameCreatingDish(userId, text);
        dishService.dropPublishFlagForEditableDish(userId);
        return new ExecutionResult(ChatStatus.DISH_NEED_PUBLISH);
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return BotAnswer.createBotAnswerWithoutKeyboard("Введите новое название блюда");
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.EDITING_NAME;
    }
}
