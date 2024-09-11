package io.github.enkarin.chefbot.service.pipelines.search;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.DisplayDishDto;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.StandardUserAnswerOption;
import io.github.enkarin.chefbot.exceptions.DishesNotFoundException;
import io.github.enkarin.chefbot.service.SearchFilterService;
import io.github.enkarin.chefbot.service.UserService;
import io.github.enkarin.chefbot.service.pipelines.ProcessingService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProcessingExecuteSearchService implements ProcessingService {

    private final SearchFilterService searchFilterService;
    private final UserService userService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        if ("вернуться в главное меню".equalsIgnoreCase(text)) {
            searchFilterService.deleteSearchFilter(userId);
            userService.switchToNewStatus(userId, ChatStatus.MAIN_MENU);
            return new ExecutionResult(ChatStatus.MAIN_MENU);
        }
        return new ExecutionResult(getCurrentStatus());
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        final String dishes = searchFilterService.searchDishWithCurrentFilter(userId).stream()
                .map(DisplayDishDto::toString)
                .collect(Collectors.joining("\n\n"));
        if (StringUtils.isEmpty(dishes)) {
            throw new DishesNotFoundException();
        }
        return new BotAnswer(dishes, StandardUserAnswerOption.MORE_OR_STOP);
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.EXECUTE_SEARCH;
    }
}
