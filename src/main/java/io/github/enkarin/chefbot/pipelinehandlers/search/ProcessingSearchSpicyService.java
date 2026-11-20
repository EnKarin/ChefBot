package io.github.enkarin.chefbot.pipelinehandlers.search;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.StandardUserAnswerOption;
import io.github.enkarin.chefbot.pipelinehandlers.NonCommandInputHandler;
import io.github.enkarin.chefbot.service.SearchFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProcessingSearchSpicyService implements NonCommandInputHandler {

    private final SearchFilterService filterService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        return new ExecutionResult(switch (text.toLowerCase(Locale.ROOT)) {
            case "да" -> {
                filterService.putSpicySign(userId, true);
                yield ChatStatus.ROOT_SELECT_DISH;
            }
            case "нет" -> {
                filterService.putSpicySign(userId, false);
                yield ChatStatus.ROOT_SELECT_DISH;
            }
            case "любое" -> ChatStatus.ROOT_SELECT_DISH;
            default -> getCurrentStatus();
        });
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
