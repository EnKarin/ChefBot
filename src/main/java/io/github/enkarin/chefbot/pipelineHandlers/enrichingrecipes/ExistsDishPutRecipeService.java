package io.github.enkarin.chefbot.pipelineHandlers.enrichingrecipes;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.pipelineHandlers.NonCommandInputHandler;
import io.github.enkarin.chefbot.service.DishService;
import io.github.enkarin.chefbot.service.ModerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExistsDishPutRecipeService implements NonCommandInputHandler {
    private final DishService dishService;
    private final ModerationService moderationService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        if (text.length() <= 2048) {
            dishService.putDishRecipe(userId, text);
            if (dishService.editableDishWasPublish(userId)) {
                dishService.dropPublishFlagForEditableDish(userId);
                return new ExecutionResult(ChatStatus.MAIN_MENU, moderationService.createModerationRequest(userId));
            } else {
                return new ExecutionResult(ChatStatus.MAIN_MENU);
            }
        } else {
            return new ExecutionResult(getCurrentStatus());
        }
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return BotAnswer.createBotAnswerWithoutKeyboard("Введите способ приготвления блюда");
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.EXISTS_DISH_PUT_RECIPE;
    }
}
