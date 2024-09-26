package io.github.enkarin.chefbot.controllers.pipelines.search;

import io.github.enkarin.chefbot.controllers.pipelines.NonCommandInputHandler;
import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.StandardUserAnswerOption;
import io.github.enkarin.chefbot.service.SearchFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProcessingSearchPublishedService implements NonCommandInputHandler {
    private final SearchFilterService filterService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        filterService.dropPageNumberValue(userId);
        return switch (text.toLowerCase(Locale.ROOT).trim()) {
            case "все блюда" -> {
                filterService.putNeedPublicSearch(userId, true);
                yield new ExecutionResult(ChatStatus.EXECUTE_SEARCH);
            }
            case "все личные блюда" -> {
                filterService.putNeedPublicSearch(userId, false);
                yield new ExecutionResult(ChatStatus.EXECUTE_SEARCH);
            }
            case "случайное личное блюдо" -> {
                filterService.putNeedPublicSearch(userId, false);
                yield new ExecutionResult(ChatStatus.EXECUTE_RANDOM_SEARCH);
            }
            case "случайное блюдо" -> {
                filterService.putNeedPublicSearch(userId, true);
                yield new ExecutionResult(ChatStatus.EXECUTE_RANDOM_SEARCH);
            }
            default -> new ExecutionResult(getCurrentStatus());
        };
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return new BotAnswer("Выберите режим поиска", StandardUserAnswerOption.SEARCH_DISH_OPTIONS);
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.SELECT_DISH_PUBLISHED;
    }
}
