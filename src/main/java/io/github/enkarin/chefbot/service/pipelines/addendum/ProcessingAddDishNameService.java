package io.github.enkarin.chefbot.service.pipelines.addendum;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.service.DishService;
import io.github.enkarin.chefbot.service.pipelines.ProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static org.apache.commons.lang3.StringUtils.isNoneBlank;

@Service
@RequiredArgsConstructor
public class ProcessingAddDishNameService implements ProcessingService {

    private final DishService dishService;

    @Override
    public ChatStatus execute(final long userId, final String text) {
        if (isNoneBlank(text)) {
            dishService.initDishName(userId, text);
            return ChatStatus.NEW_DISH_TYPE;
        }

        return getCurrentStatus();
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return BotAnswer.createBotAnswerWithoutKeyboard("Введите название блюда");
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.NEW_DISH_NAME;
    }
}
