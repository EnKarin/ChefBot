package io.github.enkarin.chefbot.service.pipelines.search;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.UserAnswerOption;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import io.github.enkarin.chefbot.service.SearchFilterService;
import io.github.enkarin.chefbot.service.pipelines.ProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessingSearchCuisineService implements ProcessingService {

    private final SearchFilterService filterService;

    @Override
    public ChatStatus execute(final long userId, final String text) {
        try {
            final WorldCuisine cuisine = WorldCuisine.getCuisine(text);
            filterService.putKitchen(userId, cuisine);
            return ChatStatus.SELECT_DISH_PUBLISHED;
        } catch (IllegalArgumentException e) {
            return ChatStatus.SELECT_DISH_PUBLISHED;
        }
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return BotAnswer.builder()
                .userAnswerOption(UserAnswerOption.CUISINES_WITH_ANY_CASE)
                .messageText("Выберите кухню мира:")
                .build();
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.SELECT_DISH_KITCHEN;
    }
}
