package io.github.enkarin.chefbot.pipelinehandlers.searchbyname;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.pipelinehandlers.NonCommandInputHandler;
import io.github.enkarin.chefbot.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindDishByNameService implements NonCommandInputHandler {
    private final DishService dishService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        return new ExecutionResult(ChatStatus.MAIN_MENU, dishService.findDishByName(userId, text));
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return new BotAnswer("Введите название искомого блюда");
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.FIND_DISH_BY_NAME;
    }
}
