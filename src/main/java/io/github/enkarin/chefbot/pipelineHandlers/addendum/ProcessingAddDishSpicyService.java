package io.github.enkarin.chefbot.pipelineHandlers.addendum;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.StandardUserAnswerOption;
import io.github.enkarin.chefbot.pipelineHandlers.NonCommandInputHandler;
import io.github.enkarin.chefbot.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProcessingAddDishSpicyService implements NonCommandInputHandler {

    private final DishService dishService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        return switch (text.toLowerCase(Locale.ROOT)) {
            case "да" -> {
                dishService.putDishIsSpicy(userId);
                yield new ExecutionResult(ChatStatus.NEW_DISH_KITCHEN);
            }
            case "нет" -> new ExecutionResult(ChatStatus.NEW_DISH_KITCHEN);
            default -> new ExecutionResult(getCurrentStatus());
        };
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return new BotAnswer("Блюдо острое?", StandardUserAnswerOption.YES_OR_NO);
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.NEW_DISH_SPICY;
    }
}
