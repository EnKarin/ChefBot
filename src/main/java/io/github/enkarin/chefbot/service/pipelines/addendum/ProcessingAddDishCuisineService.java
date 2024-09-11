package io.github.enkarin.chefbot.service.pipelines.addendum;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.StandardUserAnswerOption;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import io.github.enkarin.chefbot.service.DishService;
import io.github.enkarin.chefbot.service.pipelines.ProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessingAddDishCuisineService implements ProcessingService {

    private final DishService dishService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        try {
            final WorldCuisine cuisine = WorldCuisine.getCuisine(text);
            dishService.putDishCuisine(userId, cuisine);
            return new ExecutionResult(ChatStatus.NEW_DISH_FOODSTUFF);
        } catch (IllegalArgumentException e) {
            return new ExecutionResult(getCurrentStatus());
        }
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return new BotAnswer("Блюдо какой кухни вы добавляете?", StandardUserAnswerOption.CUISINES);
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.NEW_DISH_KITCHEN;
    }
}
