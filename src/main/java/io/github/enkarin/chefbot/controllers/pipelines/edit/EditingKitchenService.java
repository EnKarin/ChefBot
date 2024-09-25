package io.github.enkarin.chefbot.controllers.pipelines.edit;

import io.github.enkarin.chefbot.controllers.pipelines.ProcessingService;
import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.StandardUserAnswerOption;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import io.github.enkarin.chefbot.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EditingKitchenService implements ProcessingService {
    private final DishService dishService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        try {
            dishService.putDishCuisine(userId, WorldCuisine.getCuisine(text));
            dishService.dropPublishFlagForEditableDish(userId);
            return new ExecutionResult(ChatStatus.DISH_NEED_PUBLISH);
        } catch (IllegalArgumentException e) {
            return new ExecutionResult(getCurrentStatus());
        }
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return new BotAnswer("Введите кухню, к которой относится блюдо", StandardUserAnswerOption.CUISINES);
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.EDITING_KITCHEN;
    }
}
