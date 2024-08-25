package io.github.enkarin.chefbot.service.pipelines.addendum;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.UserAnswerOption;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import io.github.enkarin.chefbot.service.DishService;
import io.github.enkarin.chefbot.service.pipelines.ProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessingAddDishCuisineService implements ProcessingService {

    private final DishService dishService;

    @Override
    public ChatStatus execute(final long userId, final String text) {
        try {
            final WorldCuisine cuisine = WorldCuisine.getCuisine(text);
            dishService.putDishCuisine(userId, cuisine);
            return ChatStatus.NEW_DISH_FOODSTUFF;
        } catch (IllegalArgumentException e) {
            return getCurrentStatus();
        }
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return BotAnswer.builder()
                .userAnswerOption(UserAnswerOption.CUISINES)
                .messageText("Блюдо какой кухни вы добавляете?")
                .build();
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.NEW_DISH_KITCHEN;
    }
}
