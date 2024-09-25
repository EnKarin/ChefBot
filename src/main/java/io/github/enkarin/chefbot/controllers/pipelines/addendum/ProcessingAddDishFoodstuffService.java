package io.github.enkarin.chefbot.controllers.pipelines.addendum;

import io.github.enkarin.chefbot.controllers.pipelines.ProcessingService;
import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.service.DishService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessingAddDishFoodstuffService implements ProcessingService {
    private final DishService dishService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        if (StringUtils.isNoneBlank(text)) {
            final String[] foodstuffs = text.split("[,\n]");
            dishService.putDishFoodstuff(userId, foodstuffs);
            return new ExecutionResult(ChatStatus.GET_NEED_DISH_RECIPE);
        }
        return new ExecutionResult(getCurrentStatus());
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return BotAnswer.createBotAnswerWithoutKeyboard("Введите список продуктов для приготовления блюда одним сообщением.\nОтделяйте их запятой или новой строкой.");
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.NEW_DISH_FOODSTUFF;
    }
}
