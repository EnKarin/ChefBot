package io.github.enkarin.chefbot.service.pipelines.search;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.UserAnswerOption;
import io.github.enkarin.chefbot.service.SearchFilterService;
import io.github.enkarin.chefbot.service.UserService;
import io.github.enkarin.chefbot.service.pipelines.ProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExecuteRandomSearchService implements ProcessingService {
    private final SearchFilterService searchFilterService;
    private final UserService userService;

    @Override
    public ChatStatus execute(final long userId, final String text) {
        if ("вернуться в главное меню".equalsIgnoreCase(text)) {
            searchFilterService.deleteSearchFilter(userId);
            userService.switchToNewStatus(userId, ChatStatus.MAIN_MENU);
            return ChatStatus.MAIN_MENU;
        } else {
            return getCurrentStatus();
        }
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return new BotAnswer(searchFilterService.searchRandomDishWithCurrentFilter(userId).toString(), UserAnswerOption.MORE_OR_STOP);
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.EXECUTE_RANDOM_SEARCH;
    }
}
