package io.github.enkarin.chefbot.pipelinehandlers.searchbyproduct;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.DisplayDishDto;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.StandardUserAnswerOption;
import io.github.enkarin.chefbot.exceptions.DishesNotFoundException;
import io.github.enkarin.chefbot.pipelinehandlers.NonCommandInputHandler;
import io.github.enkarin.chefbot.service.SearchProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FindDishByProductsResponseService implements NonCommandInputHandler {
    private final SearchProductService searchProductService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        if ("Вернуться в главное меню".equalsIgnoreCase(text)) {
            searchProductService.dropSearchProductForUser(userId);
            return new ExecutionResult(ChatStatus.MAIN_MENU);
        } else {
            return new ExecutionResult(getCurrentStatus());
        }
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        final List<? extends DisplayDishDto> dishByProduct = searchProductService.findDishByProduct(userId);
        if (dishByProduct.isEmpty()) {
            throw new DishesNotFoundException();
        } else {
            return new BotAnswer(dishByProduct.stream().map(DisplayDishDto::toString).collect(Collectors.joining("\n\n")), StandardUserAnswerOption.MORE_OR_STOP);
        }
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.FIND_DISH_BY_PRODUCTS_RESPONSE;
    }
}
