package io.github.enkarin.chefbot.pipelinehandlers.exclude;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.pipelinehandlers.NonCommandInputHandler;
import io.github.enkarin.chefbot.service.ExcludeUserProductsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class ShowExcludeProductsInputHandler implements NonCommandInputHandler {
    private final ExcludeUserProductsService service;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        return new ExecutionResult(switch (text.toLowerCase(Locale.ROOT)) {
            case "в главное меню" -> ChatStatus.MAIN_MENU;
            case "добавить продукты в список" -> ChatStatus.ADD_EXCLUDE_PRODUCTS;
            case "удалить продукты из списка по полному названию" -> ChatStatus.DELETE_EXCLUDE_PRODUCTS_BY_NAME;
            case "удалить продукты из списка по частичному названию" -> ChatStatus.DELETE_EXCLUDE_PRODUCTS_CONTAINS_NAME;
            default -> getCurrentStatus();
        });
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        final String[] productsName = service.findExcludeProducts(userId);
        return productsName.length > 0
                ? new BotAnswer(String.format("Список продуктов, блюда с которыми будут исключены из поиска:\n-%s\n\nЖелаете его изменить?",
                String.join("\n-", productsName)),
                "В главное меню", "Добавить продукты в список", "Удалить продукты из списка по полному названию", "Удалить продукты из списка по частичному названию")
                : new BotAnswer("У вас нет продуктов, блюда с которыми будут исключены из поиска. Хотите добавить?",
                "В главное меню", "Добавить продукты в список");
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.EXCLUDE_PRODUCTS;
    }
}
