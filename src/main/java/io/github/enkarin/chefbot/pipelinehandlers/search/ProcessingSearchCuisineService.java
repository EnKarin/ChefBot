package io.github.enkarin.chefbot.pipelinehandlers.search;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.StandardUserAnswerOption;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import io.github.enkarin.chefbot.pipelinehandlers.NonCommandInputHandler;
import io.github.enkarin.chefbot.service.SearchFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessingSearchCuisineService implements NonCommandInputHandler {

    private final SearchFilterService filterService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        try {
            final WorldCuisine cuisine = WorldCuisine.getCuisine(text);
            filterService.putKitchen(userId, cuisine);
            return new ExecutionResult(ChatStatus.SELECT_DISH_PUBLISHED);
        } catch (IllegalArgumentException e) {
            return new ExecutionResult(ChatStatus.SELECT_DISH_PUBLISHED);
        }
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return new BotAnswer("Выберите кухню мира:", StandardUserAnswerOption.CUISINES_WITH_ANY_CASE);
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.SELECT_DISH_KITCHEN;
    }
}
