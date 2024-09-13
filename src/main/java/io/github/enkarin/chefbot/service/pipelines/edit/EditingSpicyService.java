package io.github.enkarin.chefbot.service.pipelines.edit;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.StandardUserAnswerOption;
import io.github.enkarin.chefbot.service.DishService;
import io.github.enkarin.chefbot.service.pipelines.ProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EditingSpicyService implements ProcessingService {
    private final DishService dishService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        return switch (text.toLowerCase(Locale.ROOT)) {
            case "да" -> {
                dishService.putDishIsSpicy(userId);
                dishService.dropPublishFlagForEditableDish(userId);
                yield new ExecutionResult(ChatStatus.DISH_NEED_PUBLISH);
            }
            case "нет" -> {
                dishService.putDishIsNotSpicy(userId);
                dishService.dropPublishFlagForEditableDish(userId);
                yield new ExecutionResult(ChatStatus.DISH_NEED_PUBLISH);
            }
            default -> new ExecutionResult(getCurrentStatus());
        };
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return new BotAnswer("Блюдо острое?", StandardUserAnswerOption.YES_OR_NO);
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.EDITING_SPICY;
    }
}
