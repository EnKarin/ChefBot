package io.github.enkarin.chefbot.controllers.pipelines.addendum;

import io.github.enkarin.chefbot.controllers.pipelines.NonCommandInputHandler;
import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.DishType;
import io.github.enkarin.chefbot.enums.StandardUserAnswerOption;
import io.github.enkarin.chefbot.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessingAddDishTypeService implements NonCommandInputHandler {

    private final DishService dishService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        try {
            dishService.putDishType(userId, DishType.parse(text));
            return new ExecutionResult(ChatStatus.NEW_DISH_SPICY);
        } catch (IllegalArgumentException e) {
            return new ExecutionResult(getCurrentStatus());
        }
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return new BotAnswer("Выберете тип блюда", StandardUserAnswerOption.DISH_TYPES);
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.NEW_DISH_TYPE;
    }
}
