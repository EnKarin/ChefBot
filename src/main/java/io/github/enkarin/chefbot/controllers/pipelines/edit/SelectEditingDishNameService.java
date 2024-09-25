package io.github.enkarin.chefbot.controllers.pipelines.edit;

import io.github.enkarin.chefbot.controllers.pipelines.NonCommandInputHandler;
import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SelectEditingDishNameService implements NonCommandInputHandler {
    private final DishService dishService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        dishService.putEditableDish(userId, text);
        return new ExecutionResult(ChatStatus.SELECT_EDITING_FIELD);
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return new BotAnswer("Введите название блюда, которое вы хотите отредактировать");
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.SELECT_EDITING_DISH_NAME;
    }
}
