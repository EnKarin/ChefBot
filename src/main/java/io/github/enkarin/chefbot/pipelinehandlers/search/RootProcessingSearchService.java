package io.github.enkarin.chefbot.pipelinehandlers.search;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.pipelinehandlers.NonCommandInputHandler;
import org.springframework.stereotype.Service;

@Service
public class RootProcessingSearchService implements NonCommandInputHandler {

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        return new ExecutionResult(switch (text) {
            case "По остроте" -> ChatStatus.SELECT_DISH_SPICY;
            case "По виду кухни" -> ChatStatus.SELECT_DISH_KITCHEN;
            case "Искать" -> ChatStatus.SELECT_DISH_PUBLISHED;
            default -> ChatStatus.ROOT_SELECT_DISH;
        });
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return new BotAnswer("Выберите дополнительные параметры поиска", "По остроте", "По виду кухни", "Искать");
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.ROOT_SELECT_DISH;
    }
}
