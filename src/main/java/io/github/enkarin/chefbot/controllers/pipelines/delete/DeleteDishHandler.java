package io.github.enkarin.chefbot.controllers.pipelines.delete;

import io.github.enkarin.chefbot.controllers.pipelines.NonCommandInputHandler;
import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteDishHandler implements NonCommandInputHandler {
    private final DishService dishService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        dishService.deleteDish(userId, text);
        return new ExecutionResult(ChatStatus.MAIN_MENU);
    }

    @Override
    public BotAnswer getMessageForUser(long userId) {
        return new BotAnswer("Введите название удаляемого блюда. Оно будет удалено безвозвратно!");
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.DELETE_DISH;
    }
}
