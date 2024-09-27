package io.github.enkarin.chefbot.pipelinehandlers;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.StandardUserAnswerOption;
import io.github.enkarin.chefbot.service.SearchFilterService;
import io.github.enkarin.chefbot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ApproveBackToMainMenuService implements NonCommandInputHandler {
    private final UserService userService;
    private final SearchFilterService searchFilterService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        return switch (text.toLowerCase(Locale.ROOT)) {
            case "да" -> {
                searchFilterService.deleteSearchFilter(userId);
                yield new ExecutionResult(ChatStatus.MAIN_MENU);
            }
            case "нет" -> new ExecutionResult(userService.getPreviousChatStatus(userId));
            default -> new ExecutionResult(getCurrentStatus());
        };
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return new BotAnswer("Вы хотите вернуться в главное меню?", StandardUserAnswerOption.YES_OR_NO);
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.APPROVE_BACK_TO_MAIN_MENU;
    }
}
