package io.github.enkarin.chefbot.service.pipelines.addendum;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.service.DishService;
import io.github.enkarin.chefbot.service.pipelines.ProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddNewDishRecipe implements ProcessingService {
    private final DishService dishService;

    @Override
    public ChatStatus execute(final long userId, final String text) {
        dishService.putDishRecipe(userId, text);
        return ChatStatus.NEW_DISH_NEED_PUBLISH;
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return BotAnswer.createBotAnswerWithoutKeyboard("Введите способ приготовления блюда");
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.NEW_DISH_RECIPE;
    }
}
