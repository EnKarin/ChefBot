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
public class AddExcludeProductInputHandler implements NonCommandInputHandler {
    private final ExcludeUserProductsService service;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        service.addExcludeProducts(userId, text.split("[\n,]"));
        return new ExecutionResult(ChatStatus.MAIN_MENU);
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return BotAnswer.createBotAnswerWithoutKeyboard("Введите названия продуктов, которые хотите добавить в список, через запятую или с новой строки");
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.ADD_EXCLUDE_PRODUCTS;
    }
}
