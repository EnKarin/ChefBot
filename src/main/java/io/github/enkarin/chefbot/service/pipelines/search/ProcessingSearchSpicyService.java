package io.github.enkarin.chefbot.service.pipelines.search;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.StandardUserAnswerOption;
import io.github.enkarin.chefbot.service.SearchFilterService;
import io.github.enkarin.chefbot.service.pipelines.ProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProcessingSearchSpicyService implements ProcessingService {

    private final SearchFilterService filterService;

    @Override
    public ChatStatus execute(final long userId, final String text) {
        return switch (text.toLowerCase(Locale.ROOT)) {
            case "да" -> {
                filterService.putSpicySign(userId, true);
                yield ChatStatus.SELECT_DISH_KITCHEN;
            }
            case "нет" -> {
                filterService.putSpicySign(userId, false);
                yield ChatStatus.SELECT_DISH_KITCHEN;
            }
            case "любое" -> ChatStatus.SELECT_DISH_KITCHEN;
            default -> getCurrentStatus();
        };
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return new BotAnswer("Острое блюдо?", StandardUserAnswerOption.YES_NO_OR_ANY);
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.SELECT_DISH_SPICY;
    }
}
