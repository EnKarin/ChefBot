package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.DisplayDishDto;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.UserAnswerOption;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProcessingExecuteSearchService implements ProcessingService {

    private final SearchFilterService searchFilterService;
    private final UserService userService;

    @Override
    public ChatStatus execute(final long userId, final String text) {
        if ("вернуться в главное меню".equals(text.toLowerCase(Locale.ROOT))) {
            searchFilterService.deleteSearchFilter(userId);
            userService.switchToNewStatus(userId, ChatStatus.MAIN_MENU);

            return ChatStatus.MAIN_MENU;
        }

        return getCurrentStatus();
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        final String dishes = searchFilterService.searchDishWithCurrentFilter(userId).stream()
                .map(DisplayDishDto::toString)
                .collect(Collectors.joining("\n\n"));

        return BotAnswer.builder()
                .userAnswerOption(UserAnswerOption.MORE_OR_STOP)
                .messageText(StringUtils.isNoneBlank(dishes) ? dishes : "Подходщих блюд нет")
                .build();
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.EXECUTE_SEARCH;
    }
}
