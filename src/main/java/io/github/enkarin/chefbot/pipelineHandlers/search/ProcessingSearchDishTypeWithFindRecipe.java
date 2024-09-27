package io.github.enkarin.chefbot.pipelineHandlers.search;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.DishType;
import io.github.enkarin.chefbot.enums.StandardUserAnswerOption;
import io.github.enkarin.chefbot.pipelineHandlers.NonCommandInputHandler;
import io.github.enkarin.chefbot.service.SearchFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessingSearchDishTypeWithFindRecipe implements NonCommandInputHandler {
    private final SearchFilterService filterService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        filterService.createSearchFilterForFindRecipe(userId);
        try {
            filterService.putDishType(userId, DishType.parse(text));
            return new ExecutionResult(ChatStatus.SELECT_DISH_SPICY);
        } catch (IllegalArgumentException e) {
            return new ExecutionResult(ChatStatus.SELECT_DISH_SPICY);
        }
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return new BotAnswer("Выберете тип искомого блюда", StandardUserAnswerOption.DISH_TYPES_WITH_ANY_CASE);
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.SELECT_DISH_TYPE_WITH_RECIPE_SEARCH;
    }
}
