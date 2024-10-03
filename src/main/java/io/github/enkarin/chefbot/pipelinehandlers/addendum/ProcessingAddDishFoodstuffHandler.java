package io.github.enkarin.chefbot.pipelinehandlers.addendum;

import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.pipelinehandlers.PutFoodstuffHandler;
import io.github.enkarin.chefbot.service.DishService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessingAddDishFoodstuffHandler extends PutFoodstuffHandler {
    private final DishService dishService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        if (StringUtils.isNoneBlank(text)) {
            dishService.putAllDishFoodstuff(userId, parseTextToProductMap(text));
            return new ExecutionResult(ChatStatus.GET_NEED_DISH_RECIPE);
        }
        return new ExecutionResult(getCurrentStatus());
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.NEW_DISH_FOODSTUFF;
    }
}
