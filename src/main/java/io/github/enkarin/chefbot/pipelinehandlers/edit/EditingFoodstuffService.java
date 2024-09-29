package io.github.enkarin.chefbot.pipelinehandlers.edit;

import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.pipelinehandlers.PutFoodstuffHandler;
import io.github.enkarin.chefbot.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EditingFoodstuffService extends PutFoodstuffHandler {
    private final DishService dishService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        dishService.putDishFoodstuff(userId, parseTextToProductMap(text));
        dishService.dropPublishFlagForEditableDish(userId);
        return new ExecutionResult(ChatStatus.DISH_NEED_PUBLISH);
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.EDITING_FOODSTUFF;
    }
}
