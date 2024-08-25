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
public class ProcessingSearchSoupService implements ProcessingService {

    private final SearchFilterService filterService;

    @Override
    public ChatStatus execute(final long userId, final String text) {
        return switch (text.toLowerCase(Locale.ROOT)) {
            case "да" -> {
                filterService.createSearchFilter(userId);
                filterService.putSoupSign(userId, true);
                yield ChatStatus.SELECT_DISH_SPICY;
            }
            case "нет" -> {
                filterService.createSearchFilter(userId);
                filterService.putSoupSign(userId, false);
                yield ChatStatus.SELECT_DISH_SPICY;
            }
            default -> getCurrentStatus();
        };
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return BotAnswer.builder()
                .userAnswerOption(UserAnswerOption.YES_OR_NO)
                .messageText("Вы хотите суп?")
                .build();
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.SELECT_DISH_SOUP;
    }
}
