package io.github.enkarin.chefbot.pipelinehandlers.edit;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.pipelinehandlers.NonCommandInputHandler;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class SelectEditingFieldService implements NonCommandInputHandler {
    @Override
    public ExecutionResult execute(final long userId, final String text) {
        return new ExecutionResult(switch (text.toLowerCase(Locale.ROOT)) {
            case "название" -> ChatStatus.EDITING_NAME;
            case "острота" -> ChatStatus.EDITING_SPICY;
            case "тип" -> ChatStatus.EDITING_TYPE;
            case "кухня" -> ChatStatus.EDITING_KITCHEN;
            case "список продуктов" -> ChatStatus.EDITING_FOODSTUFF;
            case "рецепт" -> ChatStatus.EDITING_RECIPE;
            default -> getCurrentStatus();
        });
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return new BotAnswer("Укажите поле, которое вы хотите отредактировать",
                "Название", "Острота", "Тип", "Кухня", "Список продуктов", "Рецепт");
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.SELECT_EDITING_FIELD;
    }
}
