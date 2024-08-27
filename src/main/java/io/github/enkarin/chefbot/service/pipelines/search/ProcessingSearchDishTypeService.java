package io.github.enkarin.chefbot.service.pipelines.search;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.DishType;
import io.github.enkarin.chefbot.enums.UserAnswerOption;
import io.github.enkarin.chefbot.service.SearchFilterService;
import io.github.enkarin.chefbot.service.pipelines.ProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessingSearchDishTypeService implements ProcessingService {

    private final SearchFilterService filterService;

    @Override
    public ChatStatus execute(final long userId, final String text) {
        filterService.createSearchFilter(userId);
        try {
            filterService.putDishType(userId, DishType.parse(text));
            return ChatStatus.SELECT_DISH_SPICY;
        } catch (IllegalArgumentException e) {
            return ChatStatus.SELECT_DISH_SPICY;
        }
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return new BotAnswer("Выберете тип искомого блюда", UserAnswerOption.DISH_TYPES_WITH_ANY_CASE);
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.SELECT_DISH_TYPE;
    }
}