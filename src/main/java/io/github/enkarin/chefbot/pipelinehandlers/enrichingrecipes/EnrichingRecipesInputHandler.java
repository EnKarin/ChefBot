package io.github.enkarin.chefbot.pipelinehandlers.enrichingrecipes;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.exceptions.DishesNotFoundException;
import io.github.enkarin.chefbot.pipelinehandlers.NonCommandInputHandler;
import io.github.enkarin.chefbot.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrichingRecipesInputHandler implements NonCommandInputHandler {
    private final DishService dishService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        if ("Другое".equalsIgnoreCase(text)) {
            return new ExecutionResult(getCurrentStatus());
        } else {
            dishService.putEditableDish(userId, text);
            return new ExecutionResult(ChatStatus.EXISTS_DISH_PUT_RECIPE);
        }
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        final List<String> dishNamesWithoutRecipeForUser = dishService.findDishNamesWithoutRecipeForUser(userId);
        if (dishNamesWithoutRecipeForUser.isEmpty()) {
            throw new DishesNotFoundException();
        }
        if (dishNamesWithoutRecipeForUser.size() == 10) {
            dishNamesWithoutRecipeForUser.add("Другое");
        }
        return new BotAnswer("Выберете добавленное вами ранее блюдо для добавления рецепта", dishNamesWithoutRecipeForUser.toArray(String[]::new));
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.ENRICHING_RECIPES;
    }
}
