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
        return switch (text.toLowerCase(Locale.ROOT)) {
            case "да" -> {
                filterService.putNeedPublicSearch(userId, true);
                yield ChatStatus.EXECUTE_SEARCH;
            }
            case "нет" -> {
                filterService.putNeedPublicSearch(userId, false);
                yield ChatStatus.EXECUTE_SEARCH;
            }
            default -> getCurrentStatus();
        };
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return BotAnswer.builder()
                .userAnswerOption(UserAnswerOption.YES_OR_NO)
                .messageText("Включить блюда других пользователей при поиске?")
                .build();
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.SELECT_DISH_PUBLISHED;
    }
}
