package io.github.enkarin.chefbot.controllers.pipelines.edit;

import io.github.enkarin.chefbot.controllers.pipelines.NonCommandInputHandler;
import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EditingFoodstuffService implements NonCommandInputHandler {
    private final DishService dishService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        dishService.putDishFoodstuff(userId, text.split("[,\n]"));
        dishService.dropPublishFlagForEditableDish(userId);
        return new ExecutionResult(ChatStatus.DISH_NEED_PUBLISH);
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return BotAnswer.createBotAnswerWithoutKeyboard("Введите список продуктов");
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.EDITING_FOODSTUFF;
    }
}
