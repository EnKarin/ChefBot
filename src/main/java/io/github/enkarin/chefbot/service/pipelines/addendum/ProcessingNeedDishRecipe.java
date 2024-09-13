package io.github.enkarin.chefbot.service.pipelines.addendum;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.StandardUserAnswerOption;
import io.github.enkarin.chefbot.service.pipelines.ProcessingService;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class ProcessingNeedDishRecipe implements ProcessingService {
    @Override
    public ExecutionResult execute(final long userId, final String text) {
        return switch (text.toLowerCase(Locale.ROOT)) {
            case "да" -> new ExecutionResult(ChatStatus.NEW_DISH_RECIPE);
            case "нет" -> new ExecutionResult(ChatStatus.DISH_NEED_PUBLISH);
            default -> new ExecutionResult(getCurrentStatus());
        };
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return new BotAnswer("Желаете добавить рецепт приготовления блюда?", StandardUserAnswerOption.YES_OR_NO);
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.GET_NEED_DISH_RECIPE;
    }
}
