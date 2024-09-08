package io.github.enkarin.chefbot.service.pipelines.enrichingrecipes;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.service.DishService;
import io.github.enkarin.chefbot.service.ModerationService;
import io.github.enkarin.chefbot.service.pipelines.ProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnrichingRecipesService implements ProcessingService {
    private final DishService dishService;
    private final ModerationService moderationService;

    @Override
    public ChatStatus execute(final long userId, final String text) {
        dishService.putEditableDish(userId, text);
        if (dishService.editableDishWasPublish(userId)) {
            dishService.putNonPublishFlagForEditableDish(userId);
            moderationService.createModerationRequest(userId);
        }
        return ChatStatus.MAIN_MENU;
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
