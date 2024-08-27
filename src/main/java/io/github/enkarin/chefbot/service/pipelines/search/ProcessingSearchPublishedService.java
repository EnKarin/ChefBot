package io.github.enkarin.chefbot.service.pipelines.search;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.UserAnswerOption;
import io.github.enkarin.chefbot.service.SearchFilterService;
import io.github.enkarin.chefbot.service.pipelines.ProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProcessingSearchPublishedService implements ProcessingService {

    private final SearchFilterService filterService;

    @Override
    public ChatStatus execute(final long userId, final String text) {
        return switch (text.toLowerCase(Locale.ROOT).trim()) {
            case "все блюда" -> {
                filterService.putNeedPublicSearch(userId, true);
                yield ChatStatus.EXECUTE_SEARCH;
            }
            case "все личные блюда" -> {
                filterService.putNeedPublicSearch(userId, false);
                yield ChatStatus.EXECUTE_SEARCH;
            }
            case "случайное личное блюдо" -> {
                filterService.putNeedPublicSearch(userId, false);
                yield ChatStatus.EXECUTE_RANDOM_SEARCH;
            }
            case "случайное блюдо" -> {
                filterService.putNeedPublicSearch(userId, true);
                yield ChatStatus.EXECUTE_RANDOM_SEARCH;
            }
            default -> getCurrentStatus();
        };
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return new BotAnswer("Выберите режим поиска", UserAnswerOption.SEARCH_DISH_OPTIONS);
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.SELECT_DISH_PUBLISHED;
    }
}
