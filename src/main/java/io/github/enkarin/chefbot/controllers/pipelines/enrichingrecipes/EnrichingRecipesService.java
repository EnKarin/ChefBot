package io.github.enkarin.chefbot.controllers.pipelines.enrichingrecipes;

import io.github.enkarin.chefbot.controllers.pipelines.ProcessingService;
import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnrichingRecipesService implements ProcessingService {
    private final DishService dishService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        dishService.putEditableDish(userId, text);
        return new ExecutionResult(ChatStatus.EXISTS_DISH_PUT_RECIPE);
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return new BotAnswer("Выберете добавленное вами ранее блюдо для добавления рецепта", dishService.findDishNamesWithoutRecipeForUser(userId));
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.ENRICHING_RECIPES;
    }
}
