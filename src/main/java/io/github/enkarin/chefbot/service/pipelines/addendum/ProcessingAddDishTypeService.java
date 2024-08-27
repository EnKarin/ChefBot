package io.github.enkarin.chefbot.service.pipelines.addendum;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.DishType;
import io.github.enkarin.chefbot.enums.UserAnswerOption;
import io.github.enkarin.chefbot.service.DishService;
import io.github.enkarin.chefbot.service.pipelines.ProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessingAddDishTypeService implements ProcessingService {

    private final DishService dishService;

    @Override
    public ChatStatus execute(final long userId, final String text) {
        try {
            dishService.putDishType(userId, DishType.parse(text));
            return ChatStatus.NEW_DISH_SPICY;
        } catch (IllegalArgumentException e) {
            return getCurrentStatus();
        }
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return new BotAnswer("Выберете тип блюда", UserAnswerOption.DISH_TYPES);
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.NEW_DISH_TYPE;
    }
}
