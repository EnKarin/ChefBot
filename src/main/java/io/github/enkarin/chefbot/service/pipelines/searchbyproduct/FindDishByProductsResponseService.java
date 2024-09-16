package io.github.enkarin.chefbot.service.pipelines.searchbyproduct;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.service.pipelines.ProcessingService;
import org.springframework.stereotype.Service;

@Service
public class FindDishByProductsResponseService implements ProcessingService {
    @Override
    public ExecutionResult execute(final long userId, final String text) {
        return null;
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return null;
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.FIND_DISH_BY_PRODUCTS_RESPONSE;
    }
}
