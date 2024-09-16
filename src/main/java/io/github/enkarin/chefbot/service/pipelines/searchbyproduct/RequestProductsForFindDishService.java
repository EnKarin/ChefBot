package io.github.enkarin.chefbot.service.pipelines.searchbyproduct;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.service.pipelines.ProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestProductsForFindDishService implements ProcessingService {

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        return new ExecutionResult(ChatStatus.MAIN_MENU); //todo: logic for save products
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return BotAnswer.createBotAnswerWithoutKeyboard("Введите список продуктов, которые должно содержать желаемое блюдо");
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.REQUEST_PRODUCTS_FOR_FIND_DISH;
    }
}
