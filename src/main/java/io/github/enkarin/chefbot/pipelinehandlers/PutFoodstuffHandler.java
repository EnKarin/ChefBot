package io.github.enkarin.chefbot.pipelinehandlers;

import io.github.enkarin.chefbot.dto.BotAnswer;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class PutFoodstuffHandler implements NonCommandInputHandler {

    protected Map<String, String> parseTextToProductMap(final String text) {
        return Arrays.stream(text.split("[,\n]"))
                .map(product -> product.split("[-:]"))
                .collect(Collectors.toMap(splitProduct -> splitProduct[0], splitProduct -> splitProduct.length == 1 ? "" : splitProduct[1]));
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return BotAnswer.createBotAnswerWithoutKeyboard("""
                Введите список продуктов для приготовления блюда одним сообщением.
                Отделяйте их запятой или новой строкой.
                Количество продукта нужно написать после его названия, отделив двоеточием или тире.""");
    }
}
