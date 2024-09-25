package io.github.enkarin.chefbot.controllers.pipelines.addendum;

import io.github.enkarin.chefbot.controllers.pipelines.ProcessingService;
import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessingAddDishNameService implements ProcessingService {

    private final DishService dishService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        dishService.initDishName(userId, text);
        return new ExecutionResult(ChatStatus.NEW_DISH_TYPE);
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return BotAnswer.createBotAnswerWithoutKeyboard("Введите название блюда");
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.NEW_DISH_NAME;
    }
}
