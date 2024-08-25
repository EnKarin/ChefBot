package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.UserAnswerOption;
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
            default -> getCurrentStatus();
        };
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return BotAnswer.builder()
                .userAnswerOption(UserAnswerOption.YES_OR_NO)
                .messageText("Острое блюдо?")
                .build();
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.SELECT_DISH_SPICY;
    }
}
