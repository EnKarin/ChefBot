package io.github.enkarin.chefbot.controllers.pipelines.searchbyproduct;

import io.github.enkarin.chefbot.controllers.pipelines.NonCommandInputHandler;
import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.DisplayDishDto;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.StandardUserAnswerOption;
import io.github.enkarin.chefbot.exceptions.DishesNotFoundException;
import io.github.enkarin.chefbot.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindDishByProductsResponseService implements NonCommandInputHandler {
    private final DishService dishService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        if ("Вернуться в главное меню".equalsIgnoreCase(text)) {
            return new ExecutionResult(ChatStatus.MAIN_MENU);
        } else {
            return new ExecutionResult(getCurrentStatus());
        }
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return new BotAnswer(dishService.findDishByProduct(userId).stream().map(DisplayDishDto::toString).reduce(String::concat).orElseThrow(DishesNotFoundException::new),
                StandardUserAnswerOption.MORE_OR_STOP);
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.FIND_DISH_BY_PRODUCTS_RESPONSE;
    }
}
