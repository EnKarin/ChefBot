package io.github.enkarin.chefbot.pipelineHandlers.addendum;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.pipelineHandlers.NonCommandInputHandler;
import io.github.enkarin.chefbot.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddNewDishRecipe implements NonCommandInputHandler {
    private final DishService dishService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        if (text.length() <= 2048) {
            dishService.putDishRecipe(userId, text);
            return new ExecutionResult(ChatStatus.DISH_NEED_PUBLISH);
        } else {
            return new ExecutionResult(getCurrentStatus());
        }
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return BotAnswer.createBotAnswerWithoutKeyboard("Введите способ приготовления блюда");
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.NEW_DISH_RECIPE;
    }
}
