package io.github.enkarin.chefbot.controllers.pipelines.search;

import io.github.enkarin.chefbot.controllers.pipelines.NonCommandInputHandler;
import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.StandardUserAnswerOption;
import io.github.enkarin.chefbot.service.SearchFilterService;
import io.github.enkarin.chefbot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExecuteRandomSearchService implements NonCommandInputHandler {
    private final SearchFilterService searchFilterService;
    private final UserService userService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        if ("вернуться в главное меню".equalsIgnoreCase(text)) {
            searchFilterService.deleteSearchFilter(userId);
            userService.switchToNewStatus(userId, ChatStatus.MAIN_MENU);
            return new ExecutionResult(ChatStatus.MAIN_MENU);
        } else {
            return new ExecutionResult(getCurrentStatus());
        }
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return new BotAnswer(searchFilterService.searchRandomDishWithCurrentFilter(userId).toString(), StandardUserAnswerOption.MORE_OR_STOP);
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.EXECUTE_RANDOM_SEARCH;
    }
}
