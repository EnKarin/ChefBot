package io.github.enkarin.chefbot.pipelinehandlers.exclude;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.pipelinehandlers.NonCommandInputHandler;
import io.github.enkarin.chefbot.service.ExcludeUserProductsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteExcludeProductsContainsNameInputHandler implements NonCommandInputHandler {
    private final ExcludeUserProductsService service;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        service.deleteExcludeProductsByLikeName(userId, text.split("[\n,]"));
        return new ExecutionResult(ChatStatus.EXCLUDE_PRODUCTS);
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return BotAnswer.createBotAnswerWithoutKeyboard("Введите названия продуктов, которые хотите исключить из списка, через запятую или с новой строки");
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.DELETE_EXCLUDE_PRODUCTS_CONTAINS_NAME;
    }
}
