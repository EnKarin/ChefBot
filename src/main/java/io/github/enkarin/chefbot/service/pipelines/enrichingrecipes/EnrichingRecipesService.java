package io.github.enkarin.chefbot.service.pipelines.enrichingrecipes;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.service.DishService;
import io.github.enkarin.chefbot.service.pipelines.ProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnrichingRecipesService implements ProcessingService {
    private final DishService dishService;

    @Override
    public ChatStatus execute(final long userId, final String text) {
        dishService.putEditableDish(userId, text);
        return ChatStatus.NEW_DISH_RECIPE;
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return new BotAnswer("Выберете добавленное вами ранее блюдо для добавления рецепта", dishService.findDishesWithoutRecipeForUser(userId));
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.ENRICHING_RECIPES;
    }
}
